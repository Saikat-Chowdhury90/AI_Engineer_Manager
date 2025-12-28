package com.example.aiem.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubCommitResponse {

    private String sha;
    private Commit commit;
    public String getSha() {
        return sha;
    }
    public  Commit getCommit() {
        return commit;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Commit {
        private Author author;
        public Author getAuthor() {
            return author;
        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Author {
        private String date;

        public String getDate() {
            return date;
        }
    }
}
