package ru.job4j.cinema.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.store.TicketStore;

import java.util.List;
import java.util.Optional;

@Service
@ThreadSafe
public class TicketService {

    private final TicketStore store;

    public TicketService(TicketStore store) {
        this.store = store;
    }

    public Ticket add(Ticket ticket) {
        return store.add(ticket);
    }

    public void update(Ticket ticket) {
        store.update(ticket);
    }

    public Optional<Ticket> findById(int id) {
        return store.findById(id);
    }

    public List<Ticket> findAll() {
        return store.findAll();
    }

    public List<Ticket> findBySession(FilmSession session) {
        return store.findBySession(session);
    }

    public List<Seat> getAvailableSeats(FilmSession session) {
        return store.getAvailableSeats(session);
    }
}