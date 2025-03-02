package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;

public interface DAInterface {

    ArrayList<UserData> userData = new ArrayList<>();
    ArrayList<GameData> gameData = new ArrayList<>();
    ArrayList<AuthData> authData = new ArrayList<>();

    default ArrayList<UserData> getUserData() {
        return userData;
    }

    default ArrayList<GameData> getGameData() {
        return gameData;
    }

    default ArrayList<AuthData> getAuthData() {
        return authData;
    }

    default void storeUserData(UserData ud) {
        userData.add(ud);
    }

    default UserData getUserData(String username) {
        for (int i = 0; i < userData.size(); i++) {
            if (userData.get(i).username().equals(username)) {
                return userData.get(i);
            }
        }
        return null;
    }

    default AuthData getAuthData(String authToken) throws DataAccessException {
        for (AuthData ad : authData) {
            if (ad.authToken().equals(authToken)) {
                return ad;
            }
        }
        throw new UnauthorizedException("Auth doesn't exist");
    }

    default GameData getGameData(int gameID) throws DataAccessException {
        for (GameData gd : gameData) {
            if (gd.gameID() == gameID) {
                return gd;
            }
        }
        throw new DataAccessException("Game doesn't exist");
    }

    default void storeAuthData(AuthData ad) {
        authData.add(ad);
    }

    default void deleteAuthData(String authToken) throws DataAccessException {
        int i = authData.size() - 1;
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
        int i = 0;
        while (searching) {
            if (gameData.get(i).gameName().equals(gd.gameName())) {
                gameData.set(i, gd);
                searching = false;
            }
            i++;
        }
    }

    default boolean hasGameData(String gameName) {
        for (int i = 0; i < gameData.size(); i++) {
            if (gameData.get(i).gameName().equals(gameName)) {
                return true;
            }
            i++;
        }
        return false;
    }

    default int getGameId() {
        return gameData.size() + 1;
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
