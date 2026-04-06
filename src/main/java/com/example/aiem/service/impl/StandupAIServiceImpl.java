package com.example.aiem.service.impl;

import com.example.aiem.cache.AiStandupCacheStore;
import com.example.aiem.client.GeminiClient;
import com.example.aiem.model.GithubActivity;
import com.example.aiem.service.StandupAIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StandupAIServiceImpl implements StandupAIService {

    private final GeminiClient aiClient;
    private final AiStandupCacheStore cacheStore;
    @Value("${github.owner}")
    private String owner;
    @Value("${github.repo}")
    private String repo;

    public StandupAIServiceImpl(GeminiClient aiClient, AiStandupCacheStore cacheStore) {
        this.aiClient = aiClient;
        this.cacheStore = cacheStore;
    }

    @Override
    public String analyzeActivity(GithubActivity activity) {

        //String cacheKey = owner + ":" + repo + ":" +
        //        activity.getCommitsToday() + ":" +
        //        activity.getPrsMerged() + ":" +
        //        activity.getPrsBlocked();

        String repoKey = owner + ":" + repo + ":" + activity.getCommitsToday() + ":" +
                        activity.getPrsMerged() + ":" + activity.getPrsBlocked();
        String cachedSummary = cacheStore.get(repoKey, java.time.LocalDate.now());
        if (cachedSummary != null) {
            return cachedSummary;
        }

        String prompt = """
                 Here is today's GitHub activity for the team:
                                - Commits today: %d
                                - PRs merged: %d
                                - PRs blocked: %d
                
                                As an Engineering Manager:
                                - Assess progress
                                - Identify risks
                                - Suggest next actions
                                Keep it concise (3–4 lines).
                """
                .formatted(
                        activity.getCommitsToday(),
                        activity.getPrsMerged(),
                        activity.getPrsBlocked()
                );

        String summary = aiClient.analyze(prompt);

        cacheStore.put(repoKey, java.time.LocalDate.now(), summary);

        return summary;
    }
}
