package com.rpsg.repository;

import com.hazelcast.map.EntryProcessor;
import com.rpsg.model.GameEvent;
import com.rpsg.model.GameState;
import com.rpsg.model.GameStatus;
import com.rpsg.model.handlers.StartGameHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;

public class StartGameProcessor implements EntryProcessor<String, GameState, GameEvent.GameStarted> {

    private static Logger logger = LoggerFactory.getLogger(StartGameProcessor.class);

    private final String playerName;
    private final StartGameHandler startGameHandler;

    public StartGameProcessor(String playerName, StartGameHandler startGameHandler) {
        this.playerName = playerName;
        this.startGameHandler = startGameHandler;
    }

    @Override
    public GameEvent.GameStarted process(Map.Entry<String, GameState> entry) {
        var gameId = entry.getKey();
        var state = entry.getValue();
        if (state != null) {
            throw new IllegalStateException("Game already started or ended");
        }
        logger.debug("start.gameId: {}", gameId);
        var newEvent = startGameHandler.handle(gameId, playerName);
        var initialEvents = new ArrayList<GameEvent>();
        initialEvents.add(newEvent);
        var newGameState = new GameState(gameId, GameStatus.STARTED, initialEvents);
        entry.setValue(newGameState);
        return newEvent;
    }

}