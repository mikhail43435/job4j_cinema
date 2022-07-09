package ru.job4j.cinema.store;

import org.junit.jupiter.api.Test;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.Main;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class HallDBStoreTest {

    @Test
    void whenAddHall() {
        HallStore store = new HallDBStore(new Main().loadPool());
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
        HallStore store = new HallDBStore(new Main().loadPool());
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
        HallStore store = new HallDBStore(new Main().loadPool());
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
}