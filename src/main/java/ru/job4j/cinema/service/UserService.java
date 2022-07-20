package ru.job4j.cinema.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.store.UserStore;

import java.util.List;
import java.util.Optional;

@Service
@ThreadSafe
public class UserService {

    private final UserStore store;

    public UserService(UserStore store) {
        this.store = store;
    }

    public User add(User user) {
        return store.add(user);
    }

    public void update(User user) {
        store.update(user);
    }

    public Optional<User> findById(int id) {
        return store.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return store.findByEmail(email);
    }

    public Optional<User> findByEmailAndPassword(String email, String password) {
        return store.findByEmailAndPassword(email, password);
    }

    public List<User> findAll() {
        return store.findAll();
    }
}