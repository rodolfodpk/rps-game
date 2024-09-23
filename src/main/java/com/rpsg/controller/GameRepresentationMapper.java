package com.rpsg.controller;

import com.rpsg.model.GameEvent;
import com.rpsg.model.GameState;
import com.rpsg.model.Move;
import com.rpsg.model.Winner;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class GameRepresentationMapper {

    public GameRepresentation map(GameState gameState) {
        var gameId = gameState.gameId();
        var gameEvents = gameState.events();
        String playerId = ((GameEvent.GameStarted) gameEvents.getFirst()).player();
        Winner winner = ((GameEvent.GameEnded) gameEvents.getLast()).winner();
        GameStats stats = new GameStats(moves(gameEvents), winners(gameEvents));
        return new GameRepresentation(gameId, playerId, winner, stats);
    }

    private Map<String, Map<Move, Long>> moves(List<? extends GameEvent> gameEvents) {
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

    private Map<Winner, Long> winners(List<? extends GameEvent> gameEvents) {
        return gameEvents.stream()
                .filter(GameEvent.RoundPlayed.class::isInstance)
                .map(gameEvent -> ((GameEvent.RoundPlayed) gameEvent).winner())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }
}
