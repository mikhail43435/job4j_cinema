package ru.job4j.cinema.store;

import ru.job4j.cinema.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerStore {

    Customer add(Customer customer);

    void update(Customer customer);

    Optional<Customer> findById(int id);

    Optional<Customer> findByEmail(String email);

    List<Customer> findAll();
}