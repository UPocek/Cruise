package com.cruisemobile.cruise.models;

public class DayDTO {
    private String date;
    private double value;

    public DayDTO() {
    }

    public DayDTO(String date, double value) {
        this.date = date;
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void addToValue(double value) {
        this.value += value;
    }
}
