package com.example.calendar.Model;

import java.time.LocalDate;

public class PeriodEntry {
    private int id;
    private String startDate;
    private String  endDate;
    private String periodFlow;

    public PeriodEntry() {
    }

    public PeriodEntry(int id,String  startDate, String  endDate, String periodFlow) {
        this.id=id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.periodFlow = periodFlow;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPeriodFlow() {
        return periodFlow;
    }

    public void setPeriodFlow(String periodFlow) {
        this.periodFlow = periodFlow;
    }
}
