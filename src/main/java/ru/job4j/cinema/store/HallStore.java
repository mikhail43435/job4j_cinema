package ru.job4j.cinema.store;

import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.model.Seat;

import java.util.List;
import java.util.Optional;

public interface HallStore {

    Hall add(Hall hall);

    void update(Hall hall);

    Optional<Hall> findById(int id);

    List<Hall> findAll();

    List<Seat> getSeats(Hall hall);
}