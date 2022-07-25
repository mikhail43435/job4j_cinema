package ru.job4j.cinema.model;

import java.io.Serializable;
import java.util.Objects;

public class Hall implements Serializable {

    private int id;
    private String name;
    private int numOfRows;
    private int numOfSeats;

    public Hall() {
    }

    public Hall(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Hall(int id, String name, int numOfRows, int numOfSeats) {
        this.id = id;
        this.name = name;
        this.numOfRows = numOfRows;
        this.numOfSeats = numOfSeats;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumOfRows() {
        return numOfRows;
    }

    public void setNumOfRows(int numOfRows) {
        this.numOfRows = numOfRows;
    }

    public int getNumOfSeats() {
        return numOfSeats;
    }

    public void setNumOfSeats(int numOfSeats) {
        this.numOfSeats = numOfSeats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Hall hall = (Hall) o;
        return id == hall.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}