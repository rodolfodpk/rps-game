package com.rpsg.repository;

import com.hazelcast.map.EntryProcessor;
import com.rpsg.model.GameEvent;
import com.rpsg.model.GameState;
import com.rpsg.model.GameStatus;
import com.rpsg.model.Move;
import com.rpsg.model.handlers.PlayRoundHandler;

import java.io.Serializable;
import java.util.Map;

public class PlayGameProcessor implements Serializable, EntryProcessor<String, GameState, GameEvent.RoundPlayed> {

    private final PlayRoundHandler playRoundHandler;
    private final Move playerMove;

    public PlayGameProcessor(PlayRoundHandler playRoundHandler, Move playerMove) {
        this.playRoundHandler = playRoundHandler;
        this.playerMove = playerMove;
    }

    @Override
    public GameEvent.RoundPlayed process(Map.Entry<String, GameState> entry) {
        if (entry.getValue() == null) {
            throw new IllegalArgumentException("Game not found");
        }
        var gameId = entry.getKey();
        var state = entry.getValue();
        var newEvent = playRoundHandler.handle(gameId, playerMove, GameStatus.STARTED); // TODO get status
        state.events().add(newEvent);
        entry.setValue(state);
        return newEvent;
    }

}