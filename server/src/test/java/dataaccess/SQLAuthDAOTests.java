package dataaccess;

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
    @DisplayName("")
    public void test() throws DataAccessException {

    }
    //createAuth - failure
    @Test
    @DisplayName("")
    public void test1() throws DataAccessException {

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
