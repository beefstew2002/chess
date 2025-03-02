package service;

import dataaccess.*;
import model.GameData;
import model.GameMetaData;
import service.RequestResult.*;

import java.util.ArrayList;
import java.util.Arrays;

public class GameService {
    private static final UserDAO udao = new UserDAO();
    private static final AuthDAO adao = new AuthDAO();
    private static final GameDAO gdao = new GameDAO();

    public static ClearResult clear(ClearRequest clearRequest) {

        udao.clearData();
        adao.clearData();
        gdao.clearData();

        return new ClearResult();
    }

    public static CreateResult create(CreateRequest createRequest) throws DataAccessException {
        String gameName = createRequest.gameName();
        String authToken = createRequest.authToken();

        //Check that authToken is verified, otherwise throw exception
        if (!adao.verifyAuth(authToken)) {
            throw new UnauthorizedException("You're not authorized");
        }

        //Check that gameName isn't taken, otherwise throw exception
        if (gdao.hasGame(gameName)) {
            throw new DataAccessException("That game already exists");
        }

        //Create a new game and add it to the database
        int gameID = gdao.createGame(gameName);

        //Create the CreateResult and return it
        return new CreateResult(gameID);
    }

    public static ListResult list(ListRequest listRequest) throws DataAccessException {
        String authToken = listRequest.authToken();

        //Check that authToken is verified, otherwise throw exception
        if (!adao.verifyAuth(authToken)) {
            throw new DataAccessException("You're not authorized");
        }

        //Create a new listResult with the list data
        ArrayList<GameMetaData> games = gdao.listGames();

        return new ListResult(games);
    }

    public static JoinResult join(JoinRequest joinRequest) throws DataAccessException {
        String authToken = joinRequest.authToken();
        String username = adao.getAuth(authToken).username(); //This throws an error if unauthorized
        String color = joinRequest.playerColor();
        int gameID = joinRequest.gameID();


        //Get the game you're looking for
        GameData game = gdao.getGame(gameID);
        GameData newGame;

        String[] colors = {"WHITE", "BLACK", "WHITE/BLACK"};
        if (!Arrays.asList(colors).contains(color)) {
            throw new BadRequestException("That's not a color");
        }

        if ((color.equals("WHITE/BLACK") || color.equals("WHITE")) && game.whiteUsername() == null) {
            newGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
            gdao.updateGame(newGame);
        } else if ((color.equals("WHITE/BLACK") || color.equals("BLACK")) && game.blackUsername() == null) {
            newGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
            gdao.updateGame(newGame);
        } else {
            throw new DataAccessException("This game is full");
        }

        return new JoinResult();

    }
}
