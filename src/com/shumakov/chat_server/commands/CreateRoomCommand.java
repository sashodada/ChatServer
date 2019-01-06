package com.shumakov.chat_server.commands;

import com.shumakov.chat_server.server.ServerClientSession;
import com.shumakov.chat_server.exceptions.RoomAlreadyExistsException;
import com.shumakov.chat_server.exceptions.IllegalCommandFormatException;

public class CreateRoomCommand extends AbstractRoomCommand {

    private static final String CORRECT_FORMAT = "@create-room <roomName>";

    private static final String ERROR_MESSAGE = "Room with such a name already exists.";
    private static final String ERROR_SERVER_MESSAGE = "Room could not be created...";
    private static final String SUCCESS_MESSAGE = "Room created successfully!";

    CreateRoomCommand(String command, ServerClientSession caller) throws IllegalCommandFormatException {
        this.caller = caller;

        initRoomName(command);
    }

    public void execute() {
        if (caller.getUser().equals(ServerClientSession.NULL_USER)) {
            caller.sendMessage("You must first login to have access to this command.");
            return;
        }

        try {
            caller.getServer().createRoom(roomName, caller.getUser());
            caller.sendMessage(SUCCESS_MESSAGE);
        } catch (RoomAlreadyExistsException e) {
            caller.sendMessage(ERROR_MESSAGE);
        }

    }

    public String getCorrectFormat() {
        return CORRECT_FORMAT;
    }
}
