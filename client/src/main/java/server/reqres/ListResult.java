package server.reqres;

import model.GameMetaData;

import java.util.ArrayList;

public record ListResult(ArrayList<GameMetaData> games) {
}