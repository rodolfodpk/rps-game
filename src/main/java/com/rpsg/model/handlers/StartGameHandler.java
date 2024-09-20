package com.rpsg.model.handlers;

import com.rpsg.model.GameCommand;
import com.rpsg.model.GameEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StartGameHandler {

    public GameEvent.GameStarted handle(GameCommand.StartGame command) {
        var gameId = UUID.randomUUID().toString();
        return new GameEvent.GameStarted(gameId, command.player());
    }

}
