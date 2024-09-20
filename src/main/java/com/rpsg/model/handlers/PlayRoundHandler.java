package com.rpsg.model.handlers;

import com.rpsg.model.GameCommand;
import com.rpsg.model.GameEvent;
import com.rpsg.model.GameEventRepository;
import com.rpsg.model.GameState;
import com.rpsg.model.Move;
import com.rpsg.model.Winner;
import org.springframework.stereotype.Component;

import java.util.Random;

import static com.rpsg.model.handlers.AbstractCommandHandler.appendElement;

@Component
public class PlayRoundHandler implements AbstractCommandHandler<GameCommand.PlayRound> {

    private final Random random = new Random();
    private final GameEventRepository gameEventRepository;

    public PlayRoundHandler(GameEventRepository gameEventRepository) {
        this.gameEventRepository = gameEventRepository;
    }

    public GameState handle(GameCommand.PlayRound command) {
        var currentEvents = gameEventRepository.findAll(command.gameId());
        var gameMove = determineGameMove();
        var winner = determineWinner(command.playerMove(), gameMove);
        var newEvent = new GameEvent.RoundPlayed(command.gameId(), command.playerMove(), gameMove, winner);
        gameEventRepository.appendEvent(command.gameId(), newEvent);
        var newEvents = appendElement(currentEvents.castToList(), newEvent);
        return new GameState(newEvent.gameId(), newEvents);
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
