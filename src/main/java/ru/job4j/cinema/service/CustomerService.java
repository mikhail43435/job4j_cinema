package ru.job4j.cinema.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.Customer;
import ru.job4j.cinema.store.CustomerStore;

import java.util.List;
import java.util.Optional;

@Service
@ThreadSafe
public class CustomerService {

    private final CustomerStore store;

    public CustomerService(CustomerStore store) {
        this.store = store;
    }

    public Customer add(Customer customer) {
        return store.add(customer);
    }

    public void update(Customer customer) {
        store.update(customer);
    }

    public Optional<Customer> findById(int id) {
        return store.findById(id);
    }

    public Optional<Customer> findByEmail(String email) {
        return store.findByEmail(email);
    }

    public List<Customer> findAll() {
        return store.findAll();
    }
}