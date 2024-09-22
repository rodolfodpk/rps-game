package com.rpsg.model.handlers;

import com.rpsg.model.GameEvent;
import com.rpsg.model.GameEventRepository;
import com.rpsg.model.GameState;
import com.rpsg.model.Winner;
import org.eclipse.collections.impl.factory.Bags;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class EndGameHandler {

    private final GameEventRepository gameEventRepository;

    public EndGameHandler(GameEventRepository gameEventRepository) {
        this.gameEventRepository = gameEventRepository;
    }

    private static <E extends GameEvent> List<E> appendElement(List<E> original, E newElement) {
        return Stream.concat(original.stream(), Stream.of(newElement))
                .collect(Collectors.toList());
    }

    public GameState handle(String gameId) {
        var currentEvents = gameEventRepository.findAll(gameId);
        // filter RoundPlayed instances
        var roundPlayedEvents = currentEvents.selectInstancesOf(GameEvent.RoundPlayed.class)
                .collect(GameEvent.RoundPlayed::winner);
        var nonDrawEvents = roundPlayedEvents
                .select(w -> !w.equals(Winner.DRAW));
        var bag = Bags.mutable.withAll(nonDrawEvents).toImmutableBag();
        var topWinners = bag.topOccurrences(1);
        Winner winner;
        if (topWinners.size() != 1) {
            winner = Winner.DRAW;
        } else {
            winner = topWinners.get(0).getOne();
        }
        var newEvent = new GameEvent.GameEnded(gameId, winner);
        gameEventRepository.appendEvent(gameId, newEvent);
        var newEvents = appendElement(currentEvents.castToList(), newEvent);
        return new GameState(newEvent.gameId(), newEvents);
    }

}
