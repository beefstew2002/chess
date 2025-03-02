package dataaccess;

import model.AuthData;

public class AuthDAO implements DAInterface {

    public void createAuth(AuthData authData) throws DataAccessException {
        storeAuthData(authData);
    }

    public boolean verifyAuth(String authToken) {
        return checkAuthData(authToken);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        return getAuthData(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        deleteAuthData(authToken);
    }

    public boolean isEmpty() {
        return isAuthDataEmpty();
    }
}
