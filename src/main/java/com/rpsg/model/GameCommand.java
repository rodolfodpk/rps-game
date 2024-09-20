package com.rpsg.model;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = GameCommand.StartGame.class, name = "StartGame"),
        @JsonSubTypes.Type(value = GameCommand.PlayRound.class, name = "PlayRound"),
        @JsonSubTypes.Type(value = GameCommand.EndGame.class, name = "EndGame"),
})
public sealed interface GameCommand
        permits GameCommand.StartGame, GameCommand.PlayRound, GameCommand.EndGame {

    record StartGame(String player) implements GameCommand {
    }

    record PlayRound(String gameId, Move playerMove) implements GameCommand {
    }

    record EndGame(String gameId) implements GameCommand {
    }

}

