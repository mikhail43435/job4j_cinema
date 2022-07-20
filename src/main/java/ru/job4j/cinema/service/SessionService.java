package ru.job4j.cinema.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.FilmSession;
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

    public FilmSession add(FilmSession filmSession) {
        return store.add(filmSession);
    }

    public void update(FilmSession filmSession) {
        store.update(filmSession);
    }

    public Optional<FilmSession> findById(int id) {
        return store.findById(id);
    }

    public List<FilmSession> findAll() {
        return store.findAll();
    }

    public List<FilmSession> findAllSessionsWithAvailableSeats() {
        return store.findAllSessionsWithAvailableSeats();
    }
}