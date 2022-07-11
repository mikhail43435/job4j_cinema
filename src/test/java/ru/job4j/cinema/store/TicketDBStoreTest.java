package ru.job4j.cinema.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.Main;
import ru.job4j.cinema.exceptions.DuplicateTicketFieldsSessionRowSeatException;
import ru.job4j.cinema.model.Customer;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.service.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
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

        CustomerService customerService = new CustomerService(new CustomerDBStore(pool));
        testCustomer = new Customer(0,
                "test name",
                "test email",
                "test phone",
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
        assertThat(itemFromDB.get().getSession()).isEqualTo(itemToAdd.getSession());
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
        assertThat(itemFromDBAfterUpdate.get().getSession()).isEqualTo(itemToUpdate.getSession());
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
        assertThat(store.getAvailableSeats(session)).isEqualTo(expected);
    }

//
//    @Test
//    void whenGenerateNewTickets() {
//        FilmSession filmSession = new FilmSession(0, "123",
//                hallService.add(new Hall(
//                        0,
//                        "whenGenerateNewTickets test hall",
//                        2,
//                        2)));
//        Customer emptyCustomer = Optional.empty();
//
//        List<Ticket> expected = new ArrayList<>();
//        expected.add(new Ticket(0, filmSession, emptyCustomer, 1, 1));
//        expected.add(new Ticket(0, filmSession, emptyCustomer, 1, 2));
//        expected.add(new Ticket(0, filmSession, emptyCustomer, 2, 1));
//        expected.add(new Ticket(0, filmSession, emptyCustomer, 2, 2));
//
//        List<Ticket> result = store.generateNewTickets(filmSession);
//
//        assertThat(result).isEqualTo(expected);
//    }

}