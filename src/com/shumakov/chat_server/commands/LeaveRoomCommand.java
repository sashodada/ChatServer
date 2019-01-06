package com.shumakov.chat_server.commands;

import com.shumakov.chat_server.server.ServerChatRoom;
import com.shumakov.chat_server.server.ServerClientSession;

public class LeaveRoomCommand implements Command {

    private ServerClientSession caller;

    private static final String CORRECT_FORMAT = "@leave-room";

    private static final String ERROR_NO_SUCH_ROOM = "Not connected to any room yet.";
    private static final String SUCCESS_MESSAGE = "Disconnected from room!";

    public LeaveRoomCommand(ServerClientSession caller) {
        this.caller = caller;
    }

    public void execute() {
        if (caller.getUser().equals(ServerClientSession.NULL_USER)) {
            caller.sendMessage("You must first login to have access to this command.");
            return;
        }

        ServerChatRoom targetRoom = caller.getCurrentRoom();

        if (targetRoom == null) {
            caller.sendMessage(ERROR_NO_SUCH_ROOM);
            return;
        }

        targetRoom.disconnectUser(caller);
        caller.leaveRoom();
        caller.sendMessage(SUCCESS_MESSAGE);
    }

    public String getCorrectFormat() {
        return CORRECT_FORMAT;
    }

}
