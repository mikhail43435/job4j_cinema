package ru.job4j.cinema.util;

import ru.job4j.cinema.model.User;

import javax.servlet.http.HttpSession;

public class UserHandler {

    private UserHandler() {
    }

    public static User getUserOfCurrentSession(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User(0, "Guest", "");
        }
        return user;
    }
}