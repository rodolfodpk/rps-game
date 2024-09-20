package com.rpsg.model.handlers;

import com.rpsg.model.GameCommand;
import com.rpsg.model.GameEvent;
import com.rpsg.model.Move;
import com.rpsg.model.Winner;
import org.eclipse.collections.api.list.ImmutableList;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class PlayRoundHandler {

    private final Random random = new Random();

    public GameEvent.RoundPlayed handle(GameCommand.PlayRound p, ImmutableList<GameEvent> gameEvents) {
        var gameMove = determineGameMove();
        var winner = determineWinner(p.playerMove(), gameMove);
        return new GameEvent.RoundPlayed(p.gameId(), p.playerMove(), gameMove, winner);
    }

    private Move determineGameMove() {
        return Move.values()[random.nextInt(Move.values().length)];
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

}
