/**
 *
 */
package org.archipelago.core.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.archipelago.core.builder.Neo4JBuilder;
import org.archipelago.test.domain.school.ClassRoom;
import org.neo4j.driver.v1.*;

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
    private static GraphConnect instance;
    private static Driver driver;

    private GraphConnect() {
        this.driver = GraphDatabase.driver(String.format("bolt://%s:%d", HOST, BOLD_PORT),
                AuthTokens.basic(USERNAME, PASSWORD));
        Session session = driver.session();
        LOGGER.info(new Neo4JBuilder().makeCreate(ClassRoom.class));
        session.run("CREATE (a:Person {name: {name}, title: {title}})",
               parameters("name", "Arthur", "title", "King"));

        StatementResult result = session.run("MATCH (a:Person) WHERE a.name = {name} " +
                        "RETURN a.name AS name, a.title AS title",
                parameters("name", "Arthur"));
        while (result.hasNext()) {
            Record record = result.next();
            System.out.println(record.get("title").asString() + " " + record.get("name").asString());
        }

        session.close();
        driver.close();

    }

    public static GraphConnect getInstance() {
        if (null == instance) {
            instance = new GraphConnect();
        }
        return instance;
    }

    public Session getSession() {
        return driver.session();
    }

}
