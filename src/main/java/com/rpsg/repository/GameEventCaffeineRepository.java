package com.rpsg.repository;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.rpsg.model.GameEvent;
import org.pcollections.PVector;
import org.pcollections.TreePVector;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Repository
public class GameEventCaffeineRepository implements com.rpsg.model.GameEventRepository {

    private final LoadingCache<String, PVector<GameEvent>> cache = Caffeine.newBuilder()
//            .maximumSize(500_000)
            .expireAfterWrite(Duration.ofMinutes(10)) // Entries will be expired after n minutes since their last write
            .build(this::loadGameEvents);

    private PVector<GameEvent> loadGameEvents(String gameId) {
        return TreePVector.empty(); // Or load from your data source
    }

    @Override
    public List<GameEvent> appendEvent(String gameId, GameEvent gameEvent) {
        var newEvents = cache.get(gameId).plus(gameEvent);
        cache.put(gameId, newEvents);
        return Collections.unmodifiableList(newEvents);
    }

    @Override
    public List<GameEvent> findAll(String gameId) {
        return cache.get(gameId);
    }
}