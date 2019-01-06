package com.shumakov.chat_server.exceptions;

import com.shumakov.chat_server.commands.Command;

public class IllegalCommandFormatException extends Exception {

    private Command failedCommand;

    public IllegalCommandFormatException(Command failedCommand) {
        this.failedCommand = failedCommand;
    }

    public Command getFailedCommand() {
        return failedCommand;
    }
}
