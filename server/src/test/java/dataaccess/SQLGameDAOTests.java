package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.exceptions.DataAccessException;
import model.GameData;
import model.GameMetaData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static dataaccess.DatabaseManager.getConnection;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLGameDAOTests {


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

    //invent game
    private int addGame(String gameName) throws DataAccessException {

        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO game (gameName, gameJson) VALUES (?, ?)", RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, gameName);
                preparedStatement.setString(2, new Gson().toJson(new ChessGame()));

                preparedStatement.executeUpdate();

                var resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
    //get game data
    private ArrayList<GameData> checkGames() throws DataAccessException{
        ArrayList<GameData> gameData = new ArrayList<>();

        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM game")) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        ChessGame game = new Gson().fromJson(rs.getString("gameJson"), ChessGame.class);
                        gameData.add(new GameData(
                                rs.getInt("gameID"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                game));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return gameData;
    }

    //createGame success
    @Test
    @DisplayName("createGame success")
    public void createGameSuccess() throws DataAccessException{
        gdao.createGame("zippers");

        Assertions.assertFalse(checkGames().isEmpty());
    }
    //createGame failure
    @Test
    @DisplayName("createGame failure: name too long")
    public void createGameFailure() {
        String longName = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaasdkfjljklajklas";
        Assertions.assertThrows(RuntimeException.class, ()->gdao.createGame(longName));
    }

    //getGame success
    @Test
    @DisplayName("getGame success")
    public void getGameSuccess() throws DataAccessException{
        int id = addGame("guh");

        GameData game = gdao.getGame(id);

        Assertions.assertEquals("guh", game.gameName());
    }
    //getGame failure
    @Test
    @DisplayName("getGame failure")
    public void getGameFailure() {
        Assertions.assertThrows(DataAccessException.class, ()->gdao.getGame(1));
    }

    //listGames success
    @Test
    @DisplayName("listGames success")
    public void listGamesSuccess() throws DataAccessException {
        addGame("gooby");
        addGame("bingus");
        addGame("cromslor");
        ArrayList<GameData> gameData = gdao.listGames();

        Assertions.assertEquals(3, gameData.size());
    }
    //listGames failure
    @Test
    @DisplayName("listGames failure")
    public void listGamesFailure() {
        //No entries if no games
        ArrayList<GameData> gameData = gdao.listGames();

        Assertions.assertEquals(0, gameData.size());

    }

    //updateGame success
    @Test
    @DisplayName("updateGame success")
    public void updateGameSuccess() throws DataAccessException {
        int id = addGame("scooby");

        GameData newGame = new GameData(id, "zoinks", "rikes", "scooby", new ChessGame());
        gdao.updateGame(newGame);

        GameData theGame = checkGames().getFirst();
        Assertions.assertEquals(newGame.getMetaData(), theGame.getMetaData());
    }
    //updateGame failure
    @Test
    @DisplayName("updateGame failure")
    public void updateGameFailure() throws DataAccessException {
        addGame("scooby");

        GameData newGame = new GameData(2, "zoinks", "rikes", "scooby", new ChessGame());
        gdao.updateGame(newGame);

        GameData theGame = checkGames().getFirst();
        Assertions.assertNotEquals(newGame.getMetaData(), theGame.getMetaData());

    }

    //isEmpty success
    @Test
    @DisplayName("isEmpty success")
    public void isEmpty() {
        //List is empty
        Assertions.assertTrue(gdao.isEmpty());
    }
    //isEmpty failure
    @Test
    @DisplayName("isEmpty failure")
    public void isEmptyFailure() throws DataAccessException {
        addGame("zoinks");
        Assertions.assertFalse(gdao.isEmpty());
    }

    //hasGame success
    @Test
    @DisplayName("hasGame success")
    public void hasGameSuccess() throws DataAccessException {
        addGame("zoinks");

        Assertions.assertTrue(gdao.hasGame("zoinks"));
    }
    //hasGame failure
    @Test
    @DisplayName("hasGame failure")
    public void hasGameFailure() {
        Assertions.assertFalse(gdao.hasGame("jinkies"));
    }

    //getGameData success
    @Test
    @DisplayName("getGameData success")
    public void getGameDataSuccess() throws DataAccessException {
        addGame("firstGame");

        Assertions.assertEquals(checkGames().getFirst().getMetaData(), gdao.getGameData().getFirst().getMetaData());
    }
    //getGameData failure
    @Test
    @DisplayName("getGameData failure")
    public void getGameDataFailure() {
        //No games to get
        Assertions.assertTrue(gdao.getGameData().isEmpty());
    }

    //clearData success
    @Test
    @DisplayName("clear")
    public void clear() throws DataAccessException {
        addGame("the core");

        gdao.clearData();

        Assertions.assertTrue(checkGames().isEmpty());
    }
}
