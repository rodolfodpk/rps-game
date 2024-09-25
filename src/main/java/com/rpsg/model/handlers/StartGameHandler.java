package com.rpsg.model.handlers;

import com.rpsg.model.GameEvent;
import com.rpsg.model.GameStatus;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class StartGameHandler implements Serializable {

    public GameEvent.GameStarted handle(String gameId, String playerName, GameStatus status) {
        if (status != null) {
            throw new IllegalStateException("Game is already started or ended");
        }
        return new GameEvent.GameStarted(gameId, playerName);
    }

}
