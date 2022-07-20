package ru.job4j.cinema.model;

import java.util.Objects;

public class Seat {

    private int rowNum;
    private int seatNum;

    public Seat(int rowNum, int seatNum) {
        this.rowNum = rowNum;
        this.seatNum = seatNum;
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
    public int hashCode() {
        return Objects.hash(rowNum, seatNum);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Seat seat = (Seat) o;
        return rowNum == seat.rowNum && seatNum == seat.seatNum;
    }

    @Override
    public String toString() {
        return "Seat <"
                + "row " + rowNum
                + ", seat in the row " + seatNum + ">";
    }
}