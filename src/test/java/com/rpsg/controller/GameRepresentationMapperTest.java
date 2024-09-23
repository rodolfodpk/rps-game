package com.rpsg.controller;

import com.rpsg.model.GameEvent;
import com.rpsg.model.GameState;
import com.rpsg.model.Move;
import com.rpsg.model.Winner;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GameRepresentationMapperTest {

    private final GameRepresentationMapper gameRepresentationMapper = new GameRepresentationMapper();

    @Test
    public void shouldMapToRepresentation() {
        // Given
        String gameId = "game1";
        List<GameEvent> gameEvents = List.of(
                new GameEvent.GameStarted(gameId, "player1"),
                new GameEvent.RoundPlayed(gameId, Move.PAPER, Move.ROCK, Winner.HUMAN),
                new GameEvent.GameEnded(gameId, Winner.HUMAN)
        );
        GameState gameState = new GameState(gameId, gameEvents);

        // When
        GameRepresentation gameRepresentation = gameRepresentationMapper.map(gameState);

        // Then
        assertNotNull(gameRepresentation);
        assertEquals(gameId, gameRepresentation.gameId());
        assertEquals("player1", gameRepresentation.playerId());
        assertEquals(Winner.HUMAN, gameRepresentation.winner());
        assertNotNull(gameRepresentation.stats());

        var stats = gameRepresentation.stats();
        assertEquals(1L, stats.moves().get("Player").get(Move.PAPER));
        assertEquals(1L, stats.moves().get("Game").get(Move.ROCK));
        assertEquals(1L, stats.winners().get(Winner.HUMAN));
    }
}