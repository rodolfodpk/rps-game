package com.rpsg.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.rpsg.model.GameStatus;
import com.rpsg.model.GameStatusRepository;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class GameStatusCaffeineRepository implements GameStatusRepository {

    private final Cache<String, GameStatus> cache;

    public GameStatusCaffeineRepository() {
        cache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(10)) // Entries will be expired after n minutes since their last write
                .build();
    }

    @Override
    public void setStatus(String gameId, GameStatus status) {
        cache.put(gameId, status);
    }

    @Override
    public GameStatus getStatus(String gameId) {
        return cache.getIfPresent(gameId);
    }
}