package ru.job4j.cinema.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.service.HallService;
import ru.job4j.cinema.service.LoggerService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class SessionDBStore implements SessionStore {

    private final BasicDataSource pool;
    private final HallService hallService;

    public SessionDBStore(BasicDataSource pool, HallService hallService) {
        this.pool = pool;
        this.hallService = hallService;
    }

    @Override
    public FilmSession add(FilmSession filmSession) {
        String param = "INSERT INTO sessions(name, hall_id) VALUES (?,?)";
        try (var connection = pool.getConnection();
             PreparedStatement prepareStatement =
                     connection.prepareStatement(param, PreparedStatement.RETURN_GENERATED_KEYS)) {
            prepareStatement.setString(1, filmSession.getName());
            prepareStatement.setInt(2, filmSession.getHall().getId());
            prepareStatement.execute();
            try (ResultSet resultSet = prepareStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    filmSession.setId((resultSet.getInt(1)));
                }
            }
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in SessionDBStore.add method", e);
        }
        return filmSession;
    }

    @Override
    public void update(FilmSession filmSession) {
        try (var connection = pool.getConnection();
             var prepareStatement = connection.prepareStatement("UPDATE sessions SET"
                     + " name = ?,"
                     + " hall_id = ?"
                     + "WHERE id = ?")) {
            prepareStatement.setString(1, filmSession.getName());
            prepareStatement.setInt(2, filmSession.getHall().getId());
            prepareStatement.setInt(3, filmSession.getId());
            prepareStatement.execute();
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in SessionDBStore.update method", e);
        }
    }

    @Override
    public Optional<FilmSession> findById(int id) {
        Optional<FilmSession> result = Optional.empty();
        try (var connection = pool.getConnection();
             var prepareStatement =
                     connection.prepareStatement("SELECT * FROM sessions WHERE id = ?")
        ) {
            prepareStatement.setInt(1, id);
            try (var resultSet = prepareStatement.executeQuery()) {
                if (resultSet.next()) {
                    result = Optional.of(
                            new FilmSession(resultSet.getInt("id"),
                                    resultSet.getString("name"),
                                    hallService.findById(resultSet.getInt(("hall_id"))).get()
                            ));
                }
            }
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in SessionDBStore.findById method", e);
        }
        return result;
    }

    @Override
    public List<FilmSession> findAll() {
        List<FilmSession> result = new ArrayList<>();
        try (var connection = pool.getConnection();
             var prepareStatement =
                     connection.prepareStatement("SELECT * FROM sessions ORDER BY id")
        ) {
            try (var resultSet = prepareStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(new FilmSession(resultSet.getInt("id"),
                            resultSet.getString("name"),
                            hallService.findById(resultSet.getInt(("hall_id"))).get()));
                }
            }
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in SessionDBStore.findAll method", e);
        }
        return result;
    }

    public List<FilmSession> findAllSessionsWithAvailableSeats() {
        String sqlQuery = "SELECT id, name, hall_id, seats_left FROM (SELECT sessions.id "
                + "AS id, sessions.name, "
                + "hall_seats.id AS hall_id, (hall_seat_amount - COALESCE(tickets_num,0)) "
                + "AS seats_left FROM sessions\n"
                + "LEFT JOIN (SELECT id, name, (num_of_rows * num_of_seats) "
                + "AS hall_seat_amount FROM halls) "
                + "AS hall_seats ON sessions.hall_id = hall_seats.id\n"
                + "LEFT JOIN (SELECT session_id, COUNT(*) AS tickets_num FROM "
                + "tickets GROUP BY session_id) "
                + "AS tickets_count ON sessions.id = tickets_count.session_id) AS seats\n"
                + "WHERE seats_left > 0;";
        List<FilmSession> result = new ArrayList<>();
        try (var connection = pool.getConnection();
             var prepareStatement =
                     connection.prepareStatement(sqlQuery)
        ) {
            try (var resultSet = prepareStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(new FilmSession(resultSet.getInt("id"),
                            resultSet.getString("name"),
                            hallService.findById(resultSet.getInt(("hall_id"))).get()
                    ));
                }
            }
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in TicketDBStore.findAll method", e);
        }
        return result;
    }
}