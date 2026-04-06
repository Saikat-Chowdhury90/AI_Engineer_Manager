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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class GitHubClient {

    private static final Logger logger = LoggerFactory.getLogger(GitHubClient.class);

    @Value("${github.token}")
    private String token;

    private final RestTemplate restTemplate= new RestTemplate();

    public ResponseEntity<String> get(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("Accept", "application/vnd.github+json");
        headers.set("User-Agent", "aiem-backend");

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    public GithubCommitResponse[] getCommits(String owner , String repo) {
        // We'll page through results; GitHub max per_page is 100
        final int perPage = 100;
        int page = 1;
        List<GithubCommitResponse> allCommits = new ArrayList<>();

        try {
            while (true) {
                String url = String.format("https://api.github.com/repos/%s/%s/commits?per_page=%d&page=%d", owner, repo, perPage, page);

                HttpHeaders headers = new HttpHeaders();
                // Use the 'token' scheme which is compatible with GitHub PATs. Do NOT set both token and Bearer to avoid overwriting header unexpectedly.
                if (token != null && !token.isEmpty()) {
                    headers.set("Authorization", "token " + token);
                }
                headers.set("Accept", "application/vnd.github+json");
                headers.set("User-Agent", "aiem-backend");

                logger.debug("Calling GitHub API page {} for {}/{}. Authorization header present: {}", page, owner, repo, token != null && !token.isEmpty());

                HttpEntity<Void> entity = new HttpEntity<>(headers);
                ResponseEntity<GithubCommitResponse[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, GithubCommitResponse[].class);

                GithubCommitResponse[] commits = response.getBody();
                int returned = commits == null ? 0 : commits.length;

                // Log response details for debugging pagination/token usage (do not log token)
                String linkHeader = response.getHeaders().getFirst("Link");
                String rateRemaining = response.getHeaders().getFirst("X-RateLimit-Remaining");
                String rateLimit = response.getHeaders().getFirst("X-RateLimit-Limit");
                String rateReset = response.getHeaders().getFirst("X-RateLimit-Reset");
                logger.debug("GitHub API page {} returned {} commits, status={}, linkHeader={}, rateRemaining={}, rateLimit={}, rateReset={}", page, returned, response.getStatusCode(), linkHeader, rateRemaining, rateLimit, rateReset);

                if (commits != null && commits.length > 0) {
                    allCommits.addAll(Arrays.asList(commits));
                }

                // If Link header doesn't include rel="next", we've reached the last page
                if (linkHeader == null || !linkHeader.contains("rel=\"next\"")) {
                    break;
                }

                page++;
            }

            return allCommits.toArray(new GithubCommitResponse[0]);

        } catch (HttpClientErrorException e){

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
