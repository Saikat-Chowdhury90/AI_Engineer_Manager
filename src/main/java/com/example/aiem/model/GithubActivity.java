package com.example.aiem.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GithubActivity {
    private int commitsToday;
    private int prsMerged;
    private int prsBlocked;

    public GithubActivity(int commitsToday, int prsMerged, int prsBlocked) {
        this.commitsToday = commitsToday;
        this.prsMerged = prsMerged;
        this.prsBlocked = prsBlocked;
    }
}
