package com.shumakov.chat_server.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity(name = "session_period")
public class Entity_SessionPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;

    @ManyToOne(cascade = CascadeType.ALL)
    private Entity_User user;

    private Timestamp fromPoint;
    private Timestamp toPoint;

    public Entity_SessionPeriod() {
        user = null;

        fromPoint = Timestamp.from(Instant.now());
        toPoint = null;
    }

    public Entity_SessionPeriod(Entity_User user) {
        this.user = user;

        fromPoint = Timestamp.from(Instant.now());
        toPoint = null;
    }

    public long getId() {
        return id;
    }

    public Entity_User getUser() {
        return user;
    }

    public Timestamp getFromPoint() {
        return fromPoint;
    }

    public Timestamp getToPoint() {
        return toPoint;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUser(Entity_User user) {
        this.user = user;
    }

    public void setFromPoint(Timestamp from) {
        this.fromPoint = from;
    }

    public void setToPoint(Timestamp to) {
        this.toPoint = to;
    }

    public void closeConnection() {
        this.toPoint = Timestamp.from(Instant.now());
    }
}
