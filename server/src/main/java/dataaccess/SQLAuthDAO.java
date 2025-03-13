package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import static dataaccess.DatabaseManager.createDatabase;

public class SQLAuthDAO implements DAInterface {

    public SQLAuthDAO() {
        //Constructor: create the database if it doesn't exist
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection() throws SQLException {
        try {
            return DatabaseManager.getConnection();
        }
        catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void configureDatabase() throws DataAccessException {
        //Get database name
        createDatabase();
        try (var conn = getConnection()) {

            var createUserTable = """
                    CREATE TABLE IF NOT EXISTS auth (
                        username VARCHAR(256) NOT NULL,
                        authToken VARCHAR(256) NOT NULL,
                        PRIMARY KEY (authToken)
                    );
                    """;

            try (var createTableStatement = conn.prepareStatement(createUserTable)) {
                createTableStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<AuthData> getAuthData() {

        //SELECT * FROM auth;
        ArrayList<AuthData> authDataList = new ArrayList<>();

        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM auth;")) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        String username = rs.getString("username");
                        String authToken = rs.getString("authToken");
                        authDataList.add(new AuthData(username, authToken));
                    }
                    return authDataList;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createAuth(AuthData authData) {
        //AUTH_DATA.add(authData);

        //INSERT INTO auth (username, authToken) VALUES (*username, *authToken)

        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO auth (username, authToken) VALUES (?, ?);")) {
                preparedStatement.setString(1, authData.username());
                preparedStatement.setString(2, authData.authToken());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifyAuth(String authToken) {
        /*int i = 0;
        while (i < AUTH_DATA.size()) {
            if (AUTH_DATA.get(i).authToken().equals(authToken)) {
                return true;
            }
            i++;
        }*/

        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * from auth WHERE authToken = ?")) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        if (rs.getString("authToken").equals(authToken)) {
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public AuthData getAuth(String authToken) throws DataAccessException {

        //SELECT * FROM auth WHERE authToken = authToken;

        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM auth WHERE authToken = ?")) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        String username = rs.getString("username");
                        String theAuthToken = rs.getString("authToken");
                        return new AuthData(username, theAuthToken);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new UnauthorizedException("there's no auth");
    }

    public void deleteAuth(String authToken) {

        //DELETE FROM auth WHERE username = *username;

        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("DELETE FROM auth WHERE authToken = ?")) {
                preparedStatement.setString(1, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isEmpty() {
        return getAuthData().isEmpty();
    }

    public void clearData() {
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE auth")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
