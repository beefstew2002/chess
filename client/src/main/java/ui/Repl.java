package ui;

/*
import client.websocket.NotificationHandler;
import webSocketMessages.Notification;
 */

import model.GameData;
import websocket.ServerMessageObserver;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements ServerMessageObserver {
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl, this);
    }

    public void run() {
        System.out.println("Welcome to Chess. Sign in to start.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public void notify(NotificationMessage message) {
        System.out.println(SET_TEXT_COLOR_BLUE + message.getMessage());
        printPrompt();
    }
    public void error(ErrorMessage message) {
        System.out.println(SET_TEXT_COLOR_RED + message.getMessage());
        printPrompt();
    }
    public void loadGame(LoadGameMessage message) {
        GameData game = message.game();
        client.loadGame(game);
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }
}
