package com.rpsg.model.handlers;

import com.rpsg.model.GameEvent;
import com.rpsg.model.GameEventRepository;
import com.rpsg.model.Move;
import com.rpsg.model.Winner;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class PlayRoundHandler {

    private final GameEventRepository gameEventRepository;
    private final GameMoveDecider gameMoveDecider;

    public PlayRoundHandler(GameEventRepository gameEventRepository, GameMoveDecider gameMoveDecider) {
        this.gameEventRepository = gameEventRepository;
        this.gameMoveDecider = gameMoveDecider;
    }

    public GameEvent.RoundPlayed handle(String gameId, Move playerMove) {
        var gameMove = gameMoveDecider.determineGameMove();
        var winner = determineWinner(playerMove, gameMove);
        var newEvent = new GameEvent.RoundPlayed(gameId, playerMove, gameMove, winner);
        gameEventRepository.appendEvent(gameId, newEvent);
        return newEvent;
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