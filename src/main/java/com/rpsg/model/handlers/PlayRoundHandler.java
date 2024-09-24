package com.rpsg.model.handlers;

import com.rpsg.model.GameEvent;
import com.rpsg.model.GameEventRepository;
import com.rpsg.model.GameStatus;
import com.rpsg.model.GameStatusRepository;
import com.rpsg.model.Move;
import com.rpsg.model.Winner;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class PlayRoundHandler {

    private final GameEventRepository gameEventRepository;
    private final GameMoveDecider gameMoveDecider;
    private final GameStatusRepository gameStatusRepository;

    public PlayRoundHandler(GameEventRepository gameEventRepository, GameMoveDecider gameMoveDecider, GameStatusRepository gameStatusRepository) {
        this.gameEventRepository = gameEventRepository;
        this.gameMoveDecider = gameMoveDecider;
        this.gameStatusRepository = gameStatusRepository;
    }

    public GameEvent.RoundPlayed handle(String gameId, Move playerMove) {
        if (gameStatusRepository.getStatus(gameId) == GameStatus.ENDED) {
            throw new IllegalStateException("Game is already ended");
        }
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