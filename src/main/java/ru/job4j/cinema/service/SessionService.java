package ru.job4j.cinema.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.Session;
import ru.job4j.cinema.store.SessionStore;

import java.util.List;
import java.util.Optional;

@Service
@ThreadSafe
public class SessionService {

    private final SessionStore store;

    public SessionService(SessionStore store) {
        this.store = store;
    }

    public Session add(Session session) {
        return store.add(session);
    }

    public void update(Session session) {
        store.update(session);
    }

    public Optional<Session> findById(int id) {
        return store.findById(id);
    }

    public List<Session> findAll() {
        return store.findAll();
    }
}