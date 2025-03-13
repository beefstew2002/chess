package dataaccess;

import dataaccess.exceptions.DataAccessException;
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

        inventUser(username, authToken);
    }
    private void inventUser(String username, String authToken) throws DataAccessException {

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

    //check
    private boolean checkForAuth(String authToken) throws DataAccessException{

        //Check if it's there
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM auth WHERE authToken = ?")) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
    private ArrayList<AuthData> getAuthData() throws DataAccessException {
        //Get auth data
        ArrayList<AuthData> authDataList = new ArrayList<>();

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

        return authDataList;
    }

    //getAuthData - success
    @Test
    @DisplayName("Auth data found")
    public void authGotten() throws DataAccessException {
        //Invent a user
        String username = "peeps";
        String authToken = "fake auth token";

        //Add auth data
        inventUser(username, authToken);

        //Check if it's there
        Assertions.assertDoesNotThrow(()->{adao.getAuthData();});

    }
    //getAuthData - failure
    @Test
    @DisplayName("Auth data not found")
    public void noAuth() {

        //Check a different auth token
        Assertions.assertTrue(adao.getAuthData().isEmpty());
    }

    //createAuth - success
    @Test
    @DisplayName("Create auth successfully")
    public void createAuthSucceed() throws DataAccessException {
        String authToken = "uhuhuhuh";
        adao.createAuth(new AuthData("sans", authToken));


        Assertions.assertTrue(checkForAuth(authToken));
    }
    //createAuth - failure
    @Test
    @DisplayName("Create auth failure")
    public void createAuthFail() {
        String authToken = "uhuhuhuh";
        adao.createAuth(new AuthData("sans", authToken));
        //Attempt to create a duplicate
        Assertions.assertThrows(RuntimeException.class, ()->adao.createAuth(new AuthData("sans", authToken)));


    }

    //verifyAuth - success
    @Test
    @DisplayName("Verify Auth success")
    public void verifyAuthSuccess() throws DataAccessException {
        //Invent a user
        String username = "peeps";
        String authToken = "fake auth token";

        //Add auth data
        inventUser(username, authToken);

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
        inventUser(username, authToken);

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
    public void getAuthFailure() {

        Assertions.assertThrows(DataAccessException.class, ()->adao.getAuth(authToken));
    }

    //deleteAuth - success
    @Test
    @DisplayName("deleteAuth success")
    public void deleteAuthSuccess() throws DataAccessException {
        inventUser();

        adao.deleteAuth(authToken);

        Assertions.assertTrue(getAuthData().isEmpty());
    }
    //deleteAuth - failure
    @Test
    @DisplayName("deleteAuth failure")
    public void deleteAuthFailure() throws DataAccessException {
        inventUser();

        adao.deleteAuth("different auth token");

        Assertions.assertFalse(getAuthData().isEmpty());
    }

    //isEmpty - success
    @Test
    @DisplayName("isEmpty success")
    public void isEmptySuccess() {

        Assertions.assertTrue(adao.isEmpty());
    }
    //isEmpty - failure
    @Test
    @DisplayName("isEmpty failure")
    public void isntEmpty() throws DataAccessException {
        inventUser();

        Assertions.assertFalse(adao.isEmpty());
    }

    //clearData success
    @Test
    @DisplayName("clearData success")
    public void clearDataTest() throws DataAccessException {
        inventUser();
        inventUser("papyrus", "spaghetti");
        inventUser("cram", "janet");

        adao.clearData();

        //Check if list is empty

        Assertions.assertTrue(getAuthData().isEmpty());
    }
}
