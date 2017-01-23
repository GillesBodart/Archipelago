/**
 * 
 */
package org.archipelago.core.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Gilles Bodart
 *
 */
public class GraphConnect {

    private static GraphConnect instance;
    private static final Logger LOGGER = LogManager.getLogger();
    private static final boolean BOLT = false;
    private static final String HOST = "gbodart.be";
    private static final int PORT = 7474;
    private static final String USERNAME = "neo4j";
    private static final String PASSWORD = "MemoryGilles";

    private GraphConnect() {
        try (Connection con = DriverManager.getConnection(String.format("jdbc:neo4j:%s://%s:%d", BOLT ? "bolt" : "http", HOST, PORT), USERNAME, PASSWORD)) {
            // Querying
            LOGGER.info("Querying");
            try (Statement stmt = con.createStatement()) {
                ResultSet rs = stmt.executeQuery("MATCH (n:Actor{ID='1'}) RETURN n");
                LOGGER.info("Matching");
                while (rs.next()) {
                    System.out.println(rs.getString("n"));
                }
            } 
            catch (SQLException e) {
                LOGGER.error(e);
            }
        } catch (SQLException e1) {
            LOGGER.error(e1);
        }
    }

    public static GraphConnect getInstance() {
        if (null == instance) {
            instance = new GraphConnect();
        }
        return instance;
    }

}
