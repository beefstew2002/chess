package client;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DatabaseShareables;
import dataaccess.DbProperties;
import dataaccess.exceptions.DataAccessException;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import dataaccess.DatabaseManager;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:"+port);
    }

    /*
     * Load the database information for the db.properties file.
     * Copied in from various bits in the server package. I sure hope I did it right bc idk what I did
     */
    static {
        DbProperties props = DatabaseShareables.getDbProperties();
        DATABASE_NAME = props.databaseName();
        USER = props.user();
        PASSWORD = props.password();
        CONNECTION_URL = props.connectionUrl();
    }
    protected static Connection getConnection() throws DataAccessException {
        return DatabaseShareables.getConnection(DATABASE_NAME, USER, PASSWORD, CONNECTION_URL);
    }

    @BeforeEach
    public void initEach() {
        //clear all database data
        //rikes
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE game")) {
                preparedStatement.executeUpdate();
            }
            try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE user")) {
                preparedStatement.executeUpdate();
            }
            try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE auth")) {
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
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
        assertDoesNotThrow(() -> {
            facade.logout(authData.authToken());
        });
        assertThrows(Exception.class, ()->{
            facade.logout(authData.authToken());
        });

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
        var authData = facade.register("player1", "password", "p1@mail.com");
        int gameID = facade.create("welcome to the underground", authData.authToken()).gameID();
        assertEquals(1, gameID);
    }

    //Create failure
    @Test
    @DisplayName("Create failure")
    void createFailure() throws Exception {
        var authData = facade.register("player1", "password", "p1@mail.com");
        int gameID = facade.create("welcome to the underground", authData.authToken()).gameID();
        assertThrows(Exception.class, () -> {
            facade.create("welcome to the underground", authData.authToken());
        });

    }

    //List success
    @Test
    @DisplayName("List success")
    void listSuccess() throws Exception {
        var authData = facade.register("player1", "password", "p1@mail.com");
        facade.create("welcome to the underground", authData.authToken());
        facade.create("how was the fall", authData.authToken());
        facade.create("if you wanna look around", authData.authToken());
        facade.create("give us a call", authData.authToken());

        var listGames = facade.list(authData.authToken()).games();

        assertFalse(listGames.isEmpty());
    }

    //List failure
    @Test
    @DisplayName("List failure")
    void listFailure() throws Exception {
        var authData = facade.register("player1", "password", "p1@mail.com");
        facade.create("welcome to the underground", authData.authToken());
        facade.create("how was the fall", authData.authToken());
        facade.create("if you wanna look around", authData.authToken());
        facade.create("give us a call", authData.authToken());

        assertThrows(Exception.class, () -> {
            facade.list("bad auth token");
        });

    }

    //Join success
    @Test
    @DisplayName("Join success")
    void joinSuccess() throws Exception {
        String username = "TheGr8Papyrus";
        var authData = facade.register(username, "password", "p1@mail.com");
        int gameId = facade.create("welcome to the underground", authData.authToken()).gameID();
        facade.join(gameId, "WHITE", authData.authToken());

        assertEquals(username, facade.list(authData.authToken()).games().getFirst().whiteUsername());

    }

    //Join failure
    @Test
    @DisplayName("Join failure")
    void joinFailure() throws Exception {
        String username = "TheGr8Papyrus";
        var authData = facade.register(username, "password", "p1@mail.com");
        int gameId = facade.create("welcome to the underground", authData.authToken()).gameID();
        facade.join(gameId, "WHITE", authData.authToken());


        var authData2 = facade.register("scooby", "roobyracks", "mysterymachine@mr.e");
        assertThrows(Exception.class, () -> {
            facade.join(gameId, "WHITE", authData2.authToken());
        });

    }

    //Clear success
    @Test
    @DisplayName("Clear success")
    void clearSuccess() throws Exception {
        var authData = facade.register("player1", "password", "p1@mail.com");
        facade.create("welcome to the underground", authData.authToken());
        facade.create("how was the fall", authData.authToken());
        facade.create("if you wanna look around", authData.authToken());
        facade.create("give us a call", authData.authToken());

        facade.clearData();

        assertThrows(Exception.class, () -> {
            facade.login("player1", "password");
        });

    }
}
