package com.rpsg.model;

public sealed interface GameCommand
        permits GameCommand.StartGame, GameCommand.PlayRound, GameCommand.EndGame {

    record StartGame(String player) implements GameCommand {
    }

    record PlayRound(Move playerMove) implements GameCommand {
    }

    record EndGame() implements GameCommand {
    }
}
