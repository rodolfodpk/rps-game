package com.rpsg.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = GameEvent.GameStarted.class, name = "GameStarted"),
        @JsonSubTypes.Type(value = GameEvent.RoundPlayed.class, name = "RoundPlayed"),
        @JsonSubTypes.Type(value = GameEvent.GameEnded.class, name = "GameEnded"),
})
public sealed interface GameEvent
        permits GameEvent.GameStarted, GameEvent.RoundPlayed, GameEvent.GameEnded {

    record GameStarted(String gameId, String player) implements GameEvent {
    }

    record RoundPlayed(String gameId, Move playerMove, Move gameMove, Winner winner) implements GameEvent {
    }

    record GameEnded(String gameId, Winner winner) implements GameEvent {
    }

}
// Note the Jackson annotations are just in case we need to ser/der in a polymorphic way using ObjectMapper
// For example: a new GameEventRepository implementation could leverage it