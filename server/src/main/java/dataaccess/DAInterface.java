package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.ArrayList;

public interface DAInterface {
    //In order to abstract from your services where data is actually being stored,
    //you must create a Java interface that hides all of the implementation details
    //for accessing and retrieving data. In this phase you will create an implementation
    //of your data access interface that stores your server's data in main memory (RAM)
    //using standard data structures (maps, sets, lists). In the next phase you
    //will create an implementation of the data access interface that uses an external
    //SQL database.

    //So for Phase 3 purposes, it's just going to store everything internally
    //In an interface? How is everything going to access it?

    //ok ok ok ok ok
    //What this needs to store:
    //Set of UserData objects
    //Set of AuthData objects
    //Set of GameData objects

    //Methods this needs:
    //store user data
    //store auth data
    //store game data
    //get that data
    //update game data
    //clear all data

    ArrayList<UserData> userData = new ArrayList<>();
    ArrayList<GameData> gameData = new ArrayList<>();
    ArrayList<AuthData> authData = new ArrayList<>();

    default ArrayList<UserData> getUserData() {return userData;}
    default ArrayList<GameData> getGameData() {return gameData;}
    default ArrayList<AuthData> getAuthData() {return authData;}

    default void storeUserData(UserData ud) {
        userData.add(ud);
    }
    default UserData getUserData(String username) {
        boolean searching = true;
        int i = 0;
        while (searching && i < userData.size()) {
            if (userData.get(i).username().equals(username)) {
                searching = false;
                return userData.get(i);
            }
            i++;
        }
        return null;
    }
    default AuthData getAuthData(String authToken) throws DataAccessException{
        for (int i=0; i<authData.size(); i++) {
            if (authData.get(i).authToken().equals(authToken)) {
                return authData.get(i);
            }
        }
        throw new UnauthorizedException("Auth doesn't exist");
    }
    default GameData getGameData(int gameID) throws DataAccessException{
        for (int i=0; i<gameData.size(); i++) {
            if (gameData.get(i).gameID() == gameID) {
                return gameData.get(i);
            }
        }
        throw new DataAccessException("Game doesn't exist");
    }
    default void storeAuthData(AuthData ad) {
        authData.add(ad);
    }
    default void deleteAuthData(String authToken) throws DataAccessException {
        int i = authData.size()-1;
        while (i >= 0) {
            if (authData.get(i).authToken().equals(authToken)) {
                authData.remove(i);
                return;
            }
            i--;
        }
        throw new DataAccessException("Could not find auth data to delete");
    }
    default boolean checkAuthData(String authToken) {
        int i = 0;
        while (i < authData.size()) {
            if (authData.get(i).authToken().equals(authToken)) {
                return true;
            }
            i++;
        }
        return false;
    }
    default void storeGameData(GameData gd) {
        gameData.add(gd);
    }
    default void updateGameData(GameData gd) {
        boolean searching = true;
        int i=0;
        while (searching) {
            if (gameData.get(i).gameName().equals(gd.gameName())) {
                gameData.set(i, gd);
                searching = false;
            }
            i++;
        }
    }
    default boolean hasGameData(String gameName) {
        for (int i=0; i<gameData.size(); i++ ) {
            if (gameData.get(i).gameName().equals(gameName)) {
                return true;
            }
            i++;
        }
        return false;
    }
    default int getGameId() {
        return gameData.size()+1;
    }

    default void clearData() {
        userData.clear();
        gameData.clear();
        authData.clear();
    }
    default boolean isUserDataEmpty() {
        return userData.isEmpty();
    }
    default boolean isAuthDataEmpty() {
        return authData.isEmpty();
    }
    default boolean isGameDataEmpty() {
        return gameData.isEmpty();
    }
}
