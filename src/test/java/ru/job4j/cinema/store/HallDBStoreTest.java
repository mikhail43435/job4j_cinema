package ru.job4j.cinema.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.Main;
import ru.job4j.cinema.service.LoggerService;
import ru.job4j.cinema.service.Seat;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class HallDBStoreTest {

    private static HallDBStore store;
    private static BasicDataSource pool;

    @BeforeAll
    static void init() {
        pool = new Main().loadPool();
        store = new HallDBStore(pool);
        try (var connection = pool.getConnection();
             PreparedStatement prepareStatement =
                     connection.prepareStatement("DELETE FROM customers")) {
            prepareStatement.execute();
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in CustomerDBStoreTest.init method", e);
        }
    }

    @AfterAll
    static void finish() throws SQLException {
        pool.close();
    }

    @Test
    void whenAddAndFindById() {
        Hall itemToAdd = new Hall(0, "hall 1", 10, 20);
        itemToAdd.setId(store.add(itemToAdd).getId());
        Optional<Hall> itemFromDB = store.findById(itemToAdd.getId());
        assertThat(itemFromDB).isPresent();
        assertThat(itemFromDB.get().getId()).isEqualTo(itemToAdd.getId());
        assertThat(itemFromDB.get().getName()).isEqualTo(itemToAdd.getName());
        assertThat(itemFromDB.get().getNumOfRows()).isEqualTo(itemToAdd.getNumOfRows());
        assertThat(itemFromDB.get().getNumOfSeats()).isEqualTo(itemToAdd.getNumOfSeats());
    }

    @Test
    void whenFindAll() {
        List<Hall> list = store.findAll();
        Hall itemToAdd1 = new Hall(0, "hall 1", 10, 20);
        itemToAdd1.setId(store.add(itemToAdd1).getId());
        Hall itemToAdd2 = new Hall(0, "hall 2", 10, 20);
        itemToAdd2.setId(store.add(itemToAdd2).getId());
        Hall itemToAdd3 = new Hall(0, "hall 3", 10, 20);
        itemToAdd3.setId(store.add(itemToAdd3).getId());
        List<Hall> listOfNewItems = List.of(itemToAdd1, itemToAdd2, itemToAdd3);
        list.addAll(listOfNewItems);
        assertThat(store.findAll()).isEqualTo(list);
    }

    @Test
    void whenUpdate() {
        Hall itemToAdd = new Hall(0, "hall 1", 10, 20);
        itemToAdd.setId(store.add(itemToAdd).getId());
        Hall itemToUpdate = new Hall(itemToAdd.getId(), "hall 1 updated", 11, 22);
        store.update(itemToUpdate);
        Hall itemFromDBAfterUpdate = store.findById(itemToUpdate.getId()).get();
        assertThat(itemFromDBAfterUpdate.getId()).isEqualTo(itemToUpdate.getId());
        assertThat(itemFromDBAfterUpdate.getName()).isEqualTo(itemToUpdate.getName());
        assertThat(itemFromDBAfterUpdate.getNumOfRows()).isEqualTo(itemToUpdate.getNumOfRows());
        assertThat(itemFromDBAfterUpdate.getNumOfSeats()).isEqualTo(itemToUpdate.getNumOfSeats());
    }

    @Test
    void whenGetSeats() {
        List<Seat> expected = List.of(
                new Seat(1, 1),
                new Seat(1, 2),
                new Seat(2, 1),
                new Seat(2, 2)
        );
        List<Seat> result = store.getSeats(new Hall(0, "test hall", 2, 2));
        assertThat(result).isEqualTo(expected);
    }
}