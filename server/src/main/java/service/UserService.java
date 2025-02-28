package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.UsernameAlreadyTaken;
import model.AuthData;
import service.RequestResult.*;

import java.util.UUID;

public class UserService {
    public static RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();

        UserDAO udao = new UserDAO();

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

            result = new RegisterResult(username, auth);

        } catch (DataAccessException e) {
            throw e;
        }
        return result;
    }
    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {

        LoginResult result = new LoginResult("","");

        return result;
    }
    //public void logout(LogoutRequest logoutRequest) {}
    //public JoinResult join(JoinRequest joinRequest) {}
}
