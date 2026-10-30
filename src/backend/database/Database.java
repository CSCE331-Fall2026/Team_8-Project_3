package database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.Database;

// Our old database function call
// Call in App at beginning to establish connection
public final class Database {
    private static final HikariDataSource pool = create();

    private Database() {}

    private static HikariDataSource create() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbSetup.url);
        config.setUsername(dbSetup.user);
        config.setPassword(dbsetup.pswd);

        return new HikariDataSource(config);
    }

    public static DataSource dataSource() {
        return pool;
    }

    public static void close() {
        pool.close();
    }
}
