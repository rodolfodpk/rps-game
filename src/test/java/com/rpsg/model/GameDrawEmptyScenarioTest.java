package com.rpsg.model;

import com.rpsg.infra.GameEventInMemoryRepository;
import com.rpsg.model.GameCommand.StartGame;
import com.rpsg.model.handlers.EndGameHandler;
import com.rpsg.model.handlers.StartGameHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.rpsg.model.Winner.DRAW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GameDrawEmptyScenarioTest {

    private static final GameEventRepository gameEventRepository = new GameEventInMemoryRepository();
    private static final StartGameHandler startGameHandler = new StartGameHandler(gameEventRepository);
    private static final EndGameHandler endGameHandler = new EndGameHandler(gameEventRepository);
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
    public void endGameShouldBeDRAW() {
        // given
        var endGame = new GameCommand.EndGame(initialState.gameId());
        // when
        var newState = endGameHandler.handle(endGame);
        // then state
        assertEquals(2, newState.events().size());
        assertNotNull(newState.gameId());
        // then last event
        assertEquals(initialState.gameId(), newState.gameId());
        var latestEvent = (GameEvent.GameEnded) newState.events().getLast();
        assertNotNull(latestEvent.gameId());
        assertEquals(DRAW, latestEvent.winner());
    }

}