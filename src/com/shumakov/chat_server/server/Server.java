package com.shumakov.chat_server.server;

import com.shumakov.chat_server.entities.Entity_Message;
import com.shumakov.chat_server.entities.Entity_User;
import com.shumakov.chat_server.exceptions.*;

import javax.persistence.EntityManager;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class Server {

    private int port;
    private ExecutorService pool;
    private Map<String, ServerClientSession> connectedUsers;
    private Map<String, ServerChatRoom> chatRooms;

    public Server(int port) {
        this.port = port;
    }

    public void initializeServer() {
        pool = Executors.newFixedThreadPool(10);

        connectedUsers = new ConcurrentHashMap<>();
        chatRooms = new ConcurrentHashMap<>();
    }

    public void close() {
        pool.shutdownNow();
    }

    private static List<Entity_User> getUserEntityInList(String username, EntityManager manager) {
        return manager
                .createQuery("SELECT u FROM user u WHERE u.username = :username")
                .setParameter("username", username)
                .getResultList();
    }

    private static void persistMessage(String message, ServerClientSession sender, String receiver) {
        String senderRoomName = (sender.getCurrentRoom() == null) ? null : sender.getCurrentRoom().getRoomName();

        Entity_Message persistableMessage =
                new Entity_Message(message, sender.getUserEntity(), receiver, (receiver == null) ? senderRoomName : null);

        sender.getUserEntity().addMessage(persistableMessage);
        sender.getEntityManager().merge(sender.getUserEntity());
    }

    public boolean isUserRegistered(String username, EntityManager manager) {
        if (connectedUsers.containsKey(username)) {
            return true;
        }

        return !getUserEntityInList(username, manager).isEmpty();

//        synchronized (userDB) {
//            try (BufferedReader br = new BufferedReader(new FileReader(userDB))) {
//                return br.lines().anyMatch(s -> s.contains(username + " "));
//            } catch (IOException e) {
//                e.printStackTrace();
//                return false;
//            }
//        }
    }

    public ServerClientSession getUser(String username) {
        return connectedUsers.getOrDefault(username, null);
    }

    public void registerUser(String username, String password, EntityManager manager) {
        Entity_User newUser = new Entity_User(username, password.hashCode());
        manager.persist(newUser);
    }

    public void loginUser(String username, String password, ServerClientSession caller)
            throws UserAlreadyConnectedException, UserNotFoundException {

        if (connectedUsers.containsKey(username)) {
            throw new UserAlreadyConnectedException();
        }

        List<Entity_User> loggedUserContainer = getUserEntityInList(username, caller.getEntityManager());
        if (loggedUserContainer.isEmpty() || loggedUserContainer.get(0).getPassword() != password.hashCode()) {
            throw new UserNotFoundException();
        }

        Entity_User loggedUser = loggedUserContainer.get(0);
        caller.getEntityManager().merge(loggedUser);

        connectedUsers.put(username, caller);
        caller.login(loggedUser);
    }

    public void logoutUser(String user) throws UserNotFoundException {
        ServerClientSession targetUserSession = connectedUsers.get(user);

        if (targetUserSession == null) {
            throw new UserNotFoundException();
        }

        if (targetUserSession.getCurrentRoom() != null) {
            targetUserSession.getCurrentRoom().disconnectUser(targetUserSession);
        }

        connectedUsers.remove(user);
    }

    public void sendToAll(String message, ServerClientSession sender) {
        for (ServerClientSession receiver : connectedUsers.values()) {
            if (receiver != sender && receiver.getCurrentRoom() == null) {
                receiver.sendMessage(message);
            }
        }

        this.persistMessage(message, sender, null);
    }

    public void sendToRoom(String message, ServerClientSession sender) {
        ServerChatRoom currentRoom = sender.getCurrentRoom();

        if (currentRoom == null) {
            sendToAll(message, sender);
            return;
        }

        for (ServerClientSession receiver : currentRoom.getUserSet().values()) {
            if (receiver != sender) {
                receiver.sendMessage(sender.getUser() + ": " + message);
            }
        }
        persistMessage(message, sender, null);
    }

    public void sendPrivateMessage(String receivingUser, String message, ServerClientSession sender) throws UserNotFoundException {
        ServerClientSession receiver = connectedUsers.get(receivingUser);
        if (receiver != null) {
            persistMessage(message, sender, receiver.getUser());
            receiver.sendMessage("PM from " + sender.getUser() + ": " + message);
            return;
        }

        throw new UserNotFoundException();
    }

    public Stream<String> listUsers() {
        return connectedUsers.keySet().stream();
    }

    public Stream<String> listUsersInRoom(ServerChatRoom currentRoom) {
        if (currentRoom == null) return listUsers();
        return currentRoom.getUserSet().keySet().stream();
    }

    public Stream<String> listRooms() {
        return chatRooms.keySet().stream()
                .filter(s -> !chatRooms.get(s).getUserSet().isEmpty());
    }

    public void createRoom(String roomName, String adminName) throws RoomAlreadyExistsException {

        ServerChatRoom newRoom = new ServerChatRoom(roomName, adminName);

        ServerChatRoom existing = chatRooms.putIfAbsent(roomName, newRoom);

        if (existing != null) {
            throw new RoomAlreadyExistsException();
        }
    }

    public void deleteRoom(String roomName, ServerClientSession userSession) throws RoomNotFoundException, AdminAccessDeniedException {

        ServerChatRoom targetRoom = chatRooms.get(roomName);

        if (targetRoom == null) {
            throw new RoomNotFoundException();
        }

        if (!targetRoom.getAdminName().equals(userSession.getUser())) {
            throw new AdminAccessDeniedException();
        }

        targetRoom.getUserSet()
                .values()
                .stream()
                .filter(s -> s.getCurrentRoom() != null &&
                        s.getCurrentRoom().equals(targetRoom))
                .forEach(s -> s.kickFromRoom());

        chatRooms.remove(roomName);
    }

    public Map<String, ServerChatRoom> getChatRooms() {
        return chatRooms;
    }

    public boolean hasNoRooms() {
        for (ServerChatRoom room : chatRooms.values()) {
            if (!room.getUserSet().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public void run() throws IOException {
        ServerSocket ss = new ServerSocket(port);
        System.out.println("Server started!");

        while (true) {
            Socket s = ss.accept();
            ServerClientSession session = new ServerClientSession(s, this);

            try {
                session.initClientSession();
                pool.submit(session);
            } catch (IOException e) {
                session.close();
            }
        }
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.println("Booting new Chat Server instance...");

        System.out.println("Please, input port for this server:");
        String portNumber = in.nextLine();

        Server server = new Server(Integer.parseInt(portNumber));
        try {
            server.initializeServer();
            server.run();
        } catch (IOException e) {
            System.out.println("Server has crashed");
            server.close();
        }
    }
}