package com.shumakov.chat_server.commands;

import com.shumakov.chat_server.server.ServerClientSession;
import com.shumakov.chat_server.exceptions.UserNotFoundException;

public class QuitCommand implements Command {

    private ServerClientSession caller;

    private static final String CORRECT_FORMAT = "@quit";

    private static final String SUCCESS_MESSAGE = "Disconnected from server. Goodbye!";

    public QuitCommand(ServerClientSession caller) {
        this.caller = caller;
    }

    public void execute() {

        caller.getEntityManager().getTransaction().begin();
        if (!(caller.getUser().equals(ServerClientSession.NULL_USER))) {
            try {
                caller.getServer().logoutUser(caller.getUser());
                caller.logOut();
                caller.sendMessage(SUCCESS_MESSAGE);
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }
        }
        caller.getEntityManager().getTransaction().commit();
    }

    public String getCorrectFormat() {
        return CORRECT_FORMAT;
    }
}
