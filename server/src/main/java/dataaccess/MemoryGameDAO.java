package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.GameMetaData;

import java.util.ArrayList;

public class MemoryGameDAO implements DAInterface{

    static ArrayList<GameData> GAME_DATA = new ArrayList<>();

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
        GAME_DATA.add(gameData);

        return gameId;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData gd : GAME_DATA) {
            if (gd.gameID() == gameID) {
                return gd;
            }
        }
        throw new DataAccessException("Game doesn't exist");
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
        boolean searching = true;
        int i = 0;
        while (searching) {
            if (GAME_DATA.get(i).gameName().equals(gd.gameName())) {
                GAME_DATA.set(i, gd);
                searching = false;
            }
            i++;
        }
    }

    public boolean isEmpty() {
        return GAME_DATA.isEmpty();
    }

    public boolean hasGame(String gameName) {
        for (int i = 0; i < GAME_DATA.size(); i++) {
            if (GAME_DATA.get(i).gameName().equals(gameName)) {
                return true;
            }
            i++;
        }
        return false;
    }

    public ArrayList<GameData> getGameData() {
        return GAME_DATA;
    }

    public int getGameId() {
        return GAME_DATA.size() + 1;
    }

    public void clearData() {
        GAME_DATA.clear();
    }
}
