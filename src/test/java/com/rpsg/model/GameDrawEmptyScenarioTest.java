package com.rpsg.model;

import com.rpsg.model.GameCommand.StartGame;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static com.rpsg.model.Winner.DRAW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GameDrawEmptyScenarioTest extends AbstractScenarioTest {

    private GameState initialState;

    @Test
    @Order(1)
    public void startGame() {
        // given
        var startGame = new StartGame("Player1");
        // when
        initialState = startGameHandler.handle(gameID, startGame);
        // then
        assertEquals(1, initialState.events().size());
        assertNotNull(initialState.gameId());
        var latestEvent = (GameEvent.GameStarted) initialState.events().getFirst();
        assertEquals("Player1", latestEvent.player());
        assertNotNull(initialState.gameId());
    }

    @Test
    @Order(2)
    public void endGameWithNoPlaysShouldBeDRAW() {
        // given
        var endGame = new GameCommand.EndGame();
        // when
        var newState = endGameHandler.handle(gameID, endGame);
        // then state
        assertEquals(2, newState.events().size());
        assertNotNull(newState.gameId());
        assertEquals(initialState.gameId(), newState.gameId());
        // then last event
        var latestEvent = (GameEvent.GameEnded) newState.events().getLast();
        assertNotNull(latestEvent.gameId());
        assertEquals(DRAW, latestEvent.winner());
    }

}