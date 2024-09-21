package com.rpsg.model;

import com.rpsg.model.GameCommand.PlayRound;
import com.rpsg.model.GameCommand.StartGame;
import com.rpsg.model.handlers.GameMoveDecider;
import com.rpsg.model.handlers.PlayRoundHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameBasicScenarioTest implements AbstractScenarioTest {

    private static final GameMoveDecider gameMoveDecider = mock(GameMoveDecider.class);
    private static final PlayRoundHandler playRoundHandler = new PlayRoundHandler(gameEventRepository, gameMoveDecider);

    private static GameState initialState;

    @BeforeAll
    public static void startGame() {
        // given
        var startGame = new StartGame("Player1");
        // when
        initialState = startGameHandler.handle(startGame);
        // then
        assertNotNull(initialState.gameId());
        var latestEvent = (GameEvent.GameStarted) initialState.events().getFirst();
        assertEquals("Player1", latestEvent.player());
        assertNotNull(initialState.gameId());
    }

    @Test
    @Order(1)
    public void playRound1() {
        // given
        var playRound = new PlayRound(initialState.gameId(), Move.ROCK);
        when(gameMoveDecider.determineGameMove()).thenReturn(Move.PAPER);
        // when
        var newState = playRoundHandler.handle(playRound);
        // then state
        assertEquals(initialState.gameId(), newState.gameId());
        // then event
        var latestEvent = (GameEvent.RoundPlayed) newState.events().getLast();
        assertEquals(Move.ROCK, latestEvent.playerMove());
        assertEquals(Move.PAPER, latestEvent.gameMove());
        assertEquals(Winner.GAME, latestEvent.winner());
    }

    @Test
    @Order(2)
    public void playRound2() {
        // given
        var playRound = new PlayRound(initialState.gameId(), Move.SCISSORS);
        // when
        when(gameMoveDecider.determineGameMove()).thenReturn(Move.PAPER);
        var newState = playRoundHandler.handle(playRound);
        // then state
        assertEquals(initialState.gameId(), newState.gameId());
        // then event
        var latestEvent = (GameEvent.RoundPlayed) newState.events().getLast();
        assertEquals(Move.SCISSORS, latestEvent.playerMove());
        assertEquals(Move.PAPER, latestEvent.gameMove());
        assertEquals(Winner.HUMAN, latestEvent.winner());
    }

    @Test
    @Order(3)
    public void playRound3() {
        // given
        var playRound = new PlayRound(initialState.gameId(), Move.ROCK);
        when(gameMoveDecider.determineGameMove()).thenReturn(Move.PAPER);
        // when
        var newState = playRoundHandler.handle(playRound);
        // then state
        assertEquals(initialState.gameId(), newState.gameId());
        // then event
        var latestEvent = (GameEvent.RoundPlayed) newState.events().getLast();
        assertEquals(Move.ROCK, latestEvent.playerMove());
        assertEquals(Move.PAPER, latestEvent.gameMove());
        assertEquals(Winner.GAME, latestEvent.winner());
    }

    @Test
    @Order(4)
    public void endGameWon() {
        // given
        var endGame = new GameCommand.EndGame(initialState.gameId());
        // when
        var newState = endGameHandler.handle(endGame);
        // then state
        assertNotNull(newState.gameId());
        assertEquals(initialState.gameId(), newState.gameId());
        assertEquals(5, newState.events().size());
        // then last event
        var latestEvent = (GameEvent.GameEnded) newState.events().getLast();
        assertEquals(Winner.GAME, latestEvent.winner());
    }


}