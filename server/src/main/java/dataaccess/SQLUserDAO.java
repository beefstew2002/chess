package dataaccess;
import dataaccess.exceptions.DataAccessException;
import model.UserData;

import java.sql.SQLException;
import java.util.ArrayList;

public class SQLUserDAO extends SQLDAO{

    public SQLUserDAO(){
        //Constructor: create the database if it doesn't exist
        super("""
                    CREATE TABLE  IF NOT EXISTS user (
                        username VARCHAR(256) NOT NULL,
                        password VARCHAR(256) NOT NULL,
                        email VARCHAR(256) NOT NULL,
                        PRIMARY KEY (username)
                    );
                    """);
    }

    public ArrayList<UserData> getUserData() {
        ArrayList<UserData> userData = new ArrayList<>();

        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM user;")) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        String username = rs.getString("username");
                        String password = rs.getString("password");
                        String email = rs.getString("email");
                        UserData aUser = new UserData(username, password, email);
                        userData.add(aUser);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }

        return userData;
    }

    public model.UserData getUser(String username) {
        String theUsername = null;
        String password = "";
        String email = "";

        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM user WHERE username=?;")) {
                preparedStatement.setString(1, username);
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        theUsername = rs.getString("username");
                        password = rs.getString("password");
                        email = rs.getString("email");
                    }
                } catch (SQLException e) {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (theUsername == null) { //means the name wasn't in the database
            return null;
        } else {
            return new model.UserData(theUsername, password, email);
        }

    }

    //SQL helper function
    public void createUser(String username, String password, String email) throws DataAccessException {
        String sql = "INSERT INTO user (username, password, email) VALUES (?, ?, ?);";

        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, email);

                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database" + ex);
        }
    }
    public boolean isEmpty() {
        return getUserData().isEmpty();
    }

    public void clearData() {
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE user")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
