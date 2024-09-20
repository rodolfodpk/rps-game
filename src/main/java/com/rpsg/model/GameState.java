package com.rpsg.model;

import java.util.List;

public record GameState(String gameId, List<? extends GameEvent> events) {
}
