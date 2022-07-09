package ru.job4j.cinema.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.model.Session;
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
    public Session add(Session session) {
        String param = "INSERT INTO sessions(name, hall_id) VALUES (?,?)";
        try (var connection = pool.getConnection();
             PreparedStatement prepareStatement =
                     connection.prepareStatement(param, PreparedStatement.RETURN_GENERATED_KEYS)) {
            prepareStatement.setString(1, session.getName());
            prepareStatement.setInt(2, session.getHall().getId());
            prepareStatement.execute();
            try (ResultSet resultSet = prepareStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    session.setId((resultSet.getInt(1)));
                }
            }
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in SessionDBStore.add method", e);
        }
        return session;
    }

    @Override
    public void update(Session session) {
        try (var connection = pool.getConnection();
             var prepareStatement = connection.prepareStatement("UPDATE sessions SET"
                     + " name = ?,"
                     + " hall_id = ?"
                     + "WHERE id = ?")) {
            prepareStatement.setString(1, session.getName());
            prepareStatement.setInt(2, session.getHall().getId());
            prepareStatement.setInt(3, session.getId());
            prepareStatement.execute();
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in SessionDBStore.update method", e);
        }
    }

    @Override
    public Optional<Session> findById(int id) {
        Optional<Session> result = Optional.empty();
        try (var connection = pool.getConnection();
             var prepareStatement =
                     connection.prepareStatement("SELECT * FROM sessions WHERE id = ?")
        ) {
            prepareStatement.setInt(1, id);
            try (var resultSet = prepareStatement.executeQuery()) {
                if (resultSet.next()) {
                    result = Optional.of(
                            new Session(resultSet.getInt("id"),
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
    public List<Session> findAll() {
        List<Session> result = new ArrayList<>();
        try (var connection = pool.getConnection();
             var prepareStatement =
                     connection.prepareStatement("SELECT * FROM sessions ORDER BY id")
        ) {
            try (var resultSet = prepareStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(new Session(resultSet.getInt("id"),
                            resultSet.getString("name"),
                            hallService.findById(resultSet.getInt(("hall_id"))).get()
                    ));
                }
            }
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in SessionDBStore.findAll method", e);
        }
        return result;
    }
}