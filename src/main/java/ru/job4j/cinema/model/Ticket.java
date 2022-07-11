package ru.job4j.cinema.model;

import java.util.Objects;

public class Ticket {

    private int id;
    private FilmSession filmSession;
    private Customer customer;
    private int rowNum;
    private int seatNum;

    public Ticket(
            int id,
            FilmSession filmSession,
            Customer customer,
            int rowNum,
            int seatNum) {
        this.id = id;
        this.filmSession = filmSession;
        this.customer = customer;
        this.rowNum = rowNum;
        this.seatNum = seatNum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FilmSession getSession() {
        return filmSession;
    }

    public void setSession(FilmSession filmSession) {
        this.filmSession = filmSession;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getRowNum() {
        return rowNum;
    }

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    public int getSeatNum() {
        return seatNum;
    }

    public void setSeatNum(int seatNum) {
        this.seatNum = seatNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Ticket ticket = (Ticket) o;
        return id == ticket.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
