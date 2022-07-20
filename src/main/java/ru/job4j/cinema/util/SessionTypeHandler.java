package ru.job4j.cinema.util;

import javax.servlet.http.HttpSession;

import static java.util.Objects.isNull;

public class SessionTypeHandler {

    private SessionTypeHandler() {
    }

    public static String getCurrentSessionType(HttpSession session) {
        String sessionType = (String) session.getAttribute("sessionType");
        if (isNull(sessionType)) {
            sessionType = "guest";
        }
        return sessionType;
    }
}