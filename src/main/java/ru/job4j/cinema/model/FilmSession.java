package ru.job4j.cinema.model;

import java.io.Serializable;
import java.util.Objects;

public class FilmSession implements Serializable {

    private int id;
    private String name;
    private Hall hall;

    public FilmSession() {
    }

    public FilmSession(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public FilmSession(int id, String name, Hall hall) {
        this.id = id;
        this.name = name;
        this.hall = hall;
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

    public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) {
        this.hall = hall;
    }

    @Override
    public String toString() {
        return "Session{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", hall=" + hall
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FilmSession filmSession = (FilmSession) o;
        return id == filmSession.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}