package com.rpsg.model;

import com.rpsg.model.GameCommand.PlayRound;
import com.rpsg.model.GameCommand.StartGame;
import com.rpsg.model.handlers.GameMoveDecider;
import com.rpsg.model.handlers.PlayRoundHandler;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static com.rpsg.model.Winner.DRAW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameDrawScenarioTest extends AbstractScenarioTest {

    private final GameMoveDecider gameMoveDecider = mock(GameMoveDecider.class);
    private final PlayRoundHandler playRoundHandler = new PlayRoundHandler(gameEventRepository, gameMoveDecider);

    @Test
    @Order(1)
    public void startGame() {
        // given
        var startGame = new StartGame("Player1");
        // when
        var event = startGameHandler.handle(gameID, startGame);
        // then
        assertNotNull(event.gameId());
        assertEquals(startGame.player(), event.player());
    }

    @Test
    @Order(1)
    public void playRound1Rock() {
        // given
        var playRound = new PlayRound(Move.ROCK);
        when(gameMoveDecider.determineGameMove()).thenReturn(Move.ROCK);
        // when
        var latestEvent = playRoundHandler.handle(gameID, playRound);
        // then event
        assertEquals(Move.ROCK, latestEvent.playerMove());
        assertEquals(Move.ROCK, latestEvent.gameMove());
        assertEquals(DRAW, latestEvent.winner());
    }

    @Test
    @Order(2)
    public void playRound2Paper() {
        // given
        var playRound = new PlayRound(Move.PAPER);
        when(gameMoveDecider.determineGameMove()).thenReturn(Move.PAPER);
        // when
        var latestEvent = playRoundHandler.handle(gameID, playRound);
        // then event
        assertEquals(Move.PAPER, latestEvent.playerMove());
        assertEquals(Move.PAPER, latestEvent.gameMove());
        assertEquals(DRAW, latestEvent.winner());
    }

    @Test
    @Order(3)
    public void endGameShouldBeDRAW() {
        // given
        var endGame = new GameCommand.EndGame();
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