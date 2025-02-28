package dataaccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.RequestResult.RegisterResult;

import java.util.UUID;

public class UserDAO implements DAInterface{
    //Methods this will require:
    //createUser
    //getUser

    public model.UserData getUser(String username) {
        return getUserData(username);
    }
    public void createUser(String username, String password, String email) throws DataAccessException {


        UserData newUser = new UserData(username, password, email);
        storeUserData(newUser);

    }
}
