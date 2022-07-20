package ru.job4j.cinema.store;

import org.apache.catalina.util.CustomObjectInputStream;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.Main;
import ru.job4j.cinema.exceptions.DuplicateCustomerEmailException;
import ru.job4j.cinema.exceptions.DuplicateCustomerPhoneException;
import ru.job4j.cinema.model.Customer;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.LoggerService;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomerDBStoreTest {

    private static CustomerStore store;
    private static BasicDataSource pool;

    @BeforeAll
    static void init() {
        pool = new Main().loadPool();
        store = new CustomerDBStore(pool);
        try (var connection = pool.getConnection();
             PreparedStatement prepareStatement =
                     connection.prepareStatement("DELETE FROM tickets; DELETE FROM customers")) {
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
    void whenAddCustomerAndFindById() {
        Customer itemToAdd = new Customer(0, "customer 1", "email1", "12", "13");
        itemToAdd.setId(store.add(itemToAdd).getId());
        Optional<Customer> itemFromDB = store.findById(itemToAdd.getId());
        assertThat(itemFromDB).isPresent();
        assertThat(itemFromDB.get().getId()).isEqualTo(itemToAdd.getId());
        assertThat(itemFromDB.get().getEmail()).isEqualTo(itemToAdd.getEmail());
        assertThat(itemFromDB.get().getName()).isEqualTo(itemToAdd.getName());
        assertThat(itemFromDB.get().getPhone()).isEqualTo(itemToAdd.getPhone());
        assertThat(itemFromDB.get().getPassword()).isEqualTo(itemToAdd.getPassword());
    }

    @Test
    void whenFindAll() {
        List<Customer> fullList = store.findAll();
        Customer itemToAdd1 = new Customer(0, "customer 21", "email21", "21", "21");
        itemToAdd1.setId(store.add(itemToAdd1).getId());
        Customer itemToAdd2 = new Customer(0, "customer 22", "email22", "22", "22");
        itemToAdd2.setId(store.add(itemToAdd2).getId());
        Customer itemToAdd3 = new Customer(0, "customer 23", "email23", "23", "23");
        itemToAdd3.setId(store.add(itemToAdd3).getId());
        List<Customer> listOfNewItems = List.of(itemToAdd1, itemToAdd2, itemToAdd3);
        fullList.addAll(listOfNewItems);
        assertThat(store.findAll()).isEqualTo(fullList);
    }

    @Test
    void whenUpdate() {
        Customer itemToAdd = new Customer(0, "customer 31", "email31", "31", "32");
        itemToAdd.setId(store.add(itemToAdd).getId());
        Customer itemToUpdate = new Customer(itemToAdd.getId(),
                "customer 31 updated",
                "email31 updated",
                "31 updated",
                "31 updated");
        store.update(itemToUpdate);

        Optional<Customer> itemFromDBAfterUpdateOptional = store.findById(itemToUpdate.getId());
        assertThat(itemFromDBAfterUpdateOptional).
                isPresent();
        assertThat(itemFromDBAfterUpdateOptional.get().getId()).
                isEqualTo(itemToUpdate.getId());
        assertThat(itemFromDBAfterUpdateOptional.get().getEmail()).
                isEqualTo(itemToUpdate.getEmail());
        assertThat(itemFromDBAfterUpdateOptional.get().getName()).
                isEqualTo(itemToUpdate.getName());
        assertThat(itemFromDBAfterUpdateOptional.get().getPhone()).
                isEqualTo(itemToUpdate.getPhone());
        assertThat(itemFromDBAfterUpdateOptional.get().getPassword()).
                isEqualTo(itemToUpdate.getPassword());
    }

    @Test
    void whenAddCustomerAndFindByEmail() {
        Customer itemToAdd = new Customer(0, "customer 4", "email4", "42", "43");
        itemToAdd.setId(store.add(itemToAdd).getId());
        Optional<Customer> itemFromDB = store.findByEmail(itemToAdd.getEmail());
        assertThat(itemFromDB).isPresent();
        assertThat(itemFromDB.get().getId()).isEqualTo(itemToAdd.getId());
        assertThat(itemFromDB.get().getEmail()).isEqualTo(itemToAdd.getEmail());
        assertThat(itemFromDB.get().getName()).isEqualTo(itemToAdd.getName());
        assertThat(itemFromDB.get().getPhone()).isEqualTo(itemToAdd.getPhone());
        assertThat(itemFromDB.get().getPassword()).isEqualTo(itemToAdd.getPassword());
    }

    @Test
    void whenNotUniqueEmail() {
        Customer itemToAdd = new Customer(0,
                "customer 51",
                "email5",
                "511",
                "512");
        itemToAdd.setId(store.add(itemToAdd).getId());
        Customer itemToAddWithDuplicateField = new Customer(0,
                "customer 52",
                "email5",
                "521",
                "513");
        assertThrows(DuplicateCustomerEmailException.class,
                () -> store.add(itemToAddWithDuplicateField));
    }

    @Test
    void whenNotUniquePhone() {
        Customer itemToAdd = new Customer(0,
                "customer 61",
                "email6",
                "611",
                "612");
        itemToAdd.setId(store.add(itemToAdd).getId());
        Customer itemToAddWithDuplicateField = new Customer(0,
                "customer 62",
                "email61",
                "611",
                "613");
        assertThrows(DuplicateCustomerPhoneException.class,
                () -> store.add(itemToAddWithDuplicateField));
    }

    @Test
    void whenAddCustomerAndFindByEmailAndPassword() {
        Customer itemToAdd = new Customer(0,
                "customer 72",
                "email71",
                "711",
                "713");
        itemToAdd.setId(store.add(itemToAdd).getId());
        Optional<Customer> itemFromDB =
                store.findByEmailAndPassword(itemToAdd.getEmail(), itemToAdd.getPassword());
        assertThat(itemFromDB).isPresent();
        assertThat(itemFromDB.get().getId()).isEqualTo(itemToAdd.getId());
        assertThat(itemFromDB.get().getName()).isEqualTo(itemToAdd.getName());
        assertThat(itemFromDB.get().getEmail()).isEqualTo(itemToAdd.getEmail());
        assertThat(itemFromDB.get().getPhone()).isEqualTo(itemToAdd.getPhone());
        assertThat(itemFromDB.get().getPassword()).isEqualTo(itemToAdd.getPassword());
    }
}