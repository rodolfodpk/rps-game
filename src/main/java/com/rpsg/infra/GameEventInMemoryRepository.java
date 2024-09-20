package com.rpsg.infra;

import com.rpsg.model.GameEvent;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.impl.factory.Multimaps;
import org.springframework.stereotype.Repository;

@Repository
public class GameEventInMemoryRepository implements com.rpsg.model.GameEventRepository {

    private final MutableListMultimap<String, GameEvent> multimap = Multimaps.mutable.list.empty();

    @Override
    public void appendEvent(String gameId, GameEvent gameEvent) {
        multimap.put(gameId, gameEvent);
    }

    @Override
    public ImmutableList<GameEvent> findAll(String gameId) {
        return multimap.get(gameId).toImmutable();
    }
}