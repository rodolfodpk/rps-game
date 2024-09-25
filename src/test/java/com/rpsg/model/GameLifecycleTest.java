package com.rpsg.model;

import com.rpsg.model.handlers.PlayRoundHandler;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static com.rpsg.model.GameStatus.ENDED;
import static com.rpsg.model.GameStatus.STARTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameLifecycleTest extends AbstractScenarioTest {

    private final PlayRoundHandler.GameMoveDecider gameMoveDecider = mock(PlayRoundHandler.GameMoveDecider.class);
    private final PlayRoundHandler playRoundHandler = new PlayRoundHandler(gameMoveDecider);

    @Test
    @Order(1)
    public void startGame() {
        // given
        var playerName = "Player1";
        // when
        var startedGameEvent = startGameHandler.handle(gameID, playerName, null);
        events.add(startedGameEvent);
        // then
        assertEquals(gameID, startedGameEvent.gameId());
        assertEquals("Player1", startedGameEvent.player());
    }

    @Test
    @Order(2)
    public void startSameGameShouldFail() {
        // given
        var playerName = "Player1";
        // when
        var exception = assertThrows(IllegalStateException.class, () -> startGameHandler.handle(gameID, playerName, STARTED));
        // then
        assertEquals("Game is already started or ended", exception.getMessage());
    }

    @Test
    @Order(3)
    public void play() {
        // given
        var playRound = Move.ROCK;
        when(gameMoveDecider.determineGameMove()).thenReturn(Move.PAPER);
        // when
        var roundPlayedEvent = playRoundHandler.handle(gameID, playRound, GameStatus.STARTED);
        events.add(roundPlayedEvent);
        // then event
        assertEquals(Move.ROCK, roundPlayedEvent.playerMove());
        assertEquals(Move.PAPER, roundPlayedEvent.gameMove());
        assertEquals(Winner.GAME, roundPlayedEvent.winner());
    }

    @Test
    @Order(4)
    public void endGame() {
        // when
        var state = new GameState(gameID, events);
        var newState = endGameHandler.handle(state);
        // then state
        assertEquals(3, newState.events().size());
        // then last event
        var latestEvent = (GameEvent.GameEnded) newState.events().getLast();
        assertEquals(Winner.GAME, latestEvent.winner());
//        assertEquals(ENDED, newState.);
    }

    @Test
    @Order(5)
    public void endGameAgainMustFail() {
        // when
        var state = new GameState(gameID, events);
        var exception = assertThrows(IllegalStateException.class, () -> endGameHandler.handle(state));
        // then
        assertEquals("Game is already ended", exception.getMessage());
    }

    @Test
    @Order(6)
    public void playEndedGameShouldFail() {
        // given
        var playRound = Move.ROCK;
        // when
        var exception = assertThrows(IllegalStateException.class, () -> {
            when(gameMoveDecider.determineGameMove()).thenReturn(Move.PAPER);
            playRoundHandler.handle(gameID, playRound, ENDED);

        });
        // then
        assertEquals("Game is already ended", exception.getMessage());
    }
}