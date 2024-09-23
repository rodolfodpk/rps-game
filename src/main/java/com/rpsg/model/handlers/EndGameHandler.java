package com.rpsg.model.handlers;

import com.rpsg.model.GameEvent;
import com.rpsg.model.GameEventRepository;
import com.rpsg.model.GameState;
import org.springframework.stereotype.Component;

@Component
public class EndGameHandler {

    private final GameEventRepository gameEventRepository;

    public EndGameHandler(GameEventRepository gameEventRepository) {
        this.gameEventRepository = gameEventRepository;
    }

    public GameState handle(String gameId) {
        var currentEvents = gameEventRepository.findAll(gameId);
        var winner = GameState.determineWinner(currentEvents);
        var newEvent = new GameEvent.GameEnded(gameId, winner);
        var newEvents = gameEventRepository.appendEvent(gameId, newEvent);
        return new GameState(newEvent.gameId(), newEvents);
    }

}
