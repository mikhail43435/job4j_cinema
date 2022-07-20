package ru.job4j.cinema.util;

import ru.job4j.cinema.model.Customer;

import javax.servlet.http.HttpSession;

public class CustomerHandler {

    private CustomerHandler() {
    }

    public static Customer getCustomerOfCurrentSession(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            customer = new Customer(0, "", "Guest", "", "");
        }
        return customer;
    }
}