package com.shumakov.chat_server.commands;

import com.shumakov.chat_server.server.ServerClientSession;
import com.shumakov.chat_server.exceptions.IllegalCommandFormatException;
import com.shumakov.chat_server.server.Server;

public class RegisterCommand extends AbstractAuthenticationCommand {

    private static final String CORRECT_FORMAT = "@register <username> <password>";

    private static final String ERROR_NOT_FOUND_MESSAGE = "User with such a username has already been registered!";
    private static final String ERROR_SERVER_FAIL = "Oops! We have encountered an error with your request. Please, try again";
    private static final String SUCCESS_MESSAGE = "Registration successful!";

    public RegisterCommand(String command, ServerClientSession caller) throws IllegalCommandFormatException {
        this.caller = caller;

        initParameters(command);

    }

    public void execute() {
        if (!caller.getUser().equals(ServerClientSession.NULL_USER)) {
            caller.sendMessage("Cannot register when already logged in!");
            return;
        }

        Server targetServer = caller.getServer();

        if (targetServer.isUserRegistered(username, caller.getEntityManager())) {
            caller.sendMessage(ERROR_NOT_FOUND_MESSAGE);
            return;
        }

        caller.getEntityManager().getTransaction().begin();
        try {
            targetServer.registerUser(username, password, caller.getEntityManager());
        } finally {
            caller.getEntityManager().getTransaction().commit();
        }

        caller.sendMessage(SUCCESS_MESSAGE);
    }

    public String getCorrectFormat() {
        return CORRECT_FORMAT;
    }
}
