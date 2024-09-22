package com.rpsg.model.handlers;

import com.rpsg.model.GameCommand;
import com.rpsg.model.GameEvent;
import com.rpsg.model.GameEventRepository;
import org.springframework.stereotype.Component;

@Component
public class StartGameHandler {

    private final GameEventRepository gameEventRepository;

    public StartGameHandler(GameEventRepository gameEventRepository) {
        this.gameEventRepository = gameEventRepository;
    }

    public GameEvent.GameStarted handle(String gameId, GameCommand.StartGame command) {
        var newEvent = new GameEvent.GameStarted(gameId, command.player());
        gameEventRepository.appendEvent(gameId, newEvent);
        return newEvent;
    }

}
