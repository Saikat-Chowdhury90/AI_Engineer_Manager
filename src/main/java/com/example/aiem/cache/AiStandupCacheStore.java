package com.example.aiem.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AiStandupCacheStore {
    private static final Logger logger = LoggerFactory.getLogger(AiStandupCacheStore.class);
    private final ConcurrentHashMap<String, AIStandupCache> cacheMap= new ConcurrentHashMap<>();

    public String get(String repoKey, LocalDate date) {
        AIStandupCache cache = cacheMap.get(repoKey);
        logger.info("Cache lookup for repoKey: {}, date: {}, found: {}", repoKey, date, cache != null);
        if (cache != null && cache.getDate().equals(date)) {
            return cache.getStandupSummary();
        }
        return null;
    }

    public void put(String repoKey, LocalDate date, String standupSummary) {
        AIStandupCache cache = new AIStandupCache(repoKey, date, standupSummary);
        cacheMap.put(repoKey, cache);
    }
}
