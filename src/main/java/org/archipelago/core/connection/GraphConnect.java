package org.archipelago.core.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.archipelago.core.builder.Neo4JBuilder;
import org.archipelago.test.domain.school.ClassRoom;
import org.archipelago.test.domain.school.Teacher;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

import java.time.LocalDate;

import static org.neo4j.driver.v1.Values.parameters;

/**
 * @author Gilles Bodart
 */
public class GraphConnect {

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
        //Session session = driver.session();
        ClassRoom cr = new ClassRoom("l003", 23, true, false, false);
        Teacher p = new Teacher();
        p.setDateOfBirth(LocalDate.now());
        p.setFirstName("Gilles");
        p.setLastName("Bodart");
        p.setSexe("M");
        p.setDiploma("Master");
        LOGGER.debug(BUILDER.makeMatch(p.getClass()));
        driver.close();

    }

    public static GraphConnect getInstance() {
        if (null == instance) {
            instance = new GraphConnect();
        }
        return instance;
    }

    /**
     * Persits an object in the database
     *
     * @param object the object to persist
     * @return the internal Id of the object
     */
    public int persist(Object object) {
        try (Session s = this.driver.session()) {
            s.run(BUILDER.makeCreate(object.getClass()),
                    parameters(BUILDER.fillCreate(object).toArray()));
            s.run(BUILDER.makeMatch(object.getClass()),
                    parameters(BUILDER.fillCreate(object).toArray()));

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        return 1;
    }

    /**
     * Persits an object in the database
     *
     * @param object the object to persist
     * @return the internal Id of the object
     */
    public int getID(Object object) {
        try (Session s = this.driver.session()) {
            s.run(BUILDER.makeMatch(object.getClass()),
                    parameters(BUILDER.fillCreate(object).toArray()));

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        return 1;
    }


    public Session getSession() {
        return driver.session();
    }

}
