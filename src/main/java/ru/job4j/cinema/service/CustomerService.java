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
        String validateResultString = validateCustomerRegInfo(customer);
        if (!validateResultString.isEmpty()) {
            throw new IllegalArgumentException(validateResultString);
        }
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

    public Optional<Customer> findByEmailAndPassword(String email, String password) {
        return store.findByEmailAndPassword(email, password);
    }

    public List<Customer> findAll() {
        return store.findAll();
    }

    private String validateCustomerRegInfo(Customer customer) {
        String result = "";
        if (customer.getName().isEmpty() || customer.getName().isBlank()) {
            return "Invalid customer name. Blank or empty name is not allowed.";
        }
        if (customer.getEmail().isEmpty() || customer.getEmail().isBlank()) {
            return "Invalid customer e-mail address. Blank or empty e-mail address is not allowed.";
        }
        if (customer.getPhone().isEmpty() || customer.getPhone().isBlank()) {
            return "Invalid customer phone number. Blank or empty password is not allowed.";
        }
        if (!customer.getPhone().chars().allMatch(Character::isDigit)) {
            return "Invalid customer phone number. Only digits (0-9) is allowed.";
        }
        if (customer.getPassword().isEmpty() || customer.getPassword().isBlank()) {
            return "Invalid customer password. Blank or empty password is not allowed.";
        }
        return result;
    }
}