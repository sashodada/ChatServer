package com.shumakov.chat_server.tests;

import com.shumakov.chat_server.server.Server;
import com.shumakov.chat_server.exceptions.RoomAlreadyExistsException;
import com.shumakov.chat_server.exceptions.UserNotFoundException;
import com.shumakov.chat_server.commands.*;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

//import sun.misc.Unsafe;

public class ServerTests {

    private static Server server;
    private static EntityManager manager;

    @Before
    public void cleanTestDB() throws Exception {
        server = new Server(1984);
        server.initializeServer();

        manager = Persistence
                .createEntityManagerFactory("NewPersistenceUnit")
                .createEntityManager();

        try (FileWriter deleter = new FileWriter("testDB.txt")) {
            deleter.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void isNonexistentUserRegistered() {
        assertFalse(server.isUserRegistered("nonExistentUser", manager));
    }

    @Test(expected = UserNotFoundException.class)
    public void logoutNonLoggedUser() throws Exception {
        server.logoutUser("nonExistentUser");
    }

    @Test(expected = RoomAlreadyExistsException.class)
    public void createDuplicateRoom() throws RoomAlreadyExistsException, IOException {
        server.createRoom("newRoom", "pseudoAdmin");
        server.createRoom("newRoom", "pseudoAdmin");
    }

    @Test
    public void serverHasNoRooms() {
        assertTrue(server.hasNoRooms());
    }

    @Test
    public void serverHasNoRoomsWithOneEmptyRoom() throws Exception {
        server.createRoom("newRoom", "pseudoAdmin");
        assertTrue(server.hasNoRooms());
    }

    @Test
    public void listOfEmptyRoomsIsEmpty() throws Exception {
        server.createRoom("newRoom", "pseudoAdmin");
        assertTrue(server.listRooms().collect(Collectors.toList()).isEmpty());
    }

    @Test
    public void parseRegisterCommand() {
        Command command = Command.parse("@register username password", null);
        assertEquals(RegisterCommand.class, command.getClass());
    }

    @Test
    public void parseMalformedRegisterCommand() {
        Command command = Command.parse("@register username", null);
        assertEquals(UnknownCommand.class, command.getClass());
    }

    @Test
    public void parseLogoutCommand() {
        Command command = Command.parse("@logout", null);
        assertEquals(LogoutCommand.class, command.getClass());
    }

    @Test
    public void parseQuitCommand() {
        Command command = Command.parse("@quit", null);
        assertEquals(QuitCommand.class, command.getClass());
    }

    @Test
    public void parseListUsersCommand() {
        Command command = Command.parse("@list-users", null);
        assertEquals(ListUsersCommand.class, command.getClass());
    }

    @Test
    public void parseMalformedLoginCommand() {
        Command command = Command.parse("@list-rooms", null);
        assertEquals(ListRoomsCommand.class, command.getClass());
    }

    @Test
    public void parseCreateRoomCommand() {
        Command command = Command.parse("@create-room testRoom", null);
        assertEquals(CreateRoomCommand.class, command.getClass());
    }

    @Test
    public void parseMalformedCreateRoomCommand() {
        Command command = Command.parse("@create-room   ", null);
        assertEquals(UnknownCommand.class, command.getClass());
    }

    @Test
    public void parseJoinRoomCommand() {
        Command command = Command.parse("@join-room testRoom", null);
        assertEquals(JoinRoomCommand.class, command.getClass());
    }

    @Test
    public void parseMalformedJoinRoomCommand() {
        Command command = Command.parse("@join-room   ", null);
        assertEquals(UnknownCommand.class, command.getClass());
    }

    @Test
    public void parseDeleteRoomCommand() {
        Command command = Command.parse("@delete-room tempRoom", null);
        assertEquals(DeleteRoomCommand.class, command.getClass());
    }

    @Test
    public void parseMalformedDeleteRoomCommand() {
        Command command = Command.parse("@delete-room   ", null);
        assertEquals(UnknownCommand.class, command.getClass());
    }

    @Test
    public void parsePrivateMessageCommand() {
        Command command = Command.parse("@send-msg username many words of message", null);
        assertEquals(PrivateMessageCommand.class, command.getClass());
    }

    @Test
    public void parseMalformedPrivateMessageCommand() {
        Command command = Command.parse("@send-msg user", null);
        assertEquals(UnknownCommand.class, command.getClass());
    }
}