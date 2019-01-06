package com.shumakov.chat_server.entities;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.List;

public class Test {

    public static void main(String[] args) {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("NewPersistenceUnit");
        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();

        Entity_User testUser = new Entity_User("sashodada", "1984".hashCode());
        Entity_User receiver = new Entity_User("parkmann", "1984".hashCode());

        Entity_SessionPeriod sessionPeriod = new Entity_SessionPeriod(testUser);
        Entity_Message message = new Entity_Message("hey", testUser, receiver.getUsername(), null);

        testUser.addMessage(message);

        manager.persist(testUser);

        testUser.addConnection(sessionPeriod);

        sessionPeriod.closeConnection();

        Query query = manager.createQuery("SELECT u FROM user u WHERE u.username = \"gayy\"");

        List<Entity_User> resultList = query.getResultList();
        manager.getTransaction().commit();

        manager.getTransaction().begin();

        Entity_Message entityMessage = new Entity_Message("hey", testUser, receiver.getUsername(), null);
        manager.persist(entityMessage);

        manager.getTransaction().commit();

        manager.close();

    }
}
