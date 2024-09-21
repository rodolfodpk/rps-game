package com.rpsg.model;

import com.rpsg.model.GameCommand.PlayRound;
import com.rpsg.model.GameCommand.StartGame;
import com.rpsg.model.handlers.GameMoveDecider;
import com.rpsg.model.handlers.PlayRoundHandler;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameBasicScenarioTest extends AbstractScenarioTest {

    private final GameMoveDecider gameMoveDecider = mock(GameMoveDecider.class);
    private final PlayRoundHandler playRoundHandler = new PlayRoundHandler(gameEventRepository, gameMoveDecider);

    @Test
    @Order(1)
    public void startGame() {
        // given
        var startGame = new StartGame("Player1");
        // when
        var initialState = startGameHandler.handle(gameID, startGame);
        // then
        assertNotNull(gameID);
        var latestEvent = (GameEvent.GameStarted) initialState.events().getFirst();
        assertEquals("Player1", latestEvent.player());
        assertNotNull(gameID);
    }

    @Test
    @Order(2)
    public void playRound1() {
        // given
        var playRound = new PlayRound(Move.ROCK);
        when(gameMoveDecider.determineGameMove()).thenReturn(Move.PAPER);
        // when
        var newState = playRoundHandler.handle(gameID, playRound);
        // then event
        var latestEvent = (GameEvent.RoundPlayed) newState.events().getLast();
        assertEquals(Move.ROCK, latestEvent.playerMove());
        assertEquals(Move.PAPER, latestEvent.gameMove());
        assertEquals(Winner.GAME, latestEvent.winner());
    }

    @Test
    @Order(3)
    public void playRound2() {
        // given
        var playRound = new PlayRound(Move.SCISSORS);
        // when
        when(gameMoveDecider.determineGameMove()).thenReturn(Move.PAPER);
        var newState = playRoundHandler.handle(gameID, playRound);
        // then event
        var latestEvent = (GameEvent.RoundPlayed) newState.events().getLast();
        assertEquals(Move.SCISSORS, latestEvent.playerMove());
        assertEquals(Move.PAPER, latestEvent.gameMove());
        assertEquals(Winner.HUMAN, latestEvent.winner());
    }

    @Test
    @Order(4)
    public void playRound3() {
        // given
        var playRound = new PlayRound(Move.ROCK);
        when(gameMoveDecider.determineGameMove()).thenReturn(Move.PAPER);
        // when
        var newState = playRoundHandler.handle(gameID, playRound);
        // then event
        var latestEvent = (GameEvent.RoundPlayed) newState.events().getLast();
        assertEquals(Move.ROCK, latestEvent.playerMove());
        assertEquals(Move.PAPER, latestEvent.gameMove());
        assertEquals(Winner.GAME, latestEvent.winner());
    }

    @Test
    @Order(5)
    public void endGameWon() {
        // given
        var endGame = new GameCommand.EndGame();
        // when
        var newState = endGameHandler.handle(gameID, endGame);
        // then state
        assertEquals(5, newState.events().size());
        // then last event
        var latestEvent = (GameEvent.GameEnded) newState.events().getLast();
        assertEquals(Winner.GAME, latestEvent.winner());
    }


}