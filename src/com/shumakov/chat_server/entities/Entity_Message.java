package com.shumakov.chat_server.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;

@Entity(name = "message")
public class Entity_Message implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;

    private String message;

    @ManyToOne(cascade = CascadeType.ALL)
    private Entity_User user;

    private String receiverName;
    private String roomName;

    private Timestamp timestamp;

    public Entity_Message() {
        message = null;
        user = null;

        timestamp = Timestamp.from(Instant.now());
    }

    public Entity_Message(String message, Entity_User user, String receiverName, String roomName) {
        this.message = message;
        this.user = user;
        this.receiverName = receiverName;
        this.roomName = roomName;

        timestamp = Timestamp.from(Instant.now());
    }

    public long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Entity_User getUser() {
        return user;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUser(Entity_User user) {
        this.user = user;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
