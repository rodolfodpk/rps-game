package com.rpsg.repository;

import com.hazelcast.map.EntryProcessor;
import com.rpsg.model.GameEvent;
import com.rpsg.model.GameState;
import com.rpsg.model.handlers.StartGameHandler;

import java.util.ArrayList;
import java.util.Map;

public class StartGameProcessor implements EntryProcessor<String, GameState, GameEvent.GameStarted> {

    private final String playerName;
    private final StartGameHandler startGameHandler;

    public StartGameProcessor(String playerName, StartGameHandler startGameHandler) {
        this.playerName = playerName;
        this.startGameHandler = startGameHandler;
    }

    @Override
    public GameEvent.GameStarted process(Map.Entry<String, GameState> entry) {
        var gameId = entry.getKey();
        var newEvent = startGameHandler.handle(gameId, playerName, null);
        var initialEvents = new ArrayList<GameEvent>();
        initialEvents.add(newEvent);
        var newGameState = new GameState(gameId, initialEvents);
        entry.setValue(newGameState);
        return newEvent;
    }

}