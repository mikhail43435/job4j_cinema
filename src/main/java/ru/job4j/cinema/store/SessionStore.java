package ru.job4j.cinema.store;

import ru.job4j.cinema.model.Session;

import java.util.List;
import java.util.Optional;

public interface SessionStore {

    Session add(Session session);

    void update(Session session);

    Optional<Session> findById(int id);

    List<Session> findAll();
}