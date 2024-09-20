package com.rpsg.model.handlers;

import com.rpsg.model.GameCommand;
import com.rpsg.model.GameEvent;
import com.rpsg.model.Winner;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Bags;
import org.springframework.stereotype.Component;


@Component
public class EndGameHandler {

    public GameEvent.GameEnded handle(GameCommand.EndGame e, ImmutableList<GameEvent> gameEvents) {
        // System.out.println("End ---");
        // filter RoundPlayed instances
        var roundPlayedEvents = gameEvents.selectInstancesOf(GameEvent.RoundPlayed.class)
                .collect(GameEvent.RoundPlayed::winner);
        // System.out.println("    events: " + roundPlayedEvents);
        var nonDrawEvents = roundPlayedEvents
                .select(w -> !w.equals(Winner.DRAW));
        var bag = Bags.mutable.withAll(nonDrawEvents).toImmutableBag();
        var topWinners = bag.topOccurrences(1);
        // System.out.println("   topWinners: " + topWinners);
        Winner winner;
        if (topWinners.isEmpty() ||
                (topWinners.size() == 2 && topWinners.get(0).getTwo() == topWinners.get(1).getTwo())) {
            winner = Winner.DRAW;
        } else {
            winner = topWinners.get(0).getOne();
        }
        return new GameEvent.GameEnded(e.gameId(), winner);
    }

}
