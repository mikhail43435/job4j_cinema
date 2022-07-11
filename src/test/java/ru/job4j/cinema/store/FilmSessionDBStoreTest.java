package ru.job4j.cinema.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.Main;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.service.HallService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class FilmSessionDBStoreTest {

    private static HallService hallService;
    private static Hall hall;
    private static BasicDataSource pool;
    private static BasicDataSource basicDataSource;

    @BeforeAll
    public static void init() {
        pool = new Main().loadPool();
        hallService = new HallService(new HallDBStore(pool));
        hall = new Hall(0, "hall 1", 1, 2);
        hall.setId(hallService.add(hall).getId());
        basicDataSource = new Main().loadPool();
    }

    @AfterAll
    static void finish() throws SQLException {
        pool.close();
    }

    @Test
    void whenAddAndFindById() {
        SessionStore store = new SessionDBStore(basicDataSource, hallService);
        FilmSession itemToAdd = new FilmSession(0, "session 1", hall);
        itemToAdd.setId(store.add(itemToAdd).getId());
        Optional<FilmSession> itemFromDB = store.findById(itemToAdd.getId());
        assertThat(itemFromDB).isPresent();
        assertThat(itemFromDB.get().getId()).isEqualTo(itemToAdd.getId());
        assertThat(itemFromDB.get().getName()).isEqualTo(itemToAdd.getName());
        assertThat(itemFromDB.get().getHall()).isEqualTo(itemToAdd.getHall());
    }

    @Test
    void whenFindAll() {
        SessionStore store = new SessionDBStore(basicDataSource, hallService);
        List<FilmSession> list = store.findAll();
        FilmSession itemToAdd1 = new FilmSession(0, "session 1", hall);
        itemToAdd1.setId(store.add(itemToAdd1).getId());
        FilmSession itemToAdd2 = new FilmSession(0, "session 2", hall);
        itemToAdd2.setId(store.add(itemToAdd2).getId());
        FilmSession itemToAdd3 = new FilmSession(0, "session 3", hall);
        itemToAdd3.setId(store.add(itemToAdd3).getId());
        List<FilmSession> listOfNewItems = List.of(itemToAdd1, itemToAdd2, itemToAdd3);
        list.addAll(listOfNewItems);
        assertThat(store.findAll()).isEqualTo(list);
    }

    @Test
    void whenUpdate() {
        SessionStore store = new SessionDBStore(basicDataSource, hallService);
        FilmSession itemToAdd = new FilmSession(0, "session 1", hall);
        itemToAdd.setId(store.add(itemToAdd).getId());

        Hall hallForUpdate = new Hall(0, "hall 1 for update", 1, 2);
        hallForUpdate.setId(hallService.add(hall).getId());

        FilmSession itemToUpdate = new FilmSession(itemToAdd.getId(),
                "session 1 updated",
                hallForUpdate);
        store.update(itemToUpdate);

        FilmSession itemFromDBAfterUpdate = store.findById(itemToUpdate.getId()).get();
        assertThat(itemFromDBAfterUpdate.getId()).isEqualTo(itemToUpdate.getId());
        assertThat(itemFromDBAfterUpdate.getName()).isEqualTo(itemToUpdate.getName());
        assertThat(itemFromDBAfterUpdate.getHall()).isEqualTo(itemToUpdate.getHall());
    }
}