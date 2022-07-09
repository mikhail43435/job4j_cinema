package ru.job4j.cinema.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.service.LoggerService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class HallDBStore implements HallStore {

    private final BasicDataSource pool;

    public HallDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    @Override
    public Hall add(Hall hall) {
        String param = "INSERT INTO halls(name, num_of_rows, num_of_seats) VALUES (?,?,?)";
        try (var connection = pool.getConnection();
             PreparedStatement prepareStatement =
                     connection.prepareStatement(param, PreparedStatement.RETURN_GENERATED_KEYS)) {
            prepareStatement.setString(1, hall.getName());
            prepareStatement.setInt(2, hall.getNumOfRows());
            prepareStatement.setInt(3, hall.getNumOfSeats());
            prepareStatement.execute();
            try (ResultSet resultSet = prepareStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    hall.setId((resultSet.getInt(1)));
                }
            }
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in HallDBStore.add method", e);
        }
        return hall;
    }

    @Override
    public void update(Hall hall) {
        try (var connection = pool.getConnection();
             var prepareStatement = connection.prepareStatement("UPDATE halls SET"
                     + " name = ?,"
                     + " num_of_rows = ?,"
                     + " num_of_seats = ? "
                     + "WHERE id = ?")) {
            prepareStatement.setString(1, hall.getName());
            prepareStatement.setInt(2, hall.getNumOfRows());
            prepareStatement.setInt(3, hall.getNumOfSeats());
            prepareStatement.setInt(4, hall.getId());
            prepareStatement.execute();
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in HallDBStore.update method", e);
        }
    }

    @Override
    public Optional<Hall> findById(int id) {
        Optional<Hall> result = Optional.empty();
        try (var connection = pool.getConnection();
             var prepareStatement = connection.prepareStatement("SELECT * FROM halls WHERE id = ?")
        ) {
            prepareStatement.setInt(1, id);
            try (var resultSet = prepareStatement.executeQuery()) {
                if (resultSet.next()) {
                    result = Optional.of(
                            new Hall(resultSet.getInt("id"),
                                    resultSet.getString("name"),
                                    resultSet.getInt(("num_of_rows")),
                                    resultSet.getInt(("num_of_seats"))
                            ));
                }
            }
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in HallDBStore.findById method", e);
        }
        return result;
    }

    @Override
    public List<Hall> findAll() {
        List<Hall> result = new ArrayList<>();
        try (var connection = pool.getConnection();
             var prepareStatement = connection.prepareStatement("SELECT * FROM halls ORDER BY id")
        ) {
            try (var resultSet = prepareStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(new Hall(resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getInt(("num_of_rows")),
                            resultSet.getInt(("num_of_seats"))
                    ));
                }
            }
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in HallDBStore.findAll method", e);
        }
        return result;
    }
}