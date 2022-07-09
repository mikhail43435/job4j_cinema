package ru.job4j.cinema.store;

import ru.job4j.cinema.model.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketStore {

    Ticket add(Ticket ticket);

    void update(Ticket ticket);

    Optional<Ticket> findById(int id);

    List<Ticket> findAll();
}