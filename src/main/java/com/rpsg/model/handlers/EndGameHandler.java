package com.rpsg.model.handlers;

import com.rpsg.model.GameCommand;
import com.rpsg.model.GameEvent;
import com.rpsg.model.GameEventRepository;
import com.rpsg.model.GameState;
import com.rpsg.model.Winner;
import org.eclipse.collections.impl.factory.Bags;
import org.springframework.stereotype.Component;

import static com.rpsg.model.handlers.AbstractCommandHandler.appendElement;

@Component
public class EndGameHandler implements AbstractCommandHandler<GameCommand.EndGame> {

    private final GameEventRepository gameEventRepository;

    public EndGameHandler(GameEventRepository gameEventRepository) {
        this.gameEventRepository = gameEventRepository;
    }

    public GameState handle(GameCommand.EndGame command) {
        var currentEvents = gameEventRepository.findAll(command.gameId());
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
        var newEvent = new GameEvent.GameEnded(command.gameId(), winner);
        gameEventRepository.appendEvent(command.gameId(), newEvent);
        var newEvents = appendElement(currentEvents.castToList(), newEvent);
        return new GameState(newEvent.gameId(), newEvents);
    }

}
