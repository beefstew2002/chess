package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import service.RequestResult.*;

import java.util.UUID;

public class UserService {

    private static UserDAO udao = new UserDAO();
    private static AuthDAO adao = new AuthDAO();

    public static RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();


        RegisterResult result = null;
        try {
            if (udao.getUserData(username) != null) {
                throw new UsernameAlreadyTaken("Error: already taken");
            }

            //Create user
            udao.createUser(username, password, email);

            //Create auth
            String auth = UUID.randomUUID().toString();
            AuthData ad = new AuthData(username, auth);
            adao.createAuth(ad);

            result = new RegisterResult(username, auth);

        } catch (DataAccessException e) {
            throw e;
        }
        return result;
    }
    public static LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        //Get the user data
        String username = loginRequest.username();
        String password = loginRequest.password();

        LoginResult result = null;

        //Verify the user exists
        UserData realUser = udao.getUser(username);
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
        adao.createAuth(ad);
        //Create result
        result = new LoginResult(username, ad.authToken());


        return result;
    }
    public static LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException{
        String authToken = logoutRequest.authToken();
        //Verify the authToken
        if (!adao.verifyAuth(authToken)) {
            throw new DataAccessException("You're trying to log out but either you never logged in or this user doesn't exist");
        }

        //Delete the authToken from the auth data
        adao.deleteAuth(authToken);

        //Create result
        LogoutResult logoutResult = new LogoutResult();
        return logoutResult;
    }
    //public JoinResult join(JoinRequest joinRequest) {}
}
