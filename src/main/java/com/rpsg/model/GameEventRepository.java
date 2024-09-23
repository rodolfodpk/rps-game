package com.rpsg.model;

import org.pcollections.PVector;

import java.util.List;

public interface GameEventRepository {

    PVector<GameEvent> appendEvent(String gameId, GameEvent gameEvent);

    List<GameEvent> findAll(String gameId);
}