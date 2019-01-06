package com.shumakov.chat_server.entities;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity(name = "user")
public class Entity_User {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;

    private String username;
    private int password;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Entity_Message> sentMessages;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Entity_SessionPeriod> connections;

    public Entity_User() {
        sentMessages = new LinkedList<>();
        connections = new LinkedList<>();
    }

    public Entity_User(String username, int password) {
        this.username = username;
        this.password = password;

        sentMessages = new LinkedList<>();
        connections = new LinkedList<>();
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public List<Entity_Message> getSentMessages() {
        return sentMessages;
    }

    public List<Entity_SessionPeriod> getConnections() {
        return connections;
    }

    public int getPassword() {
        return password;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSentMessages(List<Entity_Message> sentMessages) {
        this.sentMessages = sentMessages;
    }

    public void setConnections(List<Entity_SessionPeriod> connections) {
        this.connections = connections;
    }

    public void setPassword(int password) {
        this.password = password;
    }

    public void addMessage(Entity_Message message) {
        sentMessages.add(message);
    }

    public void addConnection(Entity_SessionPeriod connection) {
        connections.add(connection);
    }
}
