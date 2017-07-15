package org.archipelago.core.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.archipelago.core.builder.Neo4JBuilder;
import org.neo4j.driver.v1.*;

import static org.neo4j.driver.v1.Values.parameters;

/**
 * @author Gilles Bodart
 */
public class GraphConnect implements AutoCloseable {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String HOST = "gbodart.be";
    private static final int PORT = 7474;
    private static final int BOLD_PORT = 7687;
    private static final String USERNAME = "neo4j";
    private static final String PASSWORD = "MemoryGilles";
    private static final Neo4JBuilder BUILDER = new Neo4JBuilder();
    private static GraphConnect instance;
    private static Driver driver;

    private GraphConnect() {
        this.driver = GraphDatabase.driver(String.format("bolt://%s:%d", HOST, BOLD_PORT),
                AuthTokens.basic(USERNAME, PASSWORD));
    }

    public static GraphConnect getInstance() {
        if (null == instance) {
            instance = new GraphConnect();
        }
        return instance;
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
            s.run(BUILDER.makeCreate(object.getClass()),
                    parameters(BUILDER.fillCreate(object).toArray()));
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
                s.run(BUILDER.makeRelation(idA, idB, name, descriptor.getClass()), parameters(BUILDER.fillCreate(descriptor).toArray()));
                if (biDirectional) {
                    s.run(BUILDER.makeRelation(idB, idA, name, descriptor.getClass()), parameters(BUILDER.fillCreate(descriptor).toArray()));
                }
            } else {
                s.run(BUILDER.makeRelation(idA, idB, name, null));
                if (biDirectional) {
                    s.run(BUILDER.makeRelation(idB, idA, name, null));
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
            String stmt = BUILDER.makeMatch(object.getClass(), false);
            Object[] params = BUILDER.fillCreate(object).toArray();
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
