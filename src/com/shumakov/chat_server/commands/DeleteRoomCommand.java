package com.shumakov.chat_server.commands;

import com.shumakov.chat_server.server.ServerClientSession;
import com.shumakov.chat_server.exceptions.AdminAccessDeniedException;
import com.shumakov.chat_server.exceptions.RoomNotFoundException;
import com.shumakov.chat_server.exceptions.IllegalCommandFormatException;

public class DeleteRoomCommand extends AbstractRoomCommand {

    private static final String CORRECT_FORMAT = "@delete-room <roomName>";

    private static final String SUCCESS_MESSAGE = "Successfully deleted room!";
    private static final String ERROR_NOT_FOUND_MESSAGE = "No such room found.";
    private static final String ERROR_NO_RIGHTS_MESSAGE = "You cannot delete a room you are not an admin of!";

    DeleteRoomCommand(String command, ServerClientSession caller) throws IllegalCommandFormatException {
        this.caller = caller;

        initRoomName(command);
    }

    public void execute() {
        if (caller.getUser().equals(ServerClientSession.NULL_USER)) {
            caller.sendMessage("You must first login to have access to this command.");
            return;
        }

        try {
            caller.getServer().deleteRoom(roomName, caller);
            caller.sendMessage(SUCCESS_MESSAGE);
        } catch (RoomNotFoundException e) {
            caller.sendMessage(ERROR_NOT_FOUND_MESSAGE);
        } catch (AdminAccessDeniedException e) {
            caller.sendMessage(ERROR_NO_RIGHTS_MESSAGE);
        }
    }

    public String getCorrectFormat() {
        return CORRECT_FORMAT;
    }
}
