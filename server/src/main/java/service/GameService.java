package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import service.RequestResult.*;

public class GameService {
    private static UserDAO udao = new UserDAO();
    private static AuthDAO adao = new AuthDAO();
    private static GameDAO gdao = new GameDAO();

    //public static ListResult list(ListRequest listRequest) {}
    public static ClearResult clear(ClearRequest clearRequest) {

        udao.clearData(); //The way it's written right now, a single clearData will erase everything
        adao.clearData(); //Including the others in case that changes later, I might rewrite the class
        gdao.clearData();

        return new ClearResult();
    }

    public static CreateResult create(CreateRequest createRequest) throws DataAccessException {
        String gameName = createRequest.gameName();
        String authToken = createRequest.authToken();

        //Check that authToken is verified, otherwise throw exception
        if (!adao.verifyAuth(authToken)) {
            throw new DataAccessException("You're not authorized");
        }

        //Check that gameName isn't taken, otherwise throw exception
        if (gdao.hasGame(gameName)) {
            throw new DataAccessException("That game already exists");
        }

        //Create a new game and add it to the database
        int gameId = gdao.createGame(gameName);

        //Create the CreateResult and return it
        CreateResult createResult = new CreateResult(gameId);
        return createResult;
    }
}
