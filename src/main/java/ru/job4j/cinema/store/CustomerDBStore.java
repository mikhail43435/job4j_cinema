package ru.job4j.cinema.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.exceptions.DuplicateCustomerEmailException;
import ru.job4j.cinema.exceptions.DuplicateCustomerPhoneException;
import ru.job4j.cinema.model.Customer;
import ru.job4j.cinema.service.LoggerService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CustomerDBStore implements CustomerStore {

    private final BasicDataSource pool;

    public CustomerDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    @Override
    public Customer add(Customer customer) {
        //Optional<Customer> result = Optional.empty();
        String param = "INSERT INTO customers(username, email, phone, password) VALUES (?,?,?,?)";
        try (var connection = pool.getConnection();
             PreparedStatement prepareStatement =
                     connection.prepareStatement(param, PreparedStatement.RETURN_GENERATED_KEYS)) {
            prepareStatement.setString(1, customer.getName());
            prepareStatement.setString(2, customer.getEmail());
            prepareStatement.setString(3, customer.getPhone());
            prepareStatement.setString(4, customer.getPassword());
            prepareStatement.execute();
            try (ResultSet resultSet = prepareStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    customer.setId((resultSet.getInt(1)));
                }
            }
        } catch (Exception e) {
            if (e.getMessage().contains("PUBLIC.CONSTRAINT_INDEX_6 ON PUBLIC.CUSTOMERS(EMAIL)")) {
                LoggerService.LOGGER.error("Add new customer exception "
                        + "in CustomerDBStore.add method | duplicate email <"
                        + customer.getEmail() + ">", e);
                throw new DuplicateCustomerEmailException();
            }

            if (e.getMessage().contains("PUBLIC.CONSTRAINT_INDEX_62 ON PUBLIC.CUSTOMERS(PHONE)")) {
                LoggerService.LOGGER.error("Add new customer exception "
                        + "in CustomerDBStore.add method | duplicate phone <"
                        + customer.getEmail() + ">", e);
                throw new DuplicateCustomerPhoneException();
            }
            LoggerService.LOGGER.error("Exception in CustomerDBStore.add method", e);
        }
        return customer;
    }

    @Override
    public void update(Customer customer) {
        try (var connection = pool.getConnection();
             var prepareStatement = connection.prepareStatement("UPDATE customers SET"
                     + " username = ?,"
                     + " email = ?,"
                     + " phone = ?,"
                     + " password = ?"
                     + "WHERE id = ?")) {
            prepareStatement.setString(1, customer.getName());
            prepareStatement.setString(2, customer.getEmail());
            prepareStatement.setString(3, customer.getPhone());
            prepareStatement.setString(4, customer.getPassword());
            prepareStatement.setInt(5, customer.getId());
            prepareStatement.execute();
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in CustomerDBStore.update method", e);
        }
    }

    @Override
    public Optional<Customer> findById(int id) {
        Optional<Customer> result = Optional.empty();
        try (var connection = pool.getConnection();
             var prepareStatement =
                     connection.prepareStatement("SELECT * FROM customers WHERE id = ?")
        ) {
            prepareStatement.setInt(1, id);
            try (var resultSet = prepareStatement.executeQuery()) {
                if (resultSet.next()) {
                    result = Optional.of(
                            new Customer(resultSet.getInt("id"),
                                    resultSet.getString("username"),
                                    resultSet.getString("email"),
                                    resultSet.getString("phone"),
                                    resultSet.getString("password")
                            ));
                }
            }
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in CustomerDBStore.findById method", e);
        }
        return result;
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        Optional<Customer> result = Optional.empty();
        try (var connection = pool.getConnection();
             var prepareStatement =
                     connection.prepareStatement("SELECT * FROM customers WHERE email = ?")
        ) {
            prepareStatement.setString(1, email);
            try (var resultSet = prepareStatement.executeQuery()) {
                if (resultSet.next()) {
                    result = Optional.of(
                            new Customer(resultSet.getInt("id"),
                                    resultSet.getString("username"),
                                    resultSet.getString("email"),
                                    resultSet.getString("phone"),
                                    resultSet.getString("password")
                            ));
                }
            }
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in CustomerDBStore.findById method", e);
        }
        return result;
    }

    @Override
    public List<Customer> findAll() {
        List<Customer> result = new ArrayList<>();
        try (var connection = pool.getConnection();
             var prepareStatement =
                     connection.prepareStatement("SELECT * FROM customers ORDER BY id")
        ) {
            try (var resultSet = prepareStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(new Customer(resultSet.getInt("id"),
                            resultSet.getString("username"),
                            resultSet.getString("email"),
                            resultSet.getString("phone"),
                            resultSet.getString("password")
                    ));
                }
            }
        } catch (Exception e) {
            LoggerService.LOGGER.error("Exception in CustomerDBStore.findAll method", e);
        }
        return result;
    }
}