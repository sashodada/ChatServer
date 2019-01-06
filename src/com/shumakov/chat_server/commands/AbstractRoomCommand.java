package com.shumakov.chat_server.commands;

import com.shumakov.chat_server.server.ServerClientSession;
import com.shumakov.chat_server.exceptions.IllegalCommandFormatException;

public abstract class AbstractRoomCommand implements Command {

    protected ServerClientSession caller;
    protected String roomName;

    protected void initRoomName(String command) throws IllegalCommandFormatException {

        boolean metNonDelimiter = false;
        StringBuilder roomNameBuilder = new StringBuilder();

        for (int i = 0; i < command.length(); ++i) {
            if (command.charAt(i) == ' ') {
                if (metNonDelimiter) {
                    roomName = roomNameBuilder.toString();
                    return;
                }
                continue;
            }

            metNonDelimiter = true;
            roomNameBuilder.append(command.charAt(i));

        }

        if(metNonDelimiter) {
            roomName = roomNameBuilder.toString();
            return;
        }

        throw new IllegalCommandFormatException(this);
    }

    public abstract void execute();
    public abstract String getCorrectFormat();

}
