package database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import javax.sql.DataSource;

// Our old database function call
// Call in App at beginning to establish connection
public final class Database {
    // Environment variables take priority; db.properties (gitignored) is the local fallback
    private static final Path LOCAL_CONFIG = Path.of("db.properties");
    private static final Properties local = loadLocalConfig();
    private static HikariDataSource pool;

    private Database() {}

    public static boolean isConfigured() {
        return lookup("DB_URL", "url") != null
                && lookup("DB_USER", "user") != null
                && lookup("DB_PASSWORD", "password") != null;
    }

    private static HikariDataSource create() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(setting("DB_URL", "url"));
        config.setUsername(setting("DB_USER", "user"));
        config.setPassword(setting("DB_PASSWORD", "password"));

        return new HikariDataSource(config);
    }

    private static String lookup(String envName, String propertyName) {
        String value = System.getenv(envName);
        if (value == null || value.isBlank()) {
            value = local.getProperty(propertyName);
        }
        return value == null || value.isBlank() ? null : value;
    }

    private static String setting(String envName, String propertyName) {
        String value = lookup(envName, propertyName);
        if (value == null) {
            throw new IllegalStateException("Missing " + envName + " environment variable or '"
                    + propertyName + "' in " + LOCAL_CONFIG.toAbsolutePath());
        }
        return value;
    }

    private static Properties loadLocalConfig() {
        Properties props = new Properties();
        if (Files.exists(LOCAL_CONFIG)) {
            try (InputStream in = new FileInputStream(LOCAL_CONFIG.toFile())) {
                props.load(in);
            } catch (IOException e) {
                throw new IllegalStateException("Could not read " + LOCAL_CONFIG.toAbsolutePath(), e);
            }
        }
        return props;
    }

    public static synchronized DataSource dataSource() {
        if (pool == null) {
            pool = create();
        }
        return pool;
    }

    public static synchronized void close() {
        if (pool != null) {
            pool.close();
        }
    }
}
