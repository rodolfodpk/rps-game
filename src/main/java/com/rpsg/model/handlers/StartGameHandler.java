package com.rpsg.model.handlers;

import com.rpsg.model.GameEvent;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class StartGameHandler implements Serializable {

    public GameEvent.GameStarted handle(String gameId, String playerName) {
        return new GameEvent.GameStarted(gameId, playerName);
    }

}
