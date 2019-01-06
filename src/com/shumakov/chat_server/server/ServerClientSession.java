package com.shumakov.chat_server.server;

import com.shumakov.chat_server.commands.Command;
import com.shumakov.chat_server.commands.QuitCommand;
import com.shumakov.chat_server.entities.Entity_SessionPeriod;
import com.shumakov.chat_server.entities.Entity_User;
import com.shumakov.chat_server.exceptions.UserNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerClientSession implements Runnable {

    public static final String NULL_USER = "null";

    private Server server;
    private Socket socket;

    private PrintWriter socketWriter;
    private BufferedReader socketReader;

    private EntityManager manager;

    private ServerChatRoom currentRoom;
    private String user;

    private Entity_User userEntity;
    private Entity_SessionPeriod currentConnection;

    public ServerClientSession(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;

        this.user = NULL_USER;
    }

    public void initClientSession() throws IOException {

        manager = Persistence
                .createEntityManagerFactory("NewPersistenceUnit")
                .createEntityManager();

        manager.setFlushMode(FlushModeType.AUTO);

        this.socketWriter = new PrintWriter(socket.getOutputStream(), true);
        this.socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void close() throws IOException{

        if (socketWriter != null) {
            socketWriter.close();
        }
        if (socketReader != null) {
            socketReader.close();
        }
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    public Server getServer() {
        return server;
    }

    public String getUser() {
        return user;
    }

    public ServerChatRoom getCurrentRoom() {
        return currentRoom;
    }

    public EntityManager getEntityManager() {
        return manager;
    }

    public Entity_User getUserEntity() {
        return userEntity;
    }

    public void login(Entity_User userEntity) {
        this.userEntity = userEntity;
        this.user = userEntity.getUsername();

        currentConnection = new Entity_SessionPeriod(this.userEntity);

        this.userEntity.addConnection(currentConnection);
    }

    public void logOut() {
        user = NULL_USER;

        currentConnection.closeConnection();

        manager.merge(userEntity);
        manager.merge(currentConnection);

        currentConnection = null;
        userEntity = null;
    }

    // Can send messages from other users and system simultaneously
    public void sendMessage(String message) {
       synchronized (socketWriter) {
           socketWriter.println(message);
       }
    }

    public void joinRoom(ServerChatRoom room) {
        this.currentRoom = room;
    }

    public void leaveRoom() {
        currentRoom = null;
    }

    public void kickFromRoom() {
        leaveRoom();
        sendMessage("Disconnected from room!");
    }

    public void run() {

        Command command;
        String input;

        try {
            sendMessage("Welcome to the chat server!");

            do {
                synchronized (socketReader) {
                    input = socketReader.readLine();
                }

                command = Command.parse(input, this);
                command.execute();

                System.out.println(command.getClass());

            } while (command.getClass() != QuitCommand.class);

        } catch (IOException e) {
            try {
                server.logoutUser(user);

                manager.getTransaction().begin();
                logOut();
                manager.getTransaction().commit();

            } catch (UserNotFoundException ex) {
                ex.printStackTrace();
            }
        } finally {
            if (manager != null && manager.isOpen()) {
               manager.close();
            }
        }
    }
}
