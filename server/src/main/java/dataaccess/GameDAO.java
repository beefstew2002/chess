package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.GameMetaData;

import java.util.ArrayList;

public class GameDAO implements DAInterface{
    //Methods this will need:
    //createGame
    //getGame
    //hasGame
    //listGames
    //updateGame
    //clear
    public int createGame(String gameName) {
        int gameId = getGameId();
        String whiteUsername = null;
        String blackUsername = null;
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(gameId, whiteUsername, blackUsername, gameName, game);
        storeGameData(gameData);

        return gameId;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return getGameData(gameID);
    }

    public ArrayList<GameMetaData> listGames() {
        ArrayList<GameData> gameData = getGameData();
        ArrayList<GameMetaData> gameList = new ArrayList<>();
        for (GameData game : gameData) {
            gameList.add(new GameMetaData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
        }
        return gameList;
    }

    public void updateGame(GameData gd) {
        updateGameData(gd);
    }

    public boolean isEmpty() {
        return isGameDataEmpty();
    }

    public boolean hasGame(String gameName) {
        return hasGameData(gameName);
    }
}
