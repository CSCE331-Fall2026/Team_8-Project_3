package database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.DriverManager;

// Our old database function call
// Call in App at beginning to establish connection
public class Database {
    private static final Logger logger = LoggerFactory.getLogger(Database.class);

    public Connection connectDb() {
        try {
            dbSetup my = new dbSetup();

            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(my.url, my.user, my.pswd);
            logger.info("Successfully connected to database...");
            return conn;

        } catch (Exception e) {
            logger.error("Error connecting to database: {}", e.getMessage(), e);
            return null;
        }
    }
}
