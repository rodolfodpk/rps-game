package com.rpsg.model.handlers;

import com.rpsg.model.GameEvent;
import com.rpsg.model.GameEventRepository;
import com.rpsg.model.GameStatus;
import com.rpsg.model.GameStatusRepository;
import org.springframework.stereotype.Component;

@Component
public class StartGameHandler {

    private final GameEventRepository gameEventRepository;
    private final GameStatusRepository gameStatusRepository;

    public StartGameHandler(GameEventRepository gameEventRepository, GameStatusRepository gameStatusRepository) {
        this.gameEventRepository = gameEventRepository;
        this.gameStatusRepository = gameStatusRepository;
    }

    public GameEvent.GameStarted handle(String gameId, String playerName) {
        var status = gameStatusRepository.getStatus(gameId);
        if (status != null) {
            throw new IllegalStateException("Game is already started or ended");
        }
        var newEvent = new GameEvent.GameStarted(gameId, playerName);
        gameEventRepository.appendEvent(gameId, newEvent);
        gameStatusRepository.setStatus(gameId, GameStatus.STARTED);
        return newEvent;
    }

}
