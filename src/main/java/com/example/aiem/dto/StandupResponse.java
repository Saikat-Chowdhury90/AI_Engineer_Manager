package com.example.aiem.dto;


public class StandupResponse {

    private String date;
    private String summary;

    public StandupResponse(String date, String summary) {
        this.date = date;
        this.summary = summary;
    }

    public String getDate() {
        return date;
    }
    public String getSummary() {
        return summary;
    }

}
