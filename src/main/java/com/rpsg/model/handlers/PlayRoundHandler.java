package com.rpsg.model.handlers;

import com.rpsg.model.GameEvent;
import com.rpsg.model.GameStatus;
import com.rpsg.model.Move;
import com.rpsg.model.Winner;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class PlayRoundHandler {

    private final GameMoveDecider gameMoveDecider;

    public PlayRoundHandler(GameMoveDecider gameMoveDecider) {
        this.gameMoveDecider = gameMoveDecider;
    }

    public GameEvent.RoundPlayed handle(String gameId, Move playerMove, GameStatus gameStatus) {
        if (gameStatus == GameStatus.ENDED) {
            throw new IllegalStateException("Game is already ended");
        }
        var gameMove = gameMoveDecider.determineGameMove();
        var winner = determineWinner(playerMove, gameMove);
        return new GameEvent.RoundPlayed(gameId, playerMove, gameMove, winner);
    }

    private Winner determineWinner(Move humanMove, Move gameMove) {
        if (humanMove == gameMove) {
            return Winner.DRAW;
        }
        return switch (humanMove) {
            case ROCK -> gameMove == Move.SCISSORS ? Winner.HUMAN : Winner.GAME;
            case PAPER -> gameMove == Move.ROCK ? Winner.HUMAN : Winner.GAME;
            case SCISSORS -> gameMove == Move.PAPER ? Winner.HUMAN : Winner.GAME;
        };
    }

    @Component
    public static class GameMoveDecider {

        private final Random random = new Random();

        public Move determineGameMove() {
            return Move.values()[random.nextInt(Move.values().length)];
        }

    }
}