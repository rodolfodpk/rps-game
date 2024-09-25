package com.rpsg.model;

import java.io.Serializable;
import java.util.List;

public record GameState(String gameId, List<GameEvent> events) implements Serializable {
}
// TODO should have GameStatus
