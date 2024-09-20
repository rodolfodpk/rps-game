package com.rpsg.model.handlers;

import com.rpsg.model.GameCommand;
import com.rpsg.model.GameEvent;
import com.rpsg.model.GameEventRepository;
import com.rpsg.model.GameState;
import com.rpsg.model.Move;
import com.rpsg.model.Winner;
import org.springframework.stereotype.Component;

import static com.rpsg.model.handlers.AbstractCommandHandler.appendElement;

@Component
public class PlayRoundHandler implements AbstractCommandHandler<GameCommand.PlayRound> {

    private final GameEventRepository gameEventRepository;
    private final GameMoveDecider gameMoveDecider;

    public PlayRoundHandler(GameEventRepository gameEventRepository, GameMoveDecider gameMoveDecider) {
        this.gameEventRepository = gameEventRepository;
        this.gameMoveDecider = gameMoveDecider;
    }

    public GameState handle(GameCommand.PlayRound command) {
        var currentEvents = gameEventRepository.findAll(command.gameId());
        var gameMove = gameMoveDecider.determineGameMove();
        var winner = determineWinner(command.playerMove(), gameMove);
        var newEvent = new GameEvent.RoundPlayed(command.gameId(), command.playerMove(), gameMove, winner);
        gameEventRepository.appendEvent(command.gameId(), newEvent);
        var newEvents = appendElement(currentEvents.castToList(), newEvent);
        return new GameState(newEvent.gameId(), newEvents);
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