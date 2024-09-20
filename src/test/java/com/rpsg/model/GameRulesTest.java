package com.rpsg.model;

import com.rpsg.infra.GameEventInMemoryRepository;
import com.rpsg.model.GameCommand.PlayRound;
import com.rpsg.model.GameCommand.StartGame;
import com.rpsg.model.handlers.EndGameHandler;
import com.rpsg.model.handlers.GameMoveDecider;
import com.rpsg.model.handlers.PlayRoundHandler;
import com.rpsg.model.handlers.StartGameHandler;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameRulesTest {

    private static final GameEventRepository gameEventRepository = new GameEventInMemoryRepository();
    private static final StartGameHandler startGameHandler = new StartGameHandler(gameEventRepository);
    private static final GameMoveDecider gameMoveDecider = mock(GameMoveDecider.class);
    private static final PlayRoundHandler playRoundHandler = new PlayRoundHandler(gameEventRepository, gameMoveDecider);
    private static final EndGameHandler endGameHandler = new EndGameHandler(gameEventRepository);

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
        var startGame = new StartGame("Player1");
        var initialState = startGameHandler.handle(startGame);
        var gameStartedEvent = (GameEvent.GameStarted) initialState.events().getFirst();
        // when
        var humanPlay = new PlayRound(gameStartedEvent.gameId(), Move.valueOf(humanMove));
        when(gameMoveDecider.determineGameMove()).thenReturn(Move.valueOf(gameMove));
        var newState = playRoundHandler.handle(humanPlay);
        var playEvent = newState.events().getLast();
        var endState = endGameHandler.handle(new GameCommand.EndGame((initialState.gameId())));
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