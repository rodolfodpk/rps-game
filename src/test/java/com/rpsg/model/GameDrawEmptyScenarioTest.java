package com.rpsg.model;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static com.rpsg.model.Winner.DRAW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GameDrawEmptyScenarioTest extends AbstractScenarioTest {

    @Test
    @Order(1)
    public void startGame() {
        // given
        var playerName = "Player1";
        // when
        var startedGameEvent = startGameHandler.handle(gameID, playerName, null);
        events.add(startedGameEvent);
        // then
        assertNotNull(startedGameEvent.gameId());
        assertEquals(playerName, startedGameEvent.player());
    }

    @Test
    @Order(2)
    public void endGameWithNoPlaysShouldBeDraw() {
        // when
        var state = new GameState(gameID, events);
        var newState = endGameHandler.handle(state);
        // then state
        assertEquals(2, newState.events().size());
        assertNotNull(newState.gameId());
        assertEquals(gameID, newState.gameId());
        // then last event
        var latestEvent = (GameEvent.GameEnded) newState.events().getLast();
        assertNotNull(latestEvent.gameId());
        assertEquals(DRAW, latestEvent.winner());
    }

}