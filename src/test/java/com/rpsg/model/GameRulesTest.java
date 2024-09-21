package com.rpsg.model;

import com.rpsg.model.GameCommand.PlayRound;
import com.rpsg.model.GameCommand.StartGame;
import com.rpsg.model.handlers.GameMoveDecider;
import com.rpsg.model.handlers.PlayRoundHandler;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameRulesTest extends AbstractScenarioTest {

    private final GameMoveDecider gameMoveDecider = mock(GameMoveDecider.class);
    private final PlayRoundHandler playRoundHandler = new PlayRoundHandler(gameEventRepository, gameMoveDecider);

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
        var gameID = UUID.randomUUID().toString();
        var startGame = new StartGame("Player1");
        var initialState = startGameHandler.handle(gameID, startGame);
        var gameStartedEvent = (GameEvent.GameStarted) initialState.events().getFirst();
        // when
        var humanPlay = new PlayRound(Move.valueOf(humanMove));
        when(gameMoveDecider.determineGameMove()).thenReturn(Move.valueOf(gameMove));
        var newState = playRoundHandler.handle(gameID, humanPlay);
        var playEvent = newState.events().getLast();
        var endState = endGameHandler.handle(gameID, new GameCommand.EndGame());
        var endGameEvent = endState.events().getLast();
        // then play event
        if (Objects.requireNonNull(playEvent) instanceof GameEvent.RoundPlayed roundPlayed) {
            assertEquals(Move.valueOf(humanMove), roundPlayed.playerMove());
            assertEquals(Move.valueOf(gameMove), roundPlayed.gameMove());
            assertEquals(Winner.valueOf(expectedWinner), roundPlayed.winner());
        } else {
            throw new IllegalStateException("Unexpected value: " + playEvent);
        }
        // then end game event
        assertEquals(Winner.valueOf(expectedWinner), ((GameEvent.GameEnded) endGameEvent).winner());
    }

}