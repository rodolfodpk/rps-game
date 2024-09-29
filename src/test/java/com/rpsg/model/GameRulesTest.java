package com.rpsg.model;

import com.rpsg.model.handlers.PlayRoundHandler;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameRulesTest extends AbstractScenarioTest {

    private final PlayRoundHandler.GameMoveDecider gameMoveDecider = mock(PlayRoundHandler.GameMoveDecider.class);
    private final PlayRoundHandler playRoundHandler = new PlayRoundHandler(gameMoveDecider);

    @ParameterizedTest(name = "When human move is {0} and game move is {1} then the winner is {2}")
    @CsvSource({
            "PAPER, PAPER, DRAW",
            "PAPER, ROCK, HUMAN",
            "PAPER, SCISSORS, GAME",
            "ROCK, PAPER, GAME",
            "ROCK, ROCK, DRAW",
            "ROCK, SCISSORS, HUMAN",
            "SCISSORS, PAPER, HUMAN",
            "SCISSORS, ROCK, GAME",
            "SCISSORS, SCISSORS, DRAW"
    })
    void testMoveCombination(String humanMove, String gameMove, String expectedWinner) {
        // given
        events.clear();
        var gameID = UUID.randomUUID().toString();
        var playerName = "Player1";
        var gameStartedEvent = startGameHandler.handle(gameID, playerName);
        events.add(gameStartedEvent);
        // when
        var humanPlay = Move.valueOf(humanMove);
        when(gameMoveDecider.determineGameMove()).thenReturn(Move.valueOf(gameMove));
        var gamePlayed = playRoundHandler.handle(gameID, humanPlay, GameStatus.STARTED);
        events.add(gamePlayed);
        var state = new GameState(gameID, GameStatus.STARTED, events);
        var endState = endGameHandler.handle(state);
        var endGameEvent = endState.events().getLast();
        // then game started
        assertNotNull(gameStartedEvent.gameId());
        // then play event
        if (Objects.requireNonNull(gamePlayed) instanceof GameEvent.RoundPlayed roundPlayed) {
            assertEquals(Move.valueOf(humanMove), roundPlayed.playerMove());
            assertEquals(Move.valueOf(gameMove), roundPlayed.gameMove());
            assertEquals(Winner.valueOf(expectedWinner), roundPlayed.winner());
        } else {
            throw new IllegalStateException("Unexpected value: " + gamePlayed);
        }
        // then end game event
        assertEquals(Winner.valueOf(expectedWinner), ((GameEvent.GameEnded) endGameEvent).winner());
    }

}