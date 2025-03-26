package client;

import dataaccess.DatabaseManager;
import dataaccess.exceptions.DataAccessException;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    private static String DATABASE_NAME;
    private static String USER;
    private static String PASSWORD;
    private static String CONNECTION_URL;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    /*
     * Load the database information for the db.properties file.
     * Copied in from various bits in the server package. I sure hope I did it right bc idk what I did
     */
    static {
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
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }
    protected static Connection getConnection() throws SQLException {
        try {
            try {
                var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
                conn.setCatalog(DATABASE_NAME);
                return conn;
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }
        catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    @BeforeEach
    public void initEach() {
        //clear all database data
        //rikes
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE game")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE user")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE auth")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        assertTrue(true);
    }

    @Test
    @DisplayName("Register success")
    void register() throws Exception {
        var authData = facade.register("player1", "password", "p1@mail.com");
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    @DisplayName("Register failure")
    void registerFail() throws Exception {
        facade.register("player1", "password", "p1@mail.com");
        assertThrows(Exception.class, ()->{facade.register("player1", "password", "p1@mail.com");});

    }

    //Login success
    @Test
    @DisplayName("Login success")
    void loginSuccess() throws Exception {
        facade.register("player1", "password", "p1@mail.com");
        var authData = facade.login("player1", "password");
        assertTrue(authData.authToken().length() > 10);
    }

    //Login failure
    @Test
    @DisplayName("Login failure")
    void loginFailure() throws Exception {
        facade.register("player1", "password", "p1@mail.com");
        assertThrows(Exception.class, ()->{
            facade.login("player1", "wrong_password");
        });

    }

    //Logout success
    @Test
    @DisplayName("Logout success")
    void logoutSuccess() throws Exception {
        facade.register("player1", "password", "p1@mail.com");
        var authData = facade.login("player1", "password");
        facade.logout(authData.authToken());

    }

    //Logout failure
    @Test
    @DisplayName("Logout failure")
    void logoutFailure() throws Exception {
        var authData = facade.register("player1", "password", "p1@mail.com");
        facade.logout(authData.authToken());
        assertThrows(Exception.class, ()->{
            facade.logout(authData.authToken());
        });
    }

    //Create success
    @Test
    @DisplayName("Create success")
    void createSuccess() throws Exception {

    }

    //Create failure
    @Test
    @DisplayName("Create failure")
    void createFailure() throws Exception {

    }

    //List success
    @Test
    @DisplayName("List success")
    void listSuccess() throws Exception {

    }

    //List failure
    @Test
    @DisplayName("List failure")
    void listFailure() throws Exception {

    }

    //Join success
    @Test
    @DisplayName("Join success")
    void joinSuccess() throws Exception {

    }

    //Join failure
    @Test
    @DisplayName("Join failure")
    void joinFailure() throws Exception {

    }

    //Clear success
    @Test
    @DisplayName("Clear success")
    void clearSuccess() throws Exception {

    }
}
