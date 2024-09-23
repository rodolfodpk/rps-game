package com.rpsg.model;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record GameState(String gameId, List<? extends GameEvent> events) {

    public static Winner determineWinner(List<? extends GameEvent> events) {
        var nonDrawEvents = events.stream()
                .filter(event -> event instanceof GameEvent.RoundPlayed)
                .map(event -> ((GameEvent.RoundPlayed) event).winner())
                .filter(winner -> winner != Winner.DRAW).toList();
        var occurrences = nonDrawEvents.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        var maxOccurrence = occurrences.values().stream()
                .max(Long::compareTo)
                .orElse(0L);
        var topWinners = occurrences.entrySet().stream()
                .filter(e -> e.getValue().equals(maxOccurrence))
                .map(Map.Entry::getKey)
                .toList();
        if (topWinners.size() != 1) {
            return Winner.DRAW;
        } else {
            return topWinners.getFirst();
        }
    }
}
