package dataaccess;

import model.AuthData;
import model.UserData;
import service.RequestResult.RegisterResult;

import java.util.UUID;

public class AuthDAO implements DAInterface{
    //Methods I need in this:
    //createAuth
    //getAuth
    //deleteAuth

    public void createAuth(AuthData authData) throws DataAccessException {
        storeAuthData(authData);
    }

    public boolean verifyAuth(AuthData authToken) {
        return checkAuthData(authToken);
    }
}
