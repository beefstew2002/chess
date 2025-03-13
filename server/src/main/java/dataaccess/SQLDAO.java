package dataaccess;

import dataaccess.exceptions.DataAccessException;

import java.sql.Connection;
import java.sql.SQLException;

import static dataaccess.DatabaseManager.createDatabase;

public class SQLDAO implements DAInterface{
    public SQLDAO(String createUserTable) throws RuntimeException{
        //Constructor: create the database if it doesn't exist
        try {
            configureDatabase(createUserTable);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void configureDatabase(String createUserTable) throws DataAccessException {
        createDatabase();
        try (var conn = getConnection()) {

            try (var createTableStatement = conn.prepareStatement(createUserTable)) {
                createTableStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected Connection getConnection() throws SQLException {
        try {
            return DatabaseManager.getConnection();
        }
        catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
}
