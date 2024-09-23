package com.rpsg.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameStateTest {

    @Test
    void shouldReturnHumanAsWinner() {
        List<GameEvent.RoundPlayed> events = List.of(
                createGameEventWithWinner(Winner.HUMAN),
                createGameEventWithWinner(Winner.GAME),
                createGameEventWithWinner(Winner.HUMAN)
        );
        assertEquals(Winner.HUMAN, GameState.determineWinner(events));
    }

    @Test
    void shouldReturnGameAsWinner() {
        List<GameEvent.RoundPlayed> events = List.of(
                createGameEventWithWinner(Winner.GAME),
                createGameEventWithWinner(Winner.GAME),
                createGameEventWithWinner(Winner.HUMAN)
        );
        assertEquals(Winner.GAME, GameState.determineWinner(events));
    }

    @Test
    void shouldReturnDrawIfEqualWins() {
        List<GameEvent.RoundPlayed> events = List.of(
                createGameEventWithWinner(Winner.HUMAN),
                createGameEventWithWinner(Winner.GAME),
                createGameEventWithWinner(Winner.HUMAN),
                createGameEventWithWinner(Winner.GAME)
        );
        assertEquals(Winner.DRAW, GameState.determineWinner(events));
    }

    @Test
    void shouldReturnDrawIfAllDraws() {
        List<GameEvent.RoundPlayed> events = List.of(
                createGameEventWithWinner(Winner.DRAW),
                createGameEventWithWinner(Winner.DRAW),
                createGameEventWithWinner(Winner.DRAW)
        );
        assertEquals(Winner.DRAW, GameState.determineWinner(events));
    }

    private GameEvent.RoundPlayed createGameEventWithWinner(Winner winner) {
        return new GameEvent.RoundPlayed("1", null, null, winner);
    }
}