package com.shumakov.chat_server.commands;

import com.shumakov.chat_server.server.ServerClientSession;
import com.shumakov.chat_server.server.Server;

public class ListUsersCommand implements Command {

    private ServerClientSession caller;

    private static final String CORRECT_FORMAT = "@list-users";

    public ListUsersCommand(ServerClientSession caller) {
        this.caller = caller;
    }

    public void execute() {
        if (caller.getUser().equals(ServerClientSession.NULL_USER)) {
            caller.sendMessage("You must first login to have access to this command.");
            return;
        }

        Server callerServer = caller.getServer();

        callerServer.listUsersInRoom(caller.getCurrentRoom())
                .forEach(caller::sendMessage);
    }

    public String getCorrectFormat() {
        return CORRECT_FORMAT;
    }

}
