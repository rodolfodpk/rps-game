package com.rpsg.repository;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.rpsg.model.GameEvent;
import org.pcollections.PVector;
import org.pcollections.TreePVector;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class GameEventInMemoryRepository implements com.rpsg.model.GameEventRepository {

    private final LoadingCache<String, PVector<GameEvent>> cache = Caffeine.newBuilder()
//            .maximumSize(500_000)
            .expireAfterWrite(Duration.ofMinutes(10)) // Entries will be expired after n minutes since their last write
            .build(this::loadGameEvents);

    private PVector<GameEvent> loadGameEvents(String gameId) {
        return TreePVector.empty(); // Or load from your data source
    }

    @Override
    public PVector<GameEvent> appendEvent(String gameId, GameEvent gameEvent) {
        PVector<GameEvent> events = cache.get(gameId);
        events = events.plus(gameEvent);
        cache.put(gameId, events);
        return events;
    }

    @Override
    public PVector<GameEvent> findAll(String gameId) {
        return cache.get(gameId);
    }
}