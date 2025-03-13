package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.GameMetaData;
import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLGameDAO implements DAInterface{

    public SQLGameDAO() {
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
        String DATABASE_NAME;
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            DATABASE_NAME = props.getProperty("db.name");
        } catch (Exception e) {
            throw new DataAccessException("yikes");
        }
        try (var conn = getConnection()) {
            var createDbStatement = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS "+DATABASE_NAME);
            createDbStatement.executeUpdate();

            conn.setCatalog(DATABASE_NAME);

            var createUserTable = """
                    CREATE TABLE IF NOT EXISTS game (
                        gameID int NOT NULL AUTO_INCREMENT,
                        whiteUsername varchar(256) DEFAULT NULL,
                        blackUsername varchar(256) DEFAULT NULL,
                        gameName varchar(256),
                        gameJson longtext NOT NULL,
                        PRIMARY KEY (gameID)
                    );
                    """;

            try (var createTableStatement = conn.prepareStatement(createUserTable)) {
                createTableStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int createGame(String gameName) {

        //INSERT INTO game (gameID, gameName, gameJson) VALUES (*id+1, *gameName, *ChessGame json)

        //Get json of new ChessGame
        var json = new Gson().toJson(new ChessGame());

        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO game (gameName, gameJson) VALUES (?, ?)", RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, gameName);
                preparedStatement.setString(2, json);

                preparedStatement.executeUpdate();

                var resultSet = preparedStatement.getGeneratedKeys();
                var ID = 0;
                if (resultSet.next()) {
                    ID = resultSet.getInt(1);
                }
                return ID;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public GameData getGame(int gameID) throws DataAccessException {


        String whiteUsername = null;
        String blackUsername = null;
        String gameName = null;
        ChessGame game = null;
        int theGameID = 0;
        boolean found = false;

        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * from game WHERE gameID = ?")) {
                preparedStatement.setInt(1, gameID);
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        theGameID = rs.getInt("gameID");
                        whiteUsername = rs.getString("whiteUsername");
                        blackUsername = rs.getString("blackUsername");
                        gameName = rs.getString("gameName");
                        game = new Gson().fromJson(rs.getString("gameJson"), ChessGame.class);
                        found = true;
                    }
                } catch (SQLException e) {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (found) {
            return new GameData(theGameID, whiteUsername, blackUsername, gameName, game);
        } else {
            throw new DataAccessException("that game doesn't exist");
        }
    }

    public ArrayList<GameMetaData> listGames() {

        ArrayList<GameMetaData> gameData = new ArrayList<GameMetaData>();

        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM game")) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        int gameID = rs.getInt("gameID");
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");
                        gameData.add(new GameMetaData(gameID, whiteUsername, blackUsername, gameName));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return gameData;
    }

    public void updateGame(GameData gd) {

        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("UPDATE game SET whiteUsername=?, blackUsername=?, gameJson=? WHERE gameID = ?")) {
                preparedStatement.setString(1, gd.whiteUsername());
                preparedStatement.setString(2, gd.blackUsername());
                preparedStatement.setString(3, new Gson().toJson(gd.game()));
                preparedStatement.setInt(4, gd.gameID());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isEmpty() {
        return getGameData().isEmpty();
    }

    public boolean hasGame(String gameName) {
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * from game WHERE gameName = ?")) {
                preparedStatement.setString(1, gameName);
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        String theGameName = rs.getString("gameName");
                        return true;
                    }
                } catch (SQLException e) {
                    return false;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public ArrayList<GameData> getGameData() {
        ArrayList<GameData> gameData = new ArrayList<GameData>();

        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM game")) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        int gameID = rs.getInt("gameID");
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");
                        ChessGame game = new Gson().fromJson(rs.getString("gameJson"), ChessGame.class);
                        gameData.add(new GameData(gameID, whiteUsername, blackUsername, gameName, game));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return gameData;
    }

    public void clearData() {
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE game")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
