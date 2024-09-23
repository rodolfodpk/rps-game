package com.rpsg.model.handlers;

import com.rpsg.model.GameEvent;
import com.rpsg.model.GameEventRepository;
import com.rpsg.model.GameState;
import com.rpsg.model.Winner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class EndGameHandler {

    private final GameEventRepository gameEventRepository;

    public EndGameHandler(GameEventRepository gameEventRepository) {
        this.gameEventRepository = gameEventRepository;
    }

    public GameState handle(String gameId) {
        var currentEvents = gameEventRepository.findAll(gameId);
        var winner = determineWinner(currentEvents);
        var newEvent = new GameEvent.GameEnded(gameId, winner);
        var newEvents = gameEventRepository.appendEvent(gameId, newEvent);
        return new GameState(newEvent.gameId(), newEvents);
    }

    private Winner determineWinner(List<? extends GameEvent> events) {
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
