package com.example.aiem.service.impl;

import com.example.aiem.client.GitHubClient;
import com.example.aiem.dto.GithubCommitResponse;
import com.example.aiem.dto.StandupResponse;
import com.example.aiem.entity.GithubActivityEntity;
import com.example.aiem.model.GithubActivity;
import com.example.aiem.repository.GithubActivityRespository;
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
    @Value("${github.owner}")
    private String owner;
    @Value("${github.repo}")
    private String repo;

    public StandupServiceImpl(GithubActivityRespository repository, GitHubClient gitHubClient) {

        this.repository = repository;
        this.gitHubClient = gitHubClient;
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

        String summary = buildSummary(activity);

        logger.info("Standup summary generated successfully");

        return new StandupResponse(
                LocalDate.now().toString(),
                summary
        );
    }

    private GithubActivity fetchGithubActivity() {
        logger.info("fetching GitHub activity");

        GithubCommitResponse[] commits = gitHubClient.getCommits(owner, repo);

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

}
