package com.rpsg.model;

import com.rpsg.model.handlers.PlayRoundHandler;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static com.rpsg.model.Winner.DRAW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameDrawScenarioTest extends AbstractScenarioTest {

    private final PlayRoundHandler.GameMoveDecider gameMoveDecider = mock(PlayRoundHandler.GameMoveDecider.class);
    private final PlayRoundHandler playRoundHandler = new PlayRoundHandler(gameEventRepository, gameMoveDecider);

    @Test
    @Order(1)
    public void startGame() {
        // given
        var playerName = "Player1";
        // when
        var event = startGameHandler.handle(gameID, playerName);
        // then
        assertNotNull(event.gameId());
        assertEquals(playerName, event.player());
    }

    @Test
    @Order(1)
    public void playRound1Rock() {
        // given
        when(gameMoveDecider.determineGameMove()).thenReturn(Move.ROCK);
        // when
        var latestEvent = playRoundHandler.handle(gameID, Move.ROCK);
        // then event
        assertEquals(Move.ROCK, latestEvent.playerMove());
        assertEquals(Move.ROCK, latestEvent.gameMove());
        assertEquals(DRAW, latestEvent.winner());
    }

    @Test
    @Order(2)
    public void playRound2Paper() {
        // given
        when(gameMoveDecider.determineGameMove()).thenReturn(Move.PAPER);
        // when
        var latestEvent = playRoundHandler.handle(gameID, Move.PAPER);
        // then event
        assertEquals(Move.PAPER, latestEvent.playerMove());
        assertEquals(Move.PAPER, latestEvent.gameMove());
        assertEquals(DRAW, latestEvent.winner());
    }

    @Test
    @Order(3)
    public void endGameShouldBeDraw() {
        // when
        var newState = endGameHandler.handle(gameID);
        // then state
        assertEquals(4, newState.events().size());
        assertNotNull(newState.gameId());
        // then last event
        var latestEvent = (GameEvent.GameEnded) newState.events().getLast();
        assertNotNull(latestEvent.gameId());
        assertEquals(DRAW, latestEvent.winner());
    }

}