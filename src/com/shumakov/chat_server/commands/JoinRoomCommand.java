package com.shumakov.chat_server.commands;

import com.shumakov.chat_server.server.ServerChatRoom;
import com.shumakov.chat_server.server.ServerClientSession;
import com.shumakov.chat_server.exceptions.IllegalCommandFormatException;

public class JoinRoomCommand extends AbstractRoomCommand {

    private static final String CORRECT_FORMAT = "@join-room <roomName>";

    private static final String ERROR_MESSAGE = "No room with such a name exists!";
    private static final String SUCCESS_MESSAGE = "Connected to room!";

    JoinRoomCommand(String command, ServerClientSession caller) throws IllegalCommandFormatException {
        this.caller = caller;

        initRoomName(command);
    }

    public void execute() {
        if (caller.getUser().equals(ServerClientSession.NULL_USER)) {
            caller.sendMessage("You must first login to have access to this command.");
            return;
        }

        ServerChatRoom targetRoom = caller.getServer().getChatRooms().get(roomName);
        if (targetRoom == null) {
            caller.sendMessage(ERROR_MESSAGE);
            return;
        }

        targetRoom.connectUser(caller);
        caller.joinRoom(targetRoom);
        caller.sendMessage(SUCCESS_MESSAGE);
    }

    public String getCorrectFormat() {
        return CORRECT_FORMAT;
    }

}
