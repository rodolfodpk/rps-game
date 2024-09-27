package com.rpsg.repository;

import com.hazelcast.map.EntryProcessor;
import com.rpsg.model.GameEvent;
import com.rpsg.model.GameRepresentation;
import com.rpsg.model.GameState;
import com.rpsg.model.GameStats;
import com.rpsg.model.Move;
import com.rpsg.model.Winner;
import com.rpsg.model.handlers.EndGameHandler;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EndGameProcessor implements EntryProcessor<String, GameState, GameRepresentation> {

    private final EndGameHandler endGameHandler;

    public EndGameProcessor(EndGameHandler endGameHandler) {
        this.endGameHandler = endGameHandler;
    }

    @Override
    public GameRepresentation process(Map.Entry<String, GameState> entry) {
        if (entry.getValue() == null) {
            throw new IllegalArgumentException("Game not found");
        }
        var state = entry.getValue();
        var newState = endGameHandler.handle(state);
        return new GameRepresentationMapper().map(newState);
    }

    @Component
    public static class GameRepresentationMapper {

        public GameRepresentation map(GameState gameState) {
            var gameId = gameState.gameId();
            var gameEvents = gameState.events();
            var playerId = ((GameEvent.GameStarted) gameEvents.getFirst()).player();
            var winner = ((GameEvent.GameEnded) gameEvents.getLast()).winner();
            var stats = new GameStats(moves(gameEvents), winners(gameEvents));
            return new GameRepresentation(gameId, playerId, winner, stats);
        }

        private Map<String, Map<Move, Long>> moves(List<GameEvent> gameEvents) {
            return gameEvents.stream()
                    .filter(e -> e instanceof GameEvent.RoundPlayed)
                    .flatMap(e -> Stream.of(
                            new AbstractMap.SimpleEntry<>("Player", ((GameEvent.RoundPlayed) e).playerMove()),
                            new AbstractMap.SimpleEntry<>("Game", ((GameEvent.RoundPlayed) e).gameMove())
                    ))
                    .collect(Collectors.groupingBy(
                            Map.Entry::getKey,
                            Collectors.groupingBy(Map.Entry::getValue, Collectors.counting())
                    ));
        }

        private Map<Winner, Long> winners(List<GameEvent> gameEvents) {
            return gameEvents.stream()
                    .filter(GameEvent.RoundPlayed.class::isInstance)
                    .map(gameEvent -> ((GameEvent.RoundPlayed) gameEvent).winner())
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        }
    }
}