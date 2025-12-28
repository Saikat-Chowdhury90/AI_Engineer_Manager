package com.example.aiem.client;

import com.example.aiem.dto.GithubCommitResponse;
import com.example.aiem.exception.GitHubApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
public class GitHubClient {

    @Value("${github.token}")
    private String token;

    private final RestTemplate restTemplate= new RestTemplate();

    public ResponseEntity<String> get(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("Accept", "application/vnd.github+json");

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    public GithubCommitResponse[] getCommits(String owner , String repo) {
        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/commits";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.set("Accept", "application/vnd.github+json");

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<GithubCommitResponse[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, GithubCommitResponse[].class);
            return response.getBody();
        }catch (HttpClientErrorException e){

            throw new GitHubApiException(
                    "Error fetching commits from GitHub API: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(),
                    e
            );
        }catch (HttpServerErrorException e) {
            // 500 errors
            throw new GitHubApiException(
                    "Server error from GitHub API",
                    e
            );

        } catch (ResourceAccessException e) {
            // Timeout / network
            throw new GitHubApiException(
                    "Network error while calling GitHub API",
                    e
            );
        }
    }

}
