package com.shumakov.chat_server.commands;

import com.shumakov.chat_server.server.ServerClientSession;

public class HelpCommand implements Command {

    private ServerClientSession caller;

    private static final String CORRECT_FORMAT = "@help";

    private static final String LIST_MESSAGE = "Available commands:";
    private static final String NOTE_MESSAGE = "Note: parameters in <> are meant to be filled by user.";

    private static final String CORRECT_FORMAT_REGISTER = "@register <username> <password>";
    private static final String CORRECT_FORMAT_LOGIN    = "@login <username> <password>";
    private static final String CORRECT_FORMAT_LOGOUT   = "@logout";
    private static final String CORRECT_FORMAT_QUIT     = "@quit";

    private static final String CORRECT_FORMAT_LIST_USERS = "@list-users";
    private static final String CORRECT_FORMAT_LIST_ROOMS = "@list-rooms";

    private static final String CORRECT_FORMAT_CREATE_ROOM = "@create-room <roomName>";
    private static final String CORRECT_FORMAT_DELETE_ROOM = "@delete-room <roomName>";
    private static final String CORRECT_FORMAT_JOIN_ROOM   = "@join-room <roomName>";
    private static final String CORRECT_FORMAT_LEAVE_ROOM  = "@leave-room";

    private static final String CORRECT_FORMAT_PRIVATE_MESSAGE = "@send-msg <receiverUsername> <message>";

    public HelpCommand(ServerClientSession caller) {
        this.caller = caller;
    }

    public void execute() {
        caller.sendMessage(LIST_MESSAGE);

        caller.sendMessage(CORRECT_FORMAT_REGISTER);
        caller.sendMessage(CORRECT_FORMAT_LOGIN);
        caller.sendMessage(CORRECT_FORMAT_LOGOUT);
        caller.sendMessage(CORRECT_FORMAT_QUIT);

        caller.sendMessage(CORRECT_FORMAT_LIST_USERS);
        caller.sendMessage(CORRECT_FORMAT_LIST_ROOMS);

        caller.sendMessage(CORRECT_FORMAT_CREATE_ROOM);
        caller.sendMessage(CORRECT_FORMAT_DELETE_ROOM);
        caller.sendMessage(CORRECT_FORMAT_JOIN_ROOM);
        caller.sendMessage(CORRECT_FORMAT_LEAVE_ROOM);

        caller.sendMessage(CORRECT_FORMAT_PRIVATE_MESSAGE);

        caller.sendMessage(NOTE_MESSAGE);
    }

    public String getCorrectFormat() {
        return CORRECT_FORMAT;
    }
}
