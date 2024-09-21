package com.rpsg.model;


//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
//@JsonSubTypes({
//        @JsonSubTypes.Type(value = GameCommand.StartGame.class, name = "StartGame"),
//        @JsonSubTypes.Type(value = GameCommand.PlayRound.class, name = "PlayRound"),
//        @JsonSubTypes.Type(value = GameCommand.EndGame.class, name = "EndGame"),
//})
public sealed interface GameCommand
        permits GameCommand.StartGame, GameCommand.PlayRound, GameCommand.EndGame {

    record StartGame(String player) implements GameCommand {
    }

    record PlayRound(Move playerMove) implements GameCommand {
    }

    record EndGame() implements GameCommand {
    }

}

// Note the Jackson annotations are just in case we need to ser/der in a polymorphic way using ObjectMapper
