package com.shumakov.chat_server.commands;

import com.shumakov.chat_server.server.ServerClientSession;
import com.shumakov.chat_server.server.Server;

public class ListRoomsCommand implements Command {

    private ServerClientSession caller;

    private static final String CORRECT_FORMAT = "@list-rooms";

    private final static String ERROR_MESSAGE = "No rooms with connected users are currently on the server.";
    private final static String PRE_MESSAGE = "Connected users:";

    public ListRoomsCommand(ServerClientSession caller) {
        this.caller = caller;
    }

    public void execute() {
        if (caller.getUser().equals(ServerClientSession.NULL_USER)) {
            caller.sendMessage("You must first login to have access to this command.");
            return;
        }

        Server callerServer = caller.getServer();

        if (callerServer.hasNoRooms()) {
            caller.sendMessage(ERROR_MESSAGE);
            return;
        }

        caller.sendMessage(PRE_MESSAGE);
        callerServer.listRooms()
                .forEach(caller::sendMessage);
    }

    public String getCorrectFormat() {
        return CORRECT_FORMAT;
    }

}
