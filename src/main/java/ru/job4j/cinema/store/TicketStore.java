package ru.job4j.cinema.store;

import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.Seat;

import java.util.List;
import java.util.Optional;

public interface TicketStore {

    Ticket add(Ticket ticket);

    void update(Ticket ticket);

    Optional<Ticket> findById(int id);

    List<Ticket> findAll();

    List<Ticket> findBySession(FilmSession session);

    List<Ticket> findByCustomerId(int id);

    List<Seat> getAvailableSeatsForSession(FilmSession session);
}