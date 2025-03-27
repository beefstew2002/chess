package service;

import dataaccess.*;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.UnauthorizedException;
import model.GameData;
import model.GameMetaData;
import service.reqres.*;

import java.util.ArrayList;
import java.util.Arrays;

public class GameService {
    private static final UserDAO USER_DAO = new UserDAO();
    private static final AuthDAO AUTH_DAO = new AuthDAO();
    private static final GameDAO GAME_DAO = new GameDAO();

    public static ClearResult clear(ClearRequest clearRequest) {

        USER_DAO.clearData();
        AUTH_DAO.clearData();
        GAME_DAO.clearData();

        return new ClearResult();
    }

    public static CreateResult create(CreateRequest createRequest) throws DataAccessException {
        String gameName = createRequest.gameName();
        String authToken = createRequest.authToken();

        //Check that authToken is verified, otherwise throw exception
        if (!AUTH_DAO.verifyAuth(authToken)) {
            throw new UnauthorizedException("You're not authorized");
        }

        //Check that gameName isn't taken, otherwise throw exception
        if (GAME_DAO.hasGame(gameName)) {
            throw new DataAccessException("That game already exists");
        }

        //Create a new game and add it to the database
        int gameID = GAME_DAO.createGame(gameName);

        //Create the CreateResult and return it
        return new CreateResult(gameID);
    }

    public static ListResult list(ListRequest listRequest) throws DataAccessException {
        String authToken = listRequest.authToken();

        //Check that authToken is verified, otherwise throw exception
        if (!AUTH_DAO.verifyAuth(authToken)) {
            throw new DataAccessException("You're not authorized");
        }

        //Create a new listResult with the list data
        ArrayList<GameMetaData> games = GAME_DAO.listGames();

        return new ListResult(games);
    }

    public static JoinResult join(JoinRequest joinRequest) throws DataAccessException {
        String authToken = joinRequest.authToken();
        String username = AUTH_DAO.getAuth(authToken).username(); //This throws an error if unauthorized
        String color = joinRequest.playerColor().toUpperCase();
        int gameID = joinRequest.gameID();


        //Get the game you're looking for
        GameData game = GAME_DAO.getGame(gameID);
        GameData newGame;

        String[] colors = {"WHITE", "BLACK", "WHITE/BLACK"};
        if (!Arrays.asList(colors).contains(color)) {
            throw new BadRequestException("That's not a color");
        }

        if ((color.equals("WHITE/BLACK") || color.equals("WHITE")) && game.whiteUsername() == null) {
            newGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
            GAME_DAO.updateGame(newGame);
        } else if ((color.equals("WHITE/BLACK") || color.equals("BLACK")) && game.blackUsername() == null) {
            newGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
            GAME_DAO.updateGame(newGame);
        } else {
            throw new DataAccessException("This game is full");
        }

        return new JoinResult();

    }
}
