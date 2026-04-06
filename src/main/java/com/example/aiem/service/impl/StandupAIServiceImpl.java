package com.example.aiem.service.impl;

import com.example.aiem.client.GeminiClient;
import com.example.aiem.model.GithubActivity;
import com.example.aiem.service.StandupAIService;
import org.springframework.stereotype.Service;

@Service
public class StandupAIServiceImpl implements StandupAIService {

    private final GeminiClient aiClient;

    public StandupAIServiceImpl(GeminiClient aiClient) {
        this.aiClient = aiClient;
    }

    @Override
    public String analyzeActivity(GithubActivity activity) {

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

        return aiClient.analyze(prompt);
    }
}
