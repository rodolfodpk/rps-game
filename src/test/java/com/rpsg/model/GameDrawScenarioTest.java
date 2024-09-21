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

import static com.rpsg.model.Winner.DRAW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameDrawScenarioTest implements AbstractScenarioTest {

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
        assertEquals(1, initialState.events().size());
        assertNotNull(initialState.gameId());
        var latestEvent = (GameEvent.GameStarted) initialState.events().getFirst();
        assertEquals("Player1", latestEvent.player());
        assertNotNull(initialState.gameId());
    }

    @Test
    @Order(1)
    public void playRound1Rock() {
        // given
        var playRound = new PlayRound(initialState.gameId(), Move.ROCK);
        when(gameMoveDecider.determineGameMove()).thenReturn(Move.ROCK);
        // when
        var newState = playRoundHandler.handle(playRound);
        // then
        assertEquals(2, newState.events().size());
        assertEquals(initialState.gameId(), newState.gameId());
        var latestEvent = (GameEvent.RoundPlayed) newState.events().getLast();
        assertEquals(Move.ROCK, latestEvent.playerMove());
        assertEquals(Move.ROCK, latestEvent.gameMove());
        assertEquals(DRAW, latestEvent.winner());
    }

    @Test
    @Order(2)
    public void playRound2Paper() {
        // given
        var playRound = new PlayRound(initialState.gameId(), Move.PAPER);
        when(gameMoveDecider.determineGameMove()).thenReturn(Move.PAPER);
        // when
        var newState = playRoundHandler.handle(playRound);
        // then
        assertEquals(3, newState.events().size());
        assertEquals(initialState.gameId(), newState.gameId());
        var latestEvent = (GameEvent.RoundPlayed) newState.events().getLast();
        assertEquals(Move.PAPER, latestEvent.playerMove());
        assertEquals(Move.PAPER, latestEvent.gameMove());
        assertEquals(DRAW, latestEvent.winner());
    }

    @Test
    @Order(3)
    public void endGameShouldBeDRAW() {
        // given
        var endGame = new GameCommand.EndGame(initialState.gameId());
        // when
        var newState = endGameHandler.handle(endGame);
        // then state
        assertEquals(4, newState.events().size());
        assertNotNull(newState.gameId());
        assertEquals(initialState.gameId(), newState.gameId());
        // then last event
        var latestEvent = (GameEvent.GameEnded) newState.events().getLast();
        assertNotNull(latestEvent.gameId());
        assertEquals(DRAW, latestEvent.winner());
    }

}