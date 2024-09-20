package com.rpsg.model;

import com.rpsg.infra.GameEventInMemoryRepository;
import com.rpsg.model.GameCommand.PlayRound;
import com.rpsg.model.GameCommand.StartGame;
import com.rpsg.model.handlers.EndGameHandler;
import com.rpsg.model.handlers.PlayRoundHandler;
import com.rpsg.model.handlers.StartGameHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static com.rpsg.model.Winner.DRAW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameBasicScenarioTest {

    private static final GameEventRepository gameEventRepository = new GameEventInMemoryRepository();
    private static final StartGameHandler startGameHandler = new StartGameHandler(gameEventRepository);
    private static final PlayRoundHandler playRoundHandler = new PlayRoundHandler(gameEventRepository);
    private static final EndGameHandler endGameHandler = new EndGameHandler(gameEventRepository);

    private static GameState initialState;

    private static int humanWins = 0;
    private static int gameWins = 0;
    private static int draws = 0;

    @BeforeAll
    public static void startGame() {
        humanWins = 0;
        gameWins = 0;
        draws = 0;
        // given
        var startGame = new StartGame("Player1");
        // when
        initialState = startGameHandler.handle(startGame);
        // then
        assertNotNull(initialState.gameId());
        var expectedEvent = (GameEvent.GameStarted) initialState.events().getFirst();
        assertEquals("Player1", expectedEvent.player());
        assertNotNull(initialState.gameId());
    }

    @Test
    @Order(1)
    public void playRound1() {
        // given
        var playRound = new PlayRound(initialState.gameId(), Move.ROCK);
        // when
        var newState = playRoundHandler.handle(playRound);
        // then
        assertEquals(initialState.gameId(), newState.gameId());
        var expectedEvent = (GameEvent.RoundPlayed) newState.events().getLast();
        assertEquals(Move.ROCK, expectedEvent.playerMove());
        recordWinner(expectedEvent.winner());
    }

    @Test
    @Order(2)
    public void playRound2() {
        // given
        var playRound = new PlayRound(initialState.gameId(), Move.ROCK);
        // when
        var newState = playRoundHandler.handle(playRound);
        // then
        assertEquals(initialState.gameId(), newState.gameId());
        var expectedEvent = (GameEvent.RoundPlayed) newState.events().getLast();
        assertEquals(Move.ROCK, expectedEvent.playerMove());
        recordWinner(expectedEvent.winner());
    }

    @Test
    @Order(3)
    public void endGame() {
        // given
        var endGame = new GameCommand.EndGame(initialState.gameId());
        // when
        var newState = endGameHandler.handle(endGame);
        // then state
        assertEquals(4, newState.events().size());
        assertNotNull(newState.gameId());
        // then last event
        assertEquals(initialState.gameId(), newState.gameId());
        var event = (GameEvent.GameEnded) newState.events().getLast();
        assertNotNull(event.gameId());
        if (humanWins == gameWins) {
            assertEquals(DRAW, event.winner());
            return;
        }
        Winner expectedWinner;
        if (humanWins > gameWins) {
            expectedWinner = Winner.HUMAN;
        } else {
            expectedWinner = Winner.GAME;
        }
        assertEquals(expectedWinner, event.winner());
    }

    private void recordWinner(Winner winner) {
        switch (winner) {
            case DRAW -> draws = draws + 1;
            case GAME -> gameWins = gameWins + 1;
            case HUMAN -> humanWins = humanWins + 1;
        }
    }


}