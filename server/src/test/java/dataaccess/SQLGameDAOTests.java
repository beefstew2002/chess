package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
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
    private int addGame(String gameName) throws DataAccessException{
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
    //get game data
    private ArrayList<GameData> checkGames() throws DataAccessException{
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
    public void createGameFailure() throws DataAccessException{
        String longName = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaasdkfjljklajklas";
        Assertions.assertThrows(RuntimeException.class, ()->{gdao.createGame(longName);});
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
    public void getGameFailure() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, ()->{gdao.getGame(1);});
    }

    //listGames success
    @Test
    @DisplayName("listGames success")
    public void listGamesSuccess() throws DataAccessException {
        addGame("gooby");
        addGame("bingus");
        addGame("cromslor");
        ArrayList<GameMetaData> gameData = gdao.listGames();

        Assertions.assertEquals(3, gameData.size());
    }
    //listGames failure
    @Test
    @DisplayName("listGames failure")
    public void listGamesFailure() throws DataAccessException {
        //No entries if no games
        ArrayList<GameMetaData> gameData = gdao.listGames();

        Assertions.assertEquals(0, gameData.size());

    }

    //updateGame success
    @Test
    @DisplayName("updateGame success")
    public void updateGameSuccess() throws DataAccessException {

    }
    //updateGame failure
    @Test
    @DisplayName("updateGame failure")
    public void updateGameFailure() throws DataAccessException {

    }

    //isEmpty success
    @Test
    @DisplayName("isEmpty success")
    public void isEmpty() throws DataAccessException {

    }
    //isEmpty failure
    @Test
    @DisplayName("isEmpty failure")
    public void isEmptyFailure() throws DataAccessException {

    }

    //hasGame success
    @Test
    @DisplayName("hasGame success")
    public void hasGameSuccess() throws DataAccessException {

    }
    //hasGame failure
    @Test
    @DisplayName("hasGame failure")
    public void hasGameFailure() throws DataAccessException {

    }

    //getGameData success
    @Test
    @DisplayName("getGameData success")
    public void getGameDataSuccess() throws DataAccessException {

    }
    //getGameData failure
    @Test
    @DisplayName("getGameData failure")
    public void getGameDataFailure() throws DataAccessException {

    }

    //clearData success
    @Test
    @DisplayName("clear")
    public void clear() throws DataAccessException {

    }
}
