package com.shumakov.chat_server.commands;

import com.shumakov.chat_server.server.ServerClientSession;
import com.shumakov.chat_server.exceptions.IllegalCommandFormatException;

public interface Command {

    void execute();
    String getCorrectFormat();

    static Command parse(String command, ServerClientSession caller) {

        StringBuilder commandType = new StringBuilder();
        boolean metNonDelimiter = false;

        for (int i = 0; i < command.length(); ++i) {
            if (command.charAt(i) == ' ') {
                if (metNonDelimiter) {
                    return getCommand(commandType.toString(), command.substring(i), caller);
                }

                continue;
            }

            metNonDelimiter = true;
            commandType.append(command.charAt(i));
        }

        if (metNonDelimiter) {
            return getCommand(commandType.toString(), "", caller);
        }

        return new UnknownCommand(caller, null);
    }

    static Command getCommand(String commandType, String command, ServerClientSession caller) {
        try{
            switch (commandType) {

                case "@help" : return new HelpCommand(caller);

                case "@register" : return new RegisterCommand(command, caller);
                case "@login"    : return new LoginCommand   (command, caller);
                case "@logout"   : return new LogoutCommand  (caller);
                case "@quit"     : return new QuitCommand    (caller);

                case "@list-users" : return new ListUsersCommand (caller);
                case "@list-rooms" : return new ListRoomsCommand (caller);

                case "@create-room" : return new CreateRoomCommand(command, caller);
                case "@delete-room" : return new DeleteRoomCommand(command, caller);
                case "@join-room"   : return new JoinRoomCommand  (command, caller);
                case "@leave-room"  : return new LeaveRoomCommand (caller);

                case "@send-msg"  : return new PrivateMessageCommand(command, caller);

                default : return new MessageCommand(commandType + command, caller);
            }
        } catch (IllegalCommandFormatException e) {
            return new UnknownCommand(caller, e.getFailedCommand().getCorrectFormat());
        }
    }

}
