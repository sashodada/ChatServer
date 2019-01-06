package com.shumakov.chat_server.server;

import com.shumakov.chat_server.entities.Entity_Message;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServerChatRoom {

    private String roomName;
    private String adminName;
    private Map<String, ServerClientSession> userSet;

    private static final String GET_MESSAGES_QUERY = "SELECT m FROM message m WHERE m.roomName = :roomname";

    public ServerChatRoom(String roomName, String adminName) {
        this.roomName = roomName;
        this.adminName = adminName;

        this.userSet = new ConcurrentHashMap<>();
        this.userSet.clear();
    }

    public String getRoomName() {
        return roomName;
    }

    public String getAdminName() {
        return adminName;
    }

    public Map<String, ServerClientSession> getUserSet() {
        return userSet;
    }

    public void connectUser(ServerClientSession userSession) {
        userSet.put(userSession.getUser(), userSession);

        List<Entity_Message> messageList =
                userSession
                .getEntityManager()
                .createQuery(GET_MESSAGES_QUERY)
                .setParameter("roomname", roomName)
                .getResultList();

        messageList.forEach(m -> userSession.sendMessage(m.getUser().getUsername() + ": " + m.getMessage()));
    }

    public void disconnectUser(ServerClientSession userSession) {
        userSet.remove(userSession.getUser());
    }

}
