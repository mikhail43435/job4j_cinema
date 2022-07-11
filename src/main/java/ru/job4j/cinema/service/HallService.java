package ru.job4j.cinema.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.store.HallStore;

import java.util.List;
import java.util.Optional;

@Service
@ThreadSafe
public class HallService {

    private final HallStore store;

    public HallService(HallStore store) {
        this.store = store;
    }

    public Hall add(Hall hall) {
        return store.add(hall);
    }

    public void update(Hall hall) {
        store.update(hall);
    }

    public Optional<Hall> findById(int id) {
        return store.findById(id);
    }

    public List<Hall> findAll() {
        return store.findAll();
    }

    public List<Seat> getSeats(Hall hall) {
        return store.getSeats(hall);
    }
}