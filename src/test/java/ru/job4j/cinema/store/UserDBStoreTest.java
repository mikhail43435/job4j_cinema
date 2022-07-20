package ru.job4j.cinema.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.Main;
import ru.job4j.cinema.exceptions.DuplicateUserEmailException;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.LoggerService;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserDBStoreTest {

    private static UserStore store;
    private static BasicDataSource pool;

    @BeforeAll
    static void init() {
        pool = new Main().loadPool();
        store = new UserDBStore(pool);
        try (var connection = pool.getConnection();
             PreparedStatement prepareStatement =
                     connection.prepareStatement("DELETE FROM users")) {
            prepareStatement.execute();
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in UserDBStoreTest.init method", e);
        }
    }

    @AfterAll
    static void finish() throws SQLException {
        pool.close();
    }

    @Test
    void whenAddUserAndFindById() {
        User itemToAdd = new User(0, "user 1 email", "pass1");
        itemToAdd.setId(store.add(itemToAdd).getId());
        Optional<User> itemFromDB = store.findById(itemToAdd.getId());
        assertThat(itemFromDB).isPresent();
        assertThat(itemFromDB.get().getId()).isEqualTo(itemToAdd.getId());
        assertThat(itemFromDB.get().getEmail()).isEqualTo(itemToAdd.getEmail());
        assertThat(itemFromDB.get().getPassword()).isEqualTo(itemToAdd.getPassword());
    }

    @Test
    void whenFindAll() {
        List<User> fullList = store.findAll();
        User itemToAdd1 = new User(0, "user 21 email", "pass1");
        itemToAdd1.setId(store.add(itemToAdd1).getId());
        User itemToAdd2 = new User(0, "user 22 email", "pass1");
        itemToAdd2.setId(store.add(itemToAdd2).getId());
        User itemToAdd3 = new User(0, "user 23 email", "pass1");
        itemToAdd3.setId(store.add(itemToAdd3).getId());
        List<User> listOfNewItems = List.of(itemToAdd1, itemToAdd2, itemToAdd3);
        fullList.addAll(listOfNewItems);
        assertThat(store.findAll().toString()).isEqualTo(fullList.toString());
    }

    @Test
    void whenUpdate() {
        User itemToAdd = new User(0, "user 31 email", "pass1");
        itemToAdd.setId(store.add(itemToAdd).getId());
        User itemForUpdate = new User(itemToAdd.getId(),
                "user 31 email updated",
                "pass1 updated"
                );
        store.update(itemForUpdate);

        Optional<User> itemFromDBAfterUpdateOptional = store.findById(itemForUpdate.getId());
        assertThat(itemFromDBAfterUpdateOptional).
                isPresent();
        assertThat(itemFromDBAfterUpdateOptional.get().getId()).
                isEqualTo(itemForUpdate.getId());
        assertThat(itemFromDBAfterUpdateOptional.get().getEmail()).
                isEqualTo(itemForUpdate.getEmail());
        assertThat(itemFromDBAfterUpdateOptional.get().getPassword()).
                isEqualTo(itemForUpdate.getPassword());
    }

    @Test
    void whenAddUserAndFindByEmail() {
        User itemToAdd = new User(0, "user 41 email", "pass1");
        itemToAdd.setId(store.add(itemToAdd).getId());
        Optional<User> itemFromDB = store.findByEmail(itemToAdd.getEmail());
        assertThat(itemFromDB).isPresent();
        assertThat(itemFromDB.get().getId()).isEqualTo(itemToAdd.getId());
        assertThat(itemFromDB.get().getEmail()).isEqualTo(itemToAdd.getEmail());
        assertThat(itemFromDB.get().getPassword()).isEqualTo(itemToAdd.getPassword());
    }

    @Test
    void whenNotUniqueEmail() {
        User itemToAdd = new User(0,
                "user 51 email",
                "pass1");
        itemToAdd.setId(store.add(itemToAdd).getId());
        User itemToAddWithDuplicateField = new User(0,
                "user 51 email",
                "pass1");
        assertThrows(DuplicateUserEmailException.class,
                () -> store.add(itemToAddWithDuplicateField));
    }

    @Test
    void whenAddUserAndFindByEmailAndPassword() {
        User itemToAdd = new User(0, "user 61 email", "pass123");
        itemToAdd.setId(store.add(itemToAdd).getId());
        Optional<User> itemFromDB =
                store.findByEmailAndPassword(itemToAdd.getEmail(), itemToAdd.getPassword());
        assertThat(itemFromDB).isPresent();
        assertThat(itemFromDB.get().getId()).isEqualTo(itemToAdd.getId());
        assertThat(itemFromDB.get().getEmail()).isEqualTo(itemToAdd.getEmail());
        assertThat(itemFromDB.get().getPassword()).isEqualTo(itemToAdd.getPassword());
    }
}