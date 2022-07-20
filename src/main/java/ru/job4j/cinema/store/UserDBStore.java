package ru.job4j.cinema.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.exceptions.DuplicateUserEmailException;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.LoggerService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDBStore implements UserStore {

    private final BasicDataSource pool;

    public UserDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    @Override
    public User add(User user) {
        String param = "INSERT INTO users(email, password) VALUES (?,?)";
        try (var connection = pool.getConnection();
             PreparedStatement prepareStatement =
                     connection.prepareStatement(param, PreparedStatement.RETURN_GENERATED_KEYS)) {
            prepareStatement.setString(1, user.getEmail());
            prepareStatement.setString(2, user.getPassword());
            prepareStatement.execute();
            try (ResultSet resultSet = prepareStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    user.setId((resultSet.getInt(1)));
                }
            }
        } catch (Exception e) {
            if (e.getMessage().contains("PUBLIC.CONSTRAINT_INDEX_4 ON PUBLIC.USERS(EMAIL)")
                    || e.getMessage().contains("users_email_key")) {
                LoggerService.LOGGER.error(
                        String.format("Attempt to add new user with existing email <%s> "
                                        + "in UserDBStore.add method",
                                user.getEmail()));
                throw new DuplicateUserEmailException();
            }
            LoggerService.LOGGER.error("Exception in UserDBStore.add method", e);
        }
        return user;
    }

    @Override
    public void update(User user) {
        try (var connection = pool.getConnection();
             var prepareStatement = connection.prepareStatement("UPDATE users SET"
                     + " email = ?,"
                     + " password = ?"
                     + "WHERE id = ?")) {
            prepareStatement.setString(1, user.getEmail());
            prepareStatement.setString(2, user.getPassword());
            prepareStatement.setInt(3, user.getId());
            prepareStatement.execute();
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in UserDBStore.update method", e);
        }
    }

    @Override
    public Optional<User> findById(int id) {
        Optional<User> result = Optional.empty();
        try (var connection = pool.getConnection();
             var prepareStatement =
                     connection.prepareStatement("SELECT * FROM users WHERE id = ?")
        ) {
            prepareStatement.setInt(1, id);
            try (var resultSet = prepareStatement.executeQuery()) {
                if (resultSet.next()) {
                    result = Optional.of(
                            new User(resultSet.getInt("id"),
                                    resultSet.getString("email"),
                                    resultSet.getString("password")
                            ));
                }
            }
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in UserDBStore.findById method", e);
        }
        return result;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Optional<User> result = Optional.empty();
        try (var connection = pool.getConnection();
             var prepareStatement =
                     connection.prepareStatement("SELECT * FROM users WHERE email = ?")
        ) {
            prepareStatement.setString(1, email);
            try (var resultSet = prepareStatement.executeQuery()) {
                if (resultSet.next()) {
                    result = Optional.of(
                            new User(resultSet.getInt("id"),
                                    resultSet.getString("email"),
                                    resultSet.getString("password")
                            ));
                }
            }
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in UserDBStore.findById method", e);
        }
        return result;
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        Optional<User> result = Optional.empty();
        try (var connection = pool.getConnection();
             var prepareStatement =
                     connection.prepareStatement(
                             "SELECT * FROM users WHERE email = ? AND password = ?")
        ) {
            prepareStatement.setString(1, email);
            prepareStatement.setString(2, password);
            try (var resultSet = prepareStatement.executeQuery()) {
                if (resultSet.next()) {
                    result = Optional.of(
                            new User(resultSet.getInt("id"),
                                    resultSet.getString("email"),
                                    resultSet.getString("password")
                            ));
                }
            }
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in UserDBStore.findByEmailAndPassword method", e);
        }
        return result;
    }

    @Override
    public List<User> findAll() {
        List<User> result = new ArrayList<>();
        try (var connection = pool.getConnection();
             var prepareStatement =
                     connection.prepareStatement("SELECT * FROM users ORDER BY id")
        ) {
            try (var resultSet = prepareStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(new User(resultSet.getInt("id"),
                            resultSet.getString("email"),
                            resultSet.getString("password")
                    ));
                }
            }
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in UserDBStore.findAll method", e);
        }
        return result;
    }
}