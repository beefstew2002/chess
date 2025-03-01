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
        String whiteUsername = "";
        String blackUsername = "";
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(gameId, whiteUsername, blackUsername, gameName, game);
        storeGameData(gameData);

        return gameId;
    }

    public ArrayList<GameMetaData> listGames() {
        ArrayList<GameData> gameData = getGameData();
        ArrayList<GameMetaData> gameList = new ArrayList<>();
        for (int i=0; i<gameData.size(); i++) {
            GameData game = gameData.get(i);
            gameList.add(new GameMetaData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
        }
        return gameList;
    }

    public boolean isEmpty() {
        return isGameDataEmpty();
    }

    public boolean hasGame(String gameName) {
        return hasGameData(gameName);
    }
}
