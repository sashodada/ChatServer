package com.shumakov.chat_server.commands;

import com.shumakov.chat_server.server.ServerClientSession;

public class UnknownCommand implements Command {

    private ServerClientSession caller;
    private String correctFormat;

    private final static String ERROR_MESSAGE = "Unknown command.";
    private final static String MAYBE_MESSAGE = "Maybe you meant: ";
    private final static String HELP_MESSAGE = "If you don't remember the commands available, please type \"@help\"";

    public UnknownCommand(ServerClientSession caller, String correctFormat) {
        this.caller = caller;
        this.correctFormat = correctFormat;
    }

    public void execute() {
        caller.sendMessage(ERROR_MESSAGE);
        if (correctFormat != null) {
            caller.sendMessage(MAYBE_MESSAGE + correctFormat);
        }
        caller.sendMessage(HELP_MESSAGE);
    }

    public String getCorrectFormat() {
        return null;
    }

}
