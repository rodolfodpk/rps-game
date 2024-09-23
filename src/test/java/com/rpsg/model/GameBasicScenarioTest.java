package com.rpsg.model;

import com.rpsg.model.handlers.PlayRoundHandler;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameBasicScenarioTest extends AbstractScenarioTest {

    private final PlayRoundHandler.GameMoveDecider gameMoveDecider = mock(PlayRoundHandler.GameMoveDecider.class);
    private final PlayRoundHandler playRoundHandler = new PlayRoundHandler(gameEventRepository, gameMoveDecider);

    @Test
    @Order(1)
    public void startGame() {
        // given
        var playerName = "Player1";
        // when
        var startedGameEvent = startGameHandler.handle(gameID, playerName);
        // then
        assertNotNull(gameID);
        assertEquals("Player1", startedGameEvent.player());
    }

    @Test
    @Order(2)
    public void playRound1() {
        // given
        var playRound = Move.ROCK;
        when(gameMoveDecider.determineGameMove()).thenReturn(Move.PAPER);
        // when
        var roundPlayedEvent = playRoundHandler.handle(gameID, playRound);
        // then event
        assertEquals(Move.ROCK, roundPlayedEvent.playerMove());
        assertEquals(Move.PAPER, roundPlayedEvent.gameMove());
        assertEquals(Winner.GAME, roundPlayedEvent.winner());
    }

    @Test
    @Order(3)
    public void playRound2() {
        // given
        var playRound = Move.SCISSORS;
        // when
        when(gameMoveDecider.determineGameMove()).thenReturn(Move.PAPER);
        // when
        var roundPlayedEvent = playRoundHandler.handle(gameID, playRound);
        // then event
        assertEquals(Move.SCISSORS, roundPlayedEvent.playerMove());
        assertEquals(Move.PAPER, roundPlayedEvent.gameMove());
        assertEquals(Winner.HUMAN, roundPlayedEvent.winner());
    }

    @Test
    @Order(4)
    public void playRound3() {
        // given
        var playRound = Move.ROCK;
        when(gameMoveDecider.determineGameMove()).thenReturn(Move.PAPER);
        // when
        var latestEvent = playRoundHandler.handle(gameID, playRound);
        // then event
        assertEquals(Move.ROCK, latestEvent.playerMove());
        assertEquals(Move.PAPER, latestEvent.gameMove());
        assertEquals(Winner.GAME, latestEvent.winner());
    }

    @Test
    @Order(5)
    public void endGameWon() {
        // when
        var newState = endGameHandler.handle(gameID);
        // then state
        assertEquals(5, newState.events().size());
        // then last event
        var latestEvent = (GameEvent.GameEnded) newState.events().getLast();
        assertEquals(Winner.GAME, latestEvent.winner());
    }


}