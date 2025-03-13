package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;

public interface DAInterface {


    default ArrayList<UserData> getUserData() {return null;}

    default  ArrayList<GameData> getGameData() {return null;}

    default ArrayList<AuthData> getAuthData() {return null;}

    default void createUser(String username, String password, String email) throws DataAccessException {}

    default  UserData getUser(String username) {return null;}

    default AuthData getAuth(String authToken) throws DataAccessException {return null;}

    default GameData getGame(int gameID) throws DataAccessException {return null;}

    default void createAuth(AuthData authData) throws DataAccessException {}

    default void deleteAuth(String authToken) throws DataAccessException {}

    default boolean verifyAuth(String authToken) {return false;}

    default int createGame(String gameName) {return 0;}

    default void updateGame(GameData gd) {}

    default boolean hasGame(String gameName) {return false;}

    default int getGameId() {return 0;}

    default void clearData() {}

    default boolean isEmpty() {return false;}

}
