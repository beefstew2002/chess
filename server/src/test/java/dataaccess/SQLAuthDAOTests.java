package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static dataaccess.DatabaseManager.getConnection;

public class SQLAuthDAOTests {

    UserDAO udao;
    AuthDAO adao;
    GameDAO gdao;

    //setup
    @BeforeEach
    public void setup() {
        udao = new UserDAO();
        adao = new AuthDAO();
        gdao = new GameDAO();

        udao.clearData();
        adao.clearData();
        gdao.clearData();


    }

    //getAuthData - success
    @Test
    @DisplayName("Auth data found")
    public void authGotten() throws DataAccessException {
        //Invent a user
        String username = "peeps";
        String authToken = "fake auth token";

        //Add auth data
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO auth (username, authToken) VALUES (?, ?);")) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //Check if it's there
        Assertions.assertTrue(adao.verifyAuth(authToken));

    }
    //getAuthData - failure
    @Test
    @DisplayName("Auth data not found")
    public void wrongAuth() throws DataAccessException {
        //Invent a user
        String username = "peeps";
        String authToken = "fake auth token";

        //Add auth data
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO auth (username, authToken) VALUES (?, ?);")) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //Check a different auth token
        Assertions.assertFalse(adao.verifyAuth("giggity"));
    }

    //createAuth - success
    @Test
    @DisplayName("Create auth successfully")
    public void createAuthSucceed() throws DataAccessException {
        String authToken = "uhuhuhuh";
        adao.createAuth(new AuthData("sans", authToken));
        boolean found = false;

        //Check if it's there
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM auth WHERE authToken = ?")) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        String theAuthToken = rs.getString("authToken");
                        found = true;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertTrue(found);
    }
    //createAuth - failure
    @Test
    @DisplayName("Create auth failure")
    public void createAuthFail() throws DataAccessException {
        String authToken = "uhuhuhuh";
        adao.createAuth(new AuthData("sans", authToken));
        //Attempt to create a duplicate
        Assertions.assertThrows(RuntimeException.class, ()->{adao.createAuth(new AuthData("sans", authToken));});
        boolean found = false;

        //Check if it's there
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM auth WHERE authToken = ?")) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        String theAuthToken = rs.getString("authToken");
                        found = true;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    //verifyAuth - success
    @Test
    @DisplayName("")
    public void test2() throws DataAccessException {

    }
    //verifyAuth - failure
    @Test
    @DisplayName("")
    public void test3() throws DataAccessException {

    }

    //getAuth - success
    @Test
    @DisplayName("")
    public void test4() throws DataAccessException {

    }
    //getAuth - failure
    @Test
    @DisplayName("")
    public void test5() throws DataAccessException {

    }

    //deleteAuth - success
    @Test
    @DisplayName("")
    public void test6() throws DataAccessException {

    }
    //deleteAuth - failure
    @Test
    @DisplayName("")
    public void test7() throws DataAccessException {

    }

    //isEmpty - success
    @Test
    @DisplayName("")
    public void test8() throws DataAccessException {

    }
    //isEmpty - failure
    @Test
    @DisplayName("")
    public void test9() throws DataAccessException {

    }

    //clearData success
    @Test
    @DisplayName("")
    public void test0() throws DataAccessException {

    }
}
