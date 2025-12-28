package com.example.aiem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "github_activity")
@NoArgsConstructor
@Getter
@Setter
public class GithubActivityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int commitsToday;
    private int prsMerged;
    private int prsBlocked;

    public GithubActivityEntity(int commitsToday, int prsMerged, int prsBlocked) {
        this.commitsToday = commitsToday;
        this.prsMerged = prsMerged;
        this.prsBlocked = prsBlocked;
    }
}
