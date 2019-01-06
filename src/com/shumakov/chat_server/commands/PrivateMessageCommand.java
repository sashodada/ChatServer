package com.shumakov.chat_server.commands;

import com.shumakov.chat_server.server.ServerClientSession;
import com.shumakov.chat_server.exceptions.IllegalCommandFormatException;
import com.shumakov.chat_server.exceptions.UserNotFoundException;

public class PrivateMessageCommand implements Command {

    private ServerClientSession caller;
    private String receivingUser;
    private String message;

    private static final String CORRECT_FORMAT = "@send-msg <receivingUser> <message>";

    private static final String ERROR_MESSAGE = "Error! No such user currently connected to server";

    public PrivateMessageCommand(String command, ServerClientSession caller) throws IllegalCommandFormatException {
        this.caller = caller;

        initReceivingUser(command);
    }

    private void initReceivingUser(String command) throws IllegalCommandFormatException {
        boolean metNonDelimiter = false;
        StringBuilder receivingUserBuilder = new StringBuilder();

        for (int i = 0; i < command.length(); ++i) {
            if (command.charAt(i) == ' ') {
                if (metNonDelimiter) {
                    receivingUser = receivingUserBuilder.toString();
                    initMessage(command.substring(i));
                    return;
                }
                continue;
            }

            metNonDelimiter = true;
            receivingUserBuilder.append(command.charAt(i));
        }

        throw new IllegalCommandFormatException(this);
    }

    private void initMessage(String command) throws IllegalCommandFormatException {
        int firstNonDelimiterIndex = -1;

        for (int i = 0; i < command.length() && firstNonDelimiterIndex == -1; ++i) {
            if (command.charAt(i) != ' ') {
                firstNonDelimiterIndex = i;
            }
        }

        if(firstNonDelimiterIndex != -1) {
            message = command.substring(firstNonDelimiterIndex);
            return;
        }

        throw new IllegalCommandFormatException(this);
    }

    public void execute() {
        if (caller.getUser().equals(ServerClientSession.NULL_USER)) {
            caller.sendMessage("You must first login to have access to this command.");
            return;
        }

        caller.getEntityManager().getTransaction().begin();
        try {
            caller.getServer().sendPrivateMessage(receivingUser, message, caller);
            caller.getEntityManager().getTransaction().commit();
        } catch (UserNotFoundException e) {
            caller.sendMessage(ERROR_MESSAGE);
            caller.getEntityManager().getTransaction().rollback();
        }
    }

    public String getCorrectFormat() {
        return CORRECT_FORMAT;
    }
}