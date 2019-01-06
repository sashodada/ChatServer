package com.shumakov.chat_server.commands;

import com.shumakov.chat_server.server.ServerClientSession;
import com.shumakov.chat_server.exceptions.IllegalCommandFormatException;
import com.shumakov.chat_server.server.Server;
import com.shumakov.chat_server.exceptions.UserAlreadyConnectedException;
import com.shumakov.chat_server.exceptions.UserNotFoundException;

public class LoginCommand extends AbstractAuthenticationCommand {

    private static final String CORRECT_FORMAT = "@login <username> <password>";

    private static final String SUCCESS_MESSAGE = "Login successful!";

    private static final String ERROR_NOT_FOUND = "Such a user does not exist.";
    private static final String ERROR_ALREADY_CONNECTED = "This user is already connected.";

    public LoginCommand(String command, ServerClientSession caller) throws IllegalCommandFormatException {
        this.caller = caller;

        initParameters(command);
    }

    public void execute() {
        if (!caller.getUser().equals(ServerClientSession.NULL_USER)) {
            caller.sendMessage("You have already logged in");
            return;
        }

        caller.getEntityManager().getTransaction().begin();
        Server targetServer = caller.getServer();

        try {
            targetServer.loginUser(username, password, caller);
            caller.sendMessage(SUCCESS_MESSAGE);
            caller.getEntityManager().getTransaction().commit();

        } catch (UserNotFoundException e) {
            caller.sendMessage(ERROR_NOT_FOUND);
            caller.getEntityManager().getTransaction().rollback();

        } catch (UserAlreadyConnectedException e) {
            caller.sendMessage(ERROR_ALREADY_CONNECTED);
            caller.getEntityManager().getTransaction().rollback();

        } catch (Exception e) {
            e.printStackTrace();
            caller.getEntityManager().getTransaction().rollback();
        }
    }

    public String getCorrectFormat() {
        return CORRECT_FORMAT;
    }

}
