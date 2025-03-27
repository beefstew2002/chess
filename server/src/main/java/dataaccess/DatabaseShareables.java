package dataaccess;

import java.util.Properties;

public class DatabaseShareables {

    public static DbProperties getDbProperties() {
        String DATABASE_NAME;
        String USER;
        String PASSWORD;
        String CONNECTION_URL;
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) {
                    throw new Exception("Unable to load db.properties");
                }
                Properties props = new Properties();
                props.load(propStream);
                DATABASE_NAME = props.getProperty("db.name");
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);

                return new DbProperties(DATABASE_NAME, USER, PASSWORD, CONNECTION_URL);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }
}
