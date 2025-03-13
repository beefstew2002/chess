package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static dataaccess.DatabaseManager.getConnection;

public class SQLUserDAOTests {


    UserDAO udao;
    AuthDAO adao;
    GameDAO gdao;

    String username = "peeps";
    String password = "password";
    String email = "email@com";
    UserData user;
    //setup
    @BeforeEach
    public void setup() {
        udao = new UserDAO();
        adao = new AuthDAO();
        gdao = new GameDAO();

        udao.clearData();
        adao.clearData();
        gdao.clearData();

        user = new UserData(username, password, email);
    }

    //invent user
    private void inventUser() throws DataAccessException {

        inventUser(username, password, email);
    }
    private void inventUser(String username, String password, String email) throws DataAccessException {

        //Add auth data
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO user (username, password, email) VALUES (?, ?, ?);")) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, email);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private ArrayList<UserData> checkUsers() {
        ArrayList<UserData> userData = new ArrayList<>();

        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM user;")) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        UserData aUser = new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                        userData.add(aUser);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }

        return userData;

    }


    //Success getUserData
    @Test
    @DisplayName("Success getUserData")
    public void successgetUserData() throws DataAccessException{
        inventUser();
        inventUser("cromslor", "bingus", "poob has it for you");

        Assertions.assertEquals(checkUsers(), udao.getUserData());

    }
    //Failure getUserData
    @Test
    @DisplayName("Failure getUserData")
    public void failgetUserData() {
        //No data to get
        Assertions.assertEquals(new ArrayList<UserData>(), udao.getUserData());
    }

    //Success getUser
    @Test
    @DisplayName("Success getUser")
    public void successgetUser() throws DataAccessException {
        inventUser();

        UserData theUser = udao.getUser(username);

        Assertions.assertEquals(user, theUser);
    }
    //Failure getUser
    @Test
    @DisplayName("Failure getUser")
    public void failgetUser() {
        Assertions.assertNull(udao.getUser("gaster"));
    }

    //Success createUser
    @Test
    @DisplayName("Success createUser")
    public void successcreateUser() throws DataAccessException{
        udao.createUser(username, password, email);

        Assertions.assertEquals(user, checkUsers().getFirst());
    }
    //Failure createUser
    @Test
    @DisplayName("Failure createUser")
    public void failcreateUser() {
        //Name too long
        String longName = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaasdkfjljklajklas";
        Assertions.assertThrows(DataAccessException.class, ()->udao.createUser(longName, password, email));
    }

    //Success isEmpty
    @Test
    @DisplayName("Success isEmpty")
    public void successisEmpty() {
        Assertions.assertTrue(udao.isEmpty());
    }
    //Failure isEmpty
    @Test
    @DisplayName("Failure isEmpty")
    public void failisEmpty() throws DataAccessException {
        inventUser();
        Assertions.assertFalse(udao.isEmpty());
    }

    //Success clearData
    @Test
    @DisplayName("Success clearData")
    public void successclearData() throws DataAccessException {
        inventUser();

        udao.clearData();

        Assertions.assertTrue(checkUsers().isEmpty());

    }

}
