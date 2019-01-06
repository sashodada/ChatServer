package com.shumakov.chat_server.commands;

import com.shumakov.chat_server.server.ServerClientSession;

public class MessageCommand implements Command {

    private ServerClientSession caller;
    private String message;

    private static final String CORRECT_FORMAT = "<message>";

    public MessageCommand(String message, ServerClientSession caller) {
        this.message = message;
        this.caller = caller;
    }

    public void execute() {
        if (caller.getUser().equals(ServerClientSession.NULL_USER)) {
            new UnknownCommand(caller, null).execute();
            return;
        }

        caller.getEntityManager().getTransaction().begin();
        caller.getServer().sendToRoom(message, caller);
        caller.getEntityManager().getTransaction().commit();
    }

    public String getCorrectFormat() {
        return CORRECT_FORMAT;
    }
}
