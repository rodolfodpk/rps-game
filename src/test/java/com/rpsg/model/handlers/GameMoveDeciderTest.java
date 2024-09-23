package com.rpsg.model.handlers;

import com.rpsg.model.Move;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameMoveDeciderTest {

    private final PlayRoundHandler.GameMoveDecider gameMoveDecider = new PlayRoundHandler.GameMoveDecider();

    @Test
    void determineGameMove_ReturnsRock() {
        for (int i = 0; i < 100; i++) {
            Move move = gameMoveDecider.determineGameMove();
            assertTrue(move == Move.ROCK || move == Move.PAPER || move == Move.SCISSORS);
        }
    }
}