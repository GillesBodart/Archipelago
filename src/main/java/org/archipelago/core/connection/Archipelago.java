package org.archipelago.core.connection;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.archipelago.core.builder.ArchipelagoScriptBuilder;
import org.archipelago.core.builder.Neo4JBuilder;
import org.archipelago.core.configurator.ArchipelagoConfig;
import org.archipelago.core.configurator.DatabaseConfig;
import org.archipelago.core.exception.CheckException;
import org.neo4j.driver.v1.*;

import java.io.File;
import java.io.IOException;

import static org.archipelago.core.configurator.DatabaseType.NEO4J;
import static org.neo4j.driver.v1.Values.parameters;

/**
 * @author Gilles Bodart
 */
public class Archipelago implements AutoCloseable {

    public static final String CONFIG_LOCATION = "config.archipelago.json";
    public static final Logger LOGGER = LogManager.getLogger();

    private static Archipelago instance;

    private final ArchipelagoConfig archipelagoConfig;
    private Driver driver;
    private ArchipelagoScriptBuilder builder;

    private Archipelago() throws CheckException {
        // Initialization of the configuration
        this.archipelagoConfig = getConfig();
        DatabaseConfig dc = archipelagoConfig.getDatabase();

        // Personalisation of the connection
        if (NEO4J.equals(dc.getType()) && !dc.getEmbedded()) {
            this.driver = GraphDatabase.driver(
                    String.format("bolt://%s:%d", dc.getUrl(), dc.getPort()),
                    AuthTokens.basic(dc.getUsername(), dc.getPassword()));
            this.builder = new Neo4JBuilder();
        }
    }

    public static Archipelago getInstance() throws CheckException {
        if (null == instance) {
            instance = new Archipelago();
        }
        return instance;
    }

    private ArchipelagoConfig getConfig() throws CheckException {
        ArchipelagoConfig ac = null;
        ObjectMapper om = new ObjectMapper();
        try {
            ac = om.readValue(new File(getClass().getClassLoader().getResource(CONFIG_LOCATION).getFile()),
                    ArchipelagoConfig.class);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new CheckException("The config file have not been found, please have a 'config.archipelago.json' in your resource folder", e);
        }
        return ac;
    }

    /**
     * Persist an object in the database
     *
     * @param object the object to persist
     * @return the internal Id of the object (null in case or error)
     */
    public Integer persist(Object object) {
        Integer id = null;
        try (Session s = this.driver.session()) {
            s.run(builder.makeCreate(object.getClass()),
                    parameters(builder.fillCreate(object).toArray()));
            id = getId(object);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        return id;
    }


    public void link(Object first, Object second, boolean biDirectional) {
        link(first, second, null, biDirectional);
    }

    public void link(Object first, Object second, Object descriptor, boolean biDirectional) {
        boolean haveDescriptor = null != descriptor;
        try (Session s = this.driver.session()) {
            int idA = getId(first);
            int idB = getId(second);
            String name;
            if (haveDescriptor) {
                name = descriptor.getClass().getSimpleName();
            } else {
                name = String.format("%s_%s", first.getClass().getSimpleName(), second.getClass().getSimpleName());
            }
            if (haveDescriptor) {
                s.run(builder.makeRelation(idA, idB, name, descriptor.getClass()), parameters(builder.fillCreate(descriptor).toArray()));
                if (biDirectional) {
                    s.run(builder.makeRelation(idB, idA, name, descriptor.getClass()), parameters(builder.fillCreate(descriptor).toArray()));
                }
            } else {
                s.run(builder.makeRelation(idA, idB, name, null));
                if (biDirectional) {
                    s.run(builder.makeRelation(idB, idA, name, null));
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Persits an object in the database
     *
     * @param object the object to persist
     * @return the internal Id of the object
     */
    public Integer getId(Object object) {
        Integer id = null;
        try (Session s = this.driver.session()) {
            String stmt = builder.makeMatch(object.getClass(), false);
            Object[] params = builder.fillCreate(object).toArray();
            StatementResult result = s.run(stmt, parameters(params));
            while (result.hasNext()) {
                Record record = result.next();
                id = record.get("InternalId").asInt();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        return id;
    }


    public Session getSession() {
        return driver.session();
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }
}
