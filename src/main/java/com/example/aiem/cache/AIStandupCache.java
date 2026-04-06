package com.example.aiem.cache;

import java.time.LocalDate;

public class AIStandupCache {

    private String repoKey;
    private LocalDate date;
    private String standupSummary;

    public AIStandupCache(String repoKey, LocalDate date, String standupSummary) {
        this.repoKey = repoKey;
        this.date = date;
        this.standupSummary = standupSummary;
    }

    public String getRepoKey() {
        return repoKey;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getStandupSummary() {
        return standupSummary;
    }
}
