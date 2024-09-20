package com.rpsg.model;

import com.rpsg.infra.GameEventInMemoryRepository;
import com.rpsg.model.GameCommand.PlayRound;
import com.rpsg.model.GameCommand.StartGame;
import com.rpsg.model.GameEvent.GameStarted;
import com.rpsg.model.GameEvent.RoundPlayed;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static com.rpsg.model.Winner.DRAW;
import static com.rpsg.model.Winner.GAME;
import static com.rpsg.model.Winner.HUMAN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameBasicScenarioTest {

    private static final GameCommandHandler handler = new GameCommandHandler(new GameEventInMemoryRepository());
    private static GameStarted gameStarted;

    private static int humanWins = 0;
    private static int gameWins = 0;
    private static int draws = 0;

    static private <T extends GameEvent> T handleAndCastLastEvent(GameCommand command, Class<T> type) {
        var state = handler.handle(command);
        var lastEvent = state.events().getLast();
        assertTrue(type.isInstance(lastEvent));
        return type.cast(lastEvent);
    }

    private void recordAndAssert(Move playerMove, Move gameMove, Winner winner) {
        switch (gameMove) {
            case ROCK -> {
                draws = draws + 1;
                assertEquals(DRAW, winner);
            }
            case PAPER -> {
                gameWins = gameWins + 1;
                assertEquals(GAME, winner);
            }
            case SCISSORS -> {
                humanWins = humanWins + 1;
                assertEquals(HUMAN, winner);
            }
        }
        // System.out.println("Player move: " + playerMove + ", Game move: " + gameMove + ", Winner: " + winner);
    }

  @BeforeAll
  public static void startGame() {
        humanWins =0; gameWins = 0; draws = 0;
        // given
        var startGame = new StartGame("Player1");
        // when
        gameStarted = handleAndCastLastEvent(startGame, GameStarted.class);
        // then
        assertNotNull(gameStarted.gameId());
        assertEquals("Player1", gameStarted.player());
    }

    @Test
    @Order(1)
    public void playRound1() {
        // given
        var playRound = new PlayRound(gameStarted.gameId(), Move.ROCK);
        // when
        var event = handleAndCastLastEvent(playRound, RoundPlayed.class);
        // then
        assertEquals(Move.ROCK, event.playerMove());
        recordAndAssert(event.playerMove(), event.gameMove(), event.winner());
    }

    @Test
    @Order(2)
    public void playRound2() {
        // given
        var playRound = new PlayRound(gameStarted.gameId(), Move.ROCK);
        // when
        var event = handleAndCastLastEvent(playRound, RoundPlayed.class);
        // then
        assertEquals(Move.ROCK, event.playerMove());
        recordAndAssert(event.playerMove(), event.gameMove(), event.winner());
    }

    @Test
    @Order(3)
    public void endGame() {
        // given
        var endGame = new GameCommand.EndGame(gameStarted.gameId());
        // when
        var state = handler.handle(endGame);
        // then state
        assertEquals(state.events().size(), 4);
        assertNotNull(state.gameId());
        // then last event
        var event = (GameEvent.GameEnded) state.events().getLast();
        assertNotNull(event.gameId());
        // System.out.println("humanWins: " + humanWins + ", gameWins: " + gameWins + ", draws: " + draws);
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

}