package com.rpsg.model.handlers;

import com.rpsg.model.GameCommand;
import com.rpsg.model.GameEvent;
import com.rpsg.model.GameEventRepository;
import com.rpsg.model.GameState;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StartGameHandler implements AbstractCommandHandler<GameCommand.StartGame> {

    private final GameEventRepository gameEventRepository;

    public StartGameHandler(GameEventRepository gameEventRepository) {
        this.gameEventRepository = gameEventRepository;
    }

    public GameState handle(String gameId, GameCommand.StartGame command) {
        var newEvent = new GameEvent.GameStarted(gameId, command.player());
        gameEventRepository.appendEvent(gameId, newEvent);
        return new GameState(gameId, List.of(newEvent));
    }

}
