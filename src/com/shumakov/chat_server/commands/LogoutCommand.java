package com.shumakov.chat_server.commands;

import com.shumakov.chat_server.server.ServerClientSession;
import com.shumakov.chat_server.exceptions.UserNotFoundException;

public class LogoutCommand implements Command{

    private ServerClientSession caller;

    private static final String CORRECT_FORMAT = "@logout";

    private final static String ERROR_NOT_LOGGED_IN = "Not logged in yet.";
    private final static String ERROR_NO_SUCH_USER = "No such user found.";

    private final static String SUCCESS_MESSAGE = "Successfully logged out!";

    public LogoutCommand(ServerClientSession caller) {
        this.caller = caller;
    }

    public void execute() {

        if (caller.getUser().equals(ServerClientSession.NULL_USER)) {
            caller.sendMessage(ERROR_NOT_LOGGED_IN);
            return;
        }

        caller.getEntityManager().getTransaction().begin();

        try {
            caller.getServer().logoutUser(caller.getUser());
        } catch (UserNotFoundException e) {
            caller.sendMessage(ERROR_NO_SUCH_USER);
            caller.getEntityManager().getTransaction().rollback();
            return;
        }

        caller.logOut();
        caller.sendMessage(SUCCESS_MESSAGE);

        caller.getEntityManager().getTransaction().commit();
    }

    public String getCorrectFormat() {
        return CORRECT_FORMAT;
    }
}
