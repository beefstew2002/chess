package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import service.reqres.*;

import java.util.UUID;

public class UserService {

    private static final UserDAO USER_DAO = new UserDAO();
    private static final AuthDAO AUTH_DAO = new AuthDAO();

    public static RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();


        RegisterResult result;

        if (password == null) {
            throw new DataAccessException("no password");
        }
        if (USER_DAO.getUserData(username) != null) {
            throw new UsernameAlreadyTaken("Error: already taken");
        }

        //Create user
        USER_DAO.createUser(username, password, email);

        //Create auth
        String auth = UUID.randomUUID().toString();
        AuthData ad = new AuthData(username, auth);
        AUTH_DAO.createAuth(ad);

        result = new RegisterResult(username, auth);


        return result;
    }

    public static LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        //Get the user data
        String username = loginRequest.username();
        String password = loginRequest.password();

        LoginResult result;

        //Verify the user exists
        UserData realUser = USER_DAO.getUser(username);
        if (realUser == null) {
            throw new DataAccessException("username doesn't exist");
        }
        //Verify the password is correct
        if (!realUser.password().equals(password)) {
            throw new WrongPasswordException("wrong password LOL");
        }
        //Create auth and add
        String auth = UUID.randomUUID().toString();
        AuthData ad = new AuthData(username, auth);
        AUTH_DAO.createAuth(ad);
        //Create result
        result = new LoginResult(username, ad.authToken());


        return result;
    }

    public static LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException {
        String authToken = logoutRequest.authToken();
        //Verify the authToken
        if (!AUTH_DAO.verifyAuth(authToken)) {
            throw new DataAccessException("You're trying to log out but either you never logged in or this user doesn't exist");
        }

        //Delete the authToken from the auth data
        AUTH_DAO.deleteAuth(authToken);

        //Create result
        return new LogoutResult();
    }

}
