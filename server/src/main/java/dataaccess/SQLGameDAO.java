package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.exceptions.DataAccessException;
import model.GameData;
import model.GameMetaData;

import java.sql.SQLException;
import java.util.ArrayList;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLGameDAO extends SQLDAO{

    public SQLGameDAO() {
        //Constructor: create the database if it doesn't exist
        super("""
                    CREATE TABLE IF NOT EXISTS game (
                        gameID int NOT NULL AUTO_INCREMENT,
                        whiteUsername varchar(256) DEFAULT NULL,
                        blackUsername varchar(256) DEFAULT NULL,
                        gameName varchar(256),
                        gameJson longtext NOT NULL,
                        PRIMARY KEY (gameID)
                    );
                    """);
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
                var id = 0;
                if (resultSet.next()) {
                    id = resultSet.getInt(1);
                }
                return id;
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

        ArrayList<GameMetaData> gameData = new ArrayList<>();

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
                    if (rs.next()) {
                        rs.getString("gameName");
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
        ArrayList<GameData> gameData = new ArrayList<>();

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
