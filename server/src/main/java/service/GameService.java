package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import service.RequestResult.*;

public class GameService {
    //public static CreateResult create(CreateRequest createRequest) {}
    //public static ListResult list(ListRequest listRequest) {}
    public static ClearResult clear(ClearRequest clearRequest) {
        UserDAO udao = new UserDAO();
        AuthDAO adao = new AuthDAO();
        GameDAO gdao = new GameDAO();

        udao.clearData(); //The way it's written right now, a single clearData will erase everything
        adao.clearData(); //Including the others in case that changes later, I might rewrite the class
        gdao.clearData();

        return new ClearResult();
    }
}
