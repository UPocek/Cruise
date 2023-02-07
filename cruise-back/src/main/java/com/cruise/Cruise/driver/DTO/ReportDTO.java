package com.cruise.Cruise.driver.DTO;

import java.util.ArrayList;
import java.util.List;

public class ReportDTO {
    private String title;
    private String label;
    private double sum;
    private double avg;
    private List<DayDTO> days;

    public ReportDTO() {
    }

    public ReportDTO(String title, String label, double sum, double avg) {
        this.title = title;
        this.label = label;
        this.sum = sum;
        this.avg = avg;
        this.days = new ArrayList<>();
    }

    public ReportDTO(String title, String label, double sum, double avg, List<DayDTO> days) {
        this.title = title;
        this.label = label;
        this.sum = sum;
        this.avg = avg;
        this.days = days;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public void addSum(double amount) {
        this.sum += amount;
    }

    public double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    public List<DayDTO> getDays() {
        return days;
    }

    public void setDays(List<DayDTO> days) {
        this.days = days;
    }

    public void addDays(DayDTO day) {
        this.days.add(day);
    }
}
