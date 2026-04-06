package com.example.aiem.service.impl;

import com.example.aiem.client.GitHubClient;
import com.example.aiem.dto.GithubCommitResponse;
import com.example.aiem.dto.StandupResponse;
import com.example.aiem.entity.GithubActivityEntity;
import com.example.aiem.exception.GitHubApiException;
import com.example.aiem.model.GithubActivity;
import com.example.aiem.repository.GithubActivityRespository;
import com.example.aiem.service.StandupAIService;
import com.example.aiem.service.StandupService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class StandupServiceImpl implements StandupService {

    private static final Logger logger = LoggerFactory.getLogger(StandupServiceImpl.class);

    private final GithubActivityRespository repository;
    private final GitHubClient gitHubClient;
    private final StandupAIService standupAIService;
    @Value("${github.owner}")
    private String owner;
    @Value("${github.repo}")
    private String repo;

    public StandupServiceImpl(GithubActivityRespository repository, GitHubClient gitHubClient, StandupAIService standupAIService) {

        this.repository = repository;
        this.gitHubClient = gitHubClient;
        this.standupAIService = standupAIService;
    }

    @Override
    public StandupResponse generateStandup() {

        logger.info("generating standup report");

        GithubActivity activity = fetchGithubActivity();
        //GithubActivity activity = simulateGithubActivity();

        repository.save(new GithubActivityEntity(
                activity.getCommitsToday(),
                activity.getPrsMerged(),
                activity.getPrsBlocked()
        ));

        logger.debug("GitHub activity fetched: Commits Today - {}, PRs Merged - {}, PRs Blocked - {}",
                activity.getCommitsToday(), activity.getPrsMerged(), activity.getPrsBlocked());

        String summary;
        try {
            summary = standupAIService.analyzeActivity(activity);
        } catch (Exception e) {
            logger.error("AI analysis failed. Falling back to basic summary", e);
            summary = buildSummary(activity);
        }

        logger.info("Standup summary generated successfully");

        return new StandupResponse(
                LocalDate.now().toString(),
                summary
        );
    }

    private GithubActivity fetchGithubActivity() {

        try{

            logger.info("fetching GitHub activity for repo: {}/{}", owner, repo);

            GithubCommitResponse[] commits = fetchWithRetry(3);

            // Parse the responses to extract the required data
            int commitsToday = 0;

            if(commits != null){
                for (GithubCommitResponse commit : commits) {
                    if(commit.getCommit() != null && commit.getCommit().getAuthor() != null){

                        String commitDate = commit.getCommit().getAuthor().getDate();

                        if (commitDate.startsWith(LocalDate.now().toString())) {
                            commitsToday++;
                        }
                    }
                }
            }
            // For demonstration, we will simulate the data

            return new GithubActivity(
                    commitsToday,  // commitsToday
                    0,   // prsMerged
                    0    // prsBlocked
            );
        } catch (GitHubApiException e) {
            logger.error("GitHub API failed. Falling back to safe defaults", e);

            // Fallback – system keeps working
            return new GithubActivity(
                    0,
                    0,
                    0
            );
        }
    }

    private GithubActivity simulateGithubActivity() {

        logger.info("simulating GitHub activity fetch");
        // Simulate fetching GitHub activity data
        return new GithubActivity(
                12,  // commitsToday
                3,  // prsMerged
                1   // prsBlocked
        );
    }

    private String buildSummary(GithubActivity activity) {

        logger.info("building summary");

        StringBuilder summary = new StringBuilder();
        summary.append(activity.getCommitsToday())
                .append(" commits pushed today. ");
        summary.append(activity.getPrsMerged())
                .append(" PRs merged. ");
        if(activity.getPrsBlocked() > 0){
            summary.append(activity.getPrsBlocked())
                    .append(" PRs blocked.");
        }
        else{
            summary.append("No PRs blocked.");
        }
        return summary.toString();
    }

    private GithubCommitResponse[] fetchWithRetry(int attempts) {

        int count = 0;

        while (count < attempts) {
            try {
                return gitHubClient.getCommits(owner, repo);
            } catch (GitHubApiException ex) {
                count++;
                logger.warn("Retrying GitHub API call... attempt {}", count);
            }
        }

        throw new GitHubApiException("GitHub API failed after retries");
    }


}
