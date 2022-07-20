package ru.job4j.cinema.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.model.Seat;
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
        String validateResultString = validateNewHallInfo(hall);
        if (!validateResultString.isEmpty()) {
            throw new IllegalArgumentException(validateResultString);
        }
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

    private String validateNewHallInfo(Hall hall) {
        String result = "";
        if (hall.getName().isEmpty() || hall.getName().isBlank()) {
            return "Invalid hall name. Blank or empty name is not allowed.";
        }
        if (hall.getNumOfRows() < 1 || hall.getNumOfRows() > 1000) {
            return String.format("Invalid number of rows in the hall (%d). "
                    + "Num must in the 1-1000 range", hall.getNumOfRows());
        }
        if (hall.getNumOfSeats() < 1 || hall.getNumOfSeats() > 1000) {
            return String.format("Invalid number of sets in the row (%d). "
                    + "Num must in the 1-1000 range", hall.getNumOfSeats());
        }
        return result;
    }
}