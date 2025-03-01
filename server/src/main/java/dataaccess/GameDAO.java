package dataaccess;

import chess.ChessGame;
import model.GameData;

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
        String whiteUsername = "";
        String blackUsername = "";
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(gameId, whiteUsername, blackUsername, gameName, game);
        storeGameData(gameData);

        return gameId;
    }

    public ArrayList<GameData> listGames() {
        return getGameData();
    }

    public boolean isEmpty() {
        return isGameDataEmpty();
    }

    public boolean hasGame(String gameName) {
        return hasGameData(gameName);
    }
}
