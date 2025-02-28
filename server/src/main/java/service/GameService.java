package service;

import service.RequestResult.*;

public class GameService {
    //public static CreateResult create(CreateRequest createRequest) {}
    //public static ListResult list(ListRequest listRequest) {}
    public static ClearResult clear(ClearRequest clearRequest) {
        return new ClearResult();
    }
}
