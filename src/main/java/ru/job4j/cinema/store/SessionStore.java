package ru.job4j.cinema.store;

import ru.job4j.cinema.model.FilmSession;

import java.util.List;
import java.util.Optional;

public interface SessionStore {

    FilmSession add(FilmSession filmSession);

    void update(FilmSession filmSession);

    Optional<FilmSession> findById(int id);

    List<FilmSession> findAll();

    List<FilmSession> findAllSessionsWithAvailableSeats();
}