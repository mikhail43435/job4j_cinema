package ru.job4j.cinema.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.Main;
import ru.job4j.cinema.exceptions.DuplicateTicketFieldsSessionRowSeatException;
import ru.job4j.cinema.model.*;
import ru.job4j.cinema.service.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TicketDBStoreTest {

    private static TicketDBStore store;
    private static BasicDataSource pool;
    private static FilmSession testFilmSession;
    private static Customer testCustomer;
    private static HallService hallService;
    private static SessionService sessionService;
    private static CustomerService customerService;

    @BeforeAll
    static void init() {
        pool = new Main().loadPool();
        try (var connection = pool.getConnection();
             PreparedStatement prepareStatement =
                     connection.prepareStatement("DELETE FROM tickets")) {
            prepareStatement.execute();
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in TicketDBStoreTest.init method", e);
        }

        hallService = new HallService(new HallDBStore(pool));
        Hall testSHall = new Hall(0, "test hall", 10, 20);
        testSHall.setId(hallService.add(testSHall).getId());

        sessionService = new SessionService(
                new SessionDBStore(pool, new HallService(new HallDBStore(pool))));
        testFilmSession = new FilmSession(0, "test session", testSHall);
        testFilmSession.setId(sessionService.add(testFilmSession).getId());

        customerService = new CustomerService(new CustomerDBStore(pool));
        testCustomer = new Customer(0,
                "test name",
                "test email",
                "2314",
                "test password");
        testCustomer.setId(customerService.add(testCustomer).getId());
        //Customer testCustomer = testCustomer;

        store = new TicketDBStore(pool, sessionService, customerService, hallService);
    }

    @AfterAll
    static void finish() throws SQLException {
        pool.close();
    }

    @Test
    void whenAddAndFindById() {
        Ticket itemToAdd = new Ticket(0, testFilmSession, testCustomer, 11, 12);
        itemToAdd.setId(store.add(itemToAdd).getId());
        Optional<Ticket> itemFromDB = store.findById(itemToAdd.getId());
        assertThat(itemFromDB).isPresent();
        assertThat(itemFromDB.get().getId()).isEqualTo(itemToAdd.getId());
        assertThat(itemFromDB.get().getFilmSession()).isEqualTo(itemToAdd.getFilmSession());
        assertThat(itemFromDB.get().getCustomer()).isEqualTo(itemToAdd.getCustomer());
        assertThat(itemFromDB.get().getRowNum()).isEqualTo(itemToAdd.getRowNum());
        assertThat(itemFromDB.get().getSeatNum()).isEqualTo(itemToAdd.getSeatNum());
    }

    @Test
    void whenAddTicketWithNotUniqueSessionAndRowAndSeat() {
        Ticket itemToAdd = new Ticket(0, testFilmSession, testCustomer, 21, 22);
        itemToAdd.setId(store.add(itemToAdd).getId());

        Ticket itemToAddWithDuplicateFields = new Ticket(
                0,
                testFilmSession,
                testCustomer,
                21,
                22);
        assertThrows(DuplicateTicketFieldsSessionRowSeatException.class,
                () -> store.add(itemToAddWithDuplicateFields));
    }

    @Test
    void whenFindAll() {
        List<Ticket> fullList = store.findAll();
        Ticket itemToAdd1 = new Ticket(0, testFilmSession, testCustomer, 31, 34);
        itemToAdd1.setId(store.add(itemToAdd1).getId());
        Ticket itemToAdd2 = new Ticket(0, testFilmSession, testCustomer, 32, 35);
        itemToAdd2.setId(store.add(itemToAdd2).getId());
        Ticket itemToAdd3 = new Ticket(0, testFilmSession, testCustomer, 33, 36);
        itemToAdd3.setId(store.add(itemToAdd3).getId());
        List<Ticket> listOfNewItems = List.of(itemToAdd1, itemToAdd2, itemToAdd3);
        fullList.addAll(listOfNewItems);
        assertThat(store.findAll()).isEqualTo(fullList);
    }

    @Test
    void whenUpdate() {
        Ticket itemToAdd = new Ticket(0, testFilmSession, testCustomer, 41, 42);
        itemToAdd.setId(store.add(itemToAdd).getId());
        Ticket itemToUpdate = new Ticket(itemToAdd.getId(),
                testFilmSession,
                testCustomer,
                43,
                44);
        store.update(itemToUpdate);

        Optional<Ticket> itemFromDBAfterUpdate = store.findById(itemToUpdate.getId());
        assertThat(itemFromDBAfterUpdate).isPresent();
        assertThat(itemFromDBAfterUpdate.get().getId()).isEqualTo(itemToUpdate.getId());
        assertThat(itemFromDBAfterUpdate.get().getFilmSession()).
                isEqualTo(itemToUpdate.getFilmSession());
        assertThat(itemFromDBAfterUpdate.get().getRowNum()).isEqualTo(itemToUpdate.getRowNum());
        assertThat(itemFromDBAfterUpdate.get().getSeatNum()).isEqualTo(itemToUpdate.getSeatNum());
    }

    @Test
    void whenUpdateTicketWithNotUniqueSessionAndRowAndSeat() {
        Ticket itemToAdd = new Ticket(0,
                testFilmSession,
                testCustomer,
                51,
                52);
        itemToAdd.setId(store.add(itemToAdd).getId());

        Ticket itemToAddWithDuplicateFields = new Ticket(0,
                testFilmSession,
                testCustomer,
                51,
                52);
        assertThrows(DuplicateTicketFieldsSessionRowSeatException.class,
                () -> store.add(itemToAddWithDuplicateFields));
    }

    @Test
    void whenFindBySession() {
        Ticket itemToAdd1 = new Ticket(0, testFilmSession, testCustomer, 51, 511);
        itemToAdd1.setId(store.add(itemToAdd1).getId());

        Ticket itemToAdd2 = new Ticket(0, testFilmSession, testCustomer, 52, 522);
        itemToAdd2.setId(store.add(itemToAdd2).getId());

        List<Ticket> expected = List.of(itemToAdd1, itemToAdd2);
        List<Ticket> result = store.findBySession(testFilmSession);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void whenGetAvailableSeats() {
        Hall hall = new Hall(0, "whenGetAvailableSeats hall", 2, 2);
        hall.setId(hallService.add(hall).getId());
        FilmSession session = new FilmSession(
                0,
                "whenGetAvailableSeats session",
                hall);
        session.setId(sessionService.add(session).getId());

        store.add(new Ticket(0, session, testCustomer, 1, 1));
        store.add(new Ticket(0, session, testCustomer, 2, 2));

        List<Seat> expected = List.of(
                new Seat(1, 2),
                new Seat(2, 1));
        assertThat(store.getAvailableSeatsForSession(session)).isEqualTo(expected);
    }

    @Test
    void whenFindByCustomerId() {
        Customer testCustomerForThisTest = new Customer(0,
                "test name whenFindByCustomerId",
                "test email whenFindByCustomerId",
                "78678",
                "test password whenFindByCustomerId");
        testCustomerForThisTest.setId(customerService.add(testCustomerForThisTest).getId());
        Ticket ticket1 = new Ticket(0, testFilmSession, testCustomerForThisTest, 61, 62);
        ticket1.setId(store.add(ticket1).getId());
        Ticket ticket2 = new Ticket(0, testFilmSession, testCustomerForThisTest, 64, 65);
        ticket2.setId(store.add(ticket2).getId());
        List<Ticket> expected = List.of(ticket1, ticket2);
        List<Ticket> result = store.findByCustomerId(testCustomerForThisTest.getId());
        assertThat(result).isEqualTo(expected);
    }
}