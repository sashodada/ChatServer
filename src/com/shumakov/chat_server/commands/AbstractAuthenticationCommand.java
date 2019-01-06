package com.shumakov.chat_server.commands;

import com.shumakov.chat_server.server.ServerClientSession;
import com.shumakov.chat_server.exceptions.IllegalCommandFormatException;

public abstract class AbstractAuthenticationCommand implements Command {

    protected ServerClientSession caller;

    protected String username;
    protected String password;

    protected void initParameters(String command) throws IllegalCommandFormatException {

        boolean metNonDelimiter = false;
        StringBuilder usernameBuilder = new StringBuilder();

        for (int i = 0; i < command.length(); ++i) {
            if (command.charAt(i) == ' ') {
                if (metNonDelimiter) {
                    username = usernameBuilder.toString();
                    initPassword(command.substring(i));
                    return;
                }
                continue;
            }

            metNonDelimiter = true;
            usernameBuilder.append(command.charAt(i));

        }

        throw new IllegalCommandFormatException(this);
    }

    private void initPassword(String command) throws IllegalCommandFormatException {

        boolean metNonDelimiter = false;
        StringBuilder passwordBuilder = new StringBuilder();

        for (int i = 0; i < command.length(); ++i) {
            if (command.charAt(i) == ' ') {
                if (metNonDelimiter) {
                    break;
                }
                continue;
            }

            metNonDelimiter = true;
            passwordBuilder.append(command.charAt(i));
        }

        if (metNonDelimiter) {
            password = passwordBuilder.toString();
            return;
        }

        throw new IllegalCommandFormatException(this);
    }

    public abstract void execute();
    public abstract String getCorrectFormat();
}
