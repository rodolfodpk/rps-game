package com.rpsg.model;

import org.eclipse.collections.api.list.ImmutableList;

public interface GameEventRepository {

    void appendEvent(String gameId, GameEvent gameEvent);

    ImmutableList<GameEvent> findAll(String gameId);
}