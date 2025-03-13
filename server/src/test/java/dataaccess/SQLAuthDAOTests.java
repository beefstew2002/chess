package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.ArrayList;

import static dataaccess.DatabaseManager.getConnection;

public class SQLAuthDAOTests {

    UserDAO udao;
    AuthDAO adao;
    GameDAO gdao;

    String username = "peeps";
    String authToken = "fake auth token";
    AuthData user;
    //setup
    @BeforeEach
    public void setup() {
        udao = new UserDAO();
        adao = new AuthDAO();
        gdao = new GameDAO();

        udao.clearData();
        adao.clearData();
        gdao.clearData();

        user = new AuthData(username, authToken);
    }

    //invent user
    private void inventUser() throws DataAccessException {

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
        Assertions.assertDoesNotThrow(()->{adao.getAuthData();});

    }
    //getAuthData - failure
    @Test
    @DisplayName("Auth data not found")
    public void noAuth() throws DataAccessException {

        //Check a different auth token
        Assertions.assertTrue(adao.getAuthData().isEmpty());
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
    @DisplayName("Verify Auth success")
    public void verifyAuthSuccess() throws DataAccessException {
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
    //verifyAuth - failure
    @Test
    @DisplayName("Verify Auth failure")
    public void verifyAuthFailure() throws DataAccessException {

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

    //getAuth - success
    @Test
    @DisplayName("GetAuth success")
    public void getAuthSuccess() throws DataAccessException {
        inventUser();

        Assertions.assertEquals(user, adao.getAuth(authToken));
    }
    //getAuth - failure
    @Test
    @DisplayName("getAuth failure")
    public void getAuthFailure() throws DataAccessException {

        Assertions.assertThrows(DataAccessException.class, ()->{adao.getAuth(authToken);});
    }

    //deleteAuth - success
    @Test
    @DisplayName("deleteAuth success")
    public void deleteAuthSuccess() throws DataAccessException {
        inventUser();

        adao.deleteAuth(authToken);

        //Get auth data
        ArrayList<AuthData> authDataList = new ArrayList<AuthData>();

        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM auth;")) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        String username = rs.getString("username");
                        String authToken = rs.getString("authToken");
                        authDataList.add(new AuthData(username, authToken));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertTrue(authDataList.isEmpty());
    }
    //deleteAuth - failure
    @Test
    @DisplayName("deleteAuth failure")
    public void deleteAuthFailure() throws DataAccessException {
        inventUser();

        adao.deleteAuth("different auth token");

        //Get auth data
        ArrayList<AuthData> authDataList = new ArrayList<AuthData>();

        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM auth;")) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        String username = rs.getString("username");
                        String authToken = rs.getString("authToken");
                        authDataList.add(new AuthData(username, authToken));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertFalse(authDataList.isEmpty());
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
