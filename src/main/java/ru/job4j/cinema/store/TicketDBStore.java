package ru.job4j.cinema.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.service.CustomerService;
import ru.job4j.cinema.service.SessionService;
import ru.job4j.cinema.service.LoggerService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TicketDBStore implements TicketStore {

    private final BasicDataSource pool;
    private final SessionService sessionService;
    private final CustomerService customerService;

    public TicketDBStore(BasicDataSource pool,
                         SessionService sessionService,
                         CustomerService customerService) {
        this.pool = pool;
        this.sessionService = sessionService;
        this.customerService = customerService;
    }

    @Override
    public Ticket add(Ticket ticket) {
        String param = "INSERT INTO tickets(session_id, row_num, seat_num, customer_id) "
                + "VALUES (?,?,?,?)";
        try (var connection = pool.getConnection();
             PreparedStatement prepareStatement =
                     connection.prepareStatement(param, PreparedStatement.RETURN_GENERATED_KEYS)) {
            prepareStatement.setInt(1, ticket.getSession().getId());
            prepareStatement.setInt(2, ticket.getRowNum());
            prepareStatement.setInt(3, ticket.getSeatNum());
            prepareStatement.setInt(4, ticket.getCustomer().getId());
            prepareStatement.execute();
            try (ResultSet resultSet = prepareStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    ticket.setId((resultSet.getInt(1)));
                }
            }
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in TicketDBStore.add method", e);
        }
        return ticket;
    }

    @Override
    public void update(Ticket ticket) {
        try (var connection = pool.getConnection();
             var prepareStatement = connection.prepareStatement("UPDATE tickets SET"
                     + " session_id = ?,"
                     + " row_num = ?,"
                     + " seat_num = ?,"
                     + " customer_id = ?"
                     + "WHERE id = ?")) {
            prepareStatement.setInt(1, ticket.getSession().getId());
            prepareStatement.setInt(2, ticket.getRowNum());
            prepareStatement.setInt(3, ticket.getSeatNum());
            prepareStatement.setInt(4, ticket.getCustomer().getId());
            prepareStatement.setInt(4, ticket.getId());
            prepareStatement.execute();
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in TicketDBStore.update method", e);
        }
    }

    @Override
    public Optional<Ticket> findById(int id) {
        Optional<Ticket> result = Optional.empty();
        try (var connection = pool.getConnection();
             var prepareStatement =
                     connection.prepareStatement("SELECT * FROM tickets WHERE id = ?")
        ) {
            prepareStatement.setInt(1, id);
            try (var resultSet = prepareStatement.executeQuery()) {
                if (resultSet.next()) {
                    result = Optional.of(
                            new Ticket(
                                    resultSet.getInt("id"),
                                    sessionService.findById(resultSet.getInt("session_id")).get(),
                                    customerService.findById(resultSet.getInt("customer_id")).get(),
                                    resultSet.getInt("row_num"),
                                    resultSet.getInt("seat_num")
                            ));
                }
            }
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in TicketDBStore.findById method", e);
        }
        return result;
    }

    @Override
    public List<Ticket> findAll() {
        List<Ticket> result = new ArrayList<>();
        try (var connection = pool.getConnection();
             var prepareStatement =
                     connection.prepareStatement("SELECT * FROM tickets ORDER BY id")
        ) {
            try (var resultSet = prepareStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(new Ticket(
                            resultSet.getInt("id"),
                            sessionService.findById(resultSet.getInt("session_id")).get(),
                            customerService.findById(resultSet.getInt("customer_id")).get(),
                            resultSet.getInt("row_num"),
                            resultSet.getInt("seat_num")
                    ));
                }
            }
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in TicketDBStore.findAll method", e);
        }
        return result;
    }
}