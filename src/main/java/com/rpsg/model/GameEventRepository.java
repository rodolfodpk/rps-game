package com.rpsg.model;

import java.util.List;

public interface GameEventRepository {

    List<GameEvent> appendEvent(String gameId, GameEvent gameEvent);

    List<GameEvent> findAll(String gameId);
}