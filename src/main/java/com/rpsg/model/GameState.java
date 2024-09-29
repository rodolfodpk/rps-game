package com.rpsg.model;

import java.io.Serializable;
import java.util.List;

public record GameState(String gameId, GameStatus status, List<GameEvent> events) implements Serializable {
}

