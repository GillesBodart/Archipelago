package org.archipelago.core.connection;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.archipelago.core.builder.ArchipelagoQuery;
import org.archipelago.core.builder.Neo4JQueryImpl;
import org.archipelago.core.builder.OrientDBQueryImpl;
import org.archipelago.core.builder.QueryBuilder;
import org.archipelago.core.builder.old.ArchipelagoScriptBuilder;
import org.archipelago.core.builder.old.Neo4JBuilder;
import org.archipelago.core.builder.old.OrientDBBuilder;
import org.archipelago.core.configurator.ArchipelagoConfig;
import org.archipelago.core.configurator.DatabaseConfig;
import org.archipelago.core.configurator.DatabaseType;
import org.archipelago.core.exception.CheckException;
import org.archipelago.core.util.ArchipelagoUtils;
import org.neo4j.driver.v1.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.neo4j.driver.v1.Values.parameters;

/**
 * @author Gilles Bodart
 */
public class Archipelago implements AutoCloseable {

    public static final String CONFIG_LOCATION = "config.archipelago.json";
    public static final String ARCHIPELAGO_ID = "ArchipelagoId";

    public static final Logger LOGGER = LogManager.getLogger();

    private static Archipelago instance;

    private final ArchipelagoConfig archipelagoConfig;
    private DatabaseType databaseType;

    private Driver driver;
    private ArchipelagoScriptBuilder builder;

    private Client jerseyClient;
    private WebTarget rootTarget;

    private Archipelago() throws CheckException {
        // Initialization of the configuration
        this.archipelagoConfig = getConfig();
        DatabaseConfig dc = archipelagoConfig.getDatabase();
        // Personalisation of the connection

        this.databaseType = dc.getType();
        switch (dc.getType()) {
            case NEO4J:
                if (!dc.getEmbedded()) {
                    String url = String.format("bolt://%s:%d", dc.getUrl(), dc.getPort());
                    LOGGER.info("Connecting to Neo4J remote server on " + url);
                    this.driver = GraphDatabase.driver(url, AuthTokens.basic(dc.getUsername(), dc.getPassword()));
                    LOGGER.info("Connected to Neo4J remote server");
                    this.builder = new Neo4JBuilder();
                } else {
                    //TODO EMBEDDED Neo4J
                }
                break;
            case ORIENT_DB:
                this.jerseyClient = ClientBuilder.newClient();
                String url = String.format("http://%s:%d/", dc.getUrl(), dc.getPort());
                LOGGER.info("Connecting to Orient remote server on " + url);
                this.rootTarget = jerseyClient.target(url);
                this.builder = new OrientDBBuilder();
                Response response = this.rootTarget.path(String.format("connect/%s", dc.getName())).request(MediaType.APPLICATION_JSON).get();
                if (response.getStatus() != 204) {
                    throw new CheckException("Unable to connect to Orient Remote server");
                } else {
                    LOGGER.info("Connected to Orient remote server");
                }
                break;
            default:
                throw new CheckException("Only NEO4J and ORIENT_DB are supported for the moment!");
        }
    }

    public static Archipelago getInstance() throws CheckException {
        if (null == instance) {
            instance = new Archipelago();
        }
        return instance;
    }

    public QueryBuilder getQueryBuilder() throws CheckException {
        switch (databaseType) {
            case NEO4J:
                return Neo4JQueryImpl.init();
            case ORIENT_DB:
                return OrientDBQueryImpl.init();
            default:
                throw new CheckException("Only NEO4J and ORIENT_DB are supported for the moment!");

        }
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

    public List<Object> execute(ArchipelagoQuery aq) throws CheckException {
        List<Object> nodes = new LinkedList<>();
        switch (databaseType) {
            case NEO4J:
                try (Session s = this.driver.session()) {
                    StatementResult result = s.run(aq.getQuery());
                    Map<String, Object> nodeValues = new HashMap<>();
                    while (result.hasNext()) {
                        Record record = result.next();
                        aq.getKeys().stream().forEach(key -> {
                            if (ARCHIPELAGO_ID.equalsIgnoreCase(key)) {
                                nodeValues.put(key, record.get(key).asInt());
                            } else {
                                nodeValues.put(key, record.get(key).asObject());
                            }
                        });
                        Object node = aq.getTarget().getConstructor().newInstance();
                        if (!(aq.isWithId() && aq.getKeys().size() == 1)) {
                            ArchipelagoUtils.feedObject(node, nodeValues);
                        }
                        if (aq.isWithId()) {
                            ArchipelagoUtils.feedId(node, nodeValues.get(ARCHIPELAGO_ID));
                        }
                        nodes.add(node);
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    throw new CheckException(e.getMessage());
                }
                break;
            case ORIENT_DB:
                Response response = this.rootTarget.path(String.format("command/%s/sql/%s", getConfig().getDatabase().getName(), aq.getQuery()))
                        .request(MediaType.APPLICATION_JSON)
                        .get();
                break;
        }
        return nodes;
    }
}
