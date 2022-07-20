package ru.job4j.cinema.store;

import ru.job4j.cinema.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStore {

    User add(User user);

    void update(User user);

    Optional<User> findById(int id);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndPassword(String email, String password);

    List<User> findAll();
}