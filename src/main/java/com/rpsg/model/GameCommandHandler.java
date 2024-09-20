package com.rpsg.model;

import com.rpsg.model.GameCommand.EndGame;
import com.rpsg.model.GameCommand.PlayRound;
import com.rpsg.model.GameCommand.StartGame;
import com.rpsg.model.GameEvent.GameEnded;
import com.rpsg.model.GameEvent.GameStarted;
import com.rpsg.model.GameEvent.RoundPlayed;
import org.eclipse.collections.impl.factory.Bags;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
public class GameCommandHandler {

    private final Random random = new Random();

    private final GameEventRepository gameEventRepository;

    public GameCommandHandler(GameEventRepository gameEventRepository) {
        this.gameEventRepository = gameEventRepository;
    }

    public GameState handle(GameCommand command) {
        String gameId;
        var events = switch (command) {
            case StartGame s -> {
                gameId = UUID.randomUUID().toString();
                yield List.of(new GameStarted(gameId, s.player()));
            }
            case PlayRound p -> {
                gameId = p.gameId();
                var gameEvents = gameEventRepository.findAll(gameId).toList();
                var gameMove = determineGameMove(p.playerMove());
                var winner = determineWinner(p.playerMove(), gameMove);
                gameEvents.add(new RoundPlayed(p.gameId(), p.playerMove(), gameMove, winner));
                yield gameEvents;
            }
            case EndGame e -> {
                // System.out.println("End ---");
                gameId = e.gameId();
                var gameEvents = gameEventRepository.findAll(gameId).toList();
                // filter RoundPlayed instances
                var roundPlayedEvents = gameEvents.selectInstancesOf(RoundPlayed.class)
                        .collect(RoundPlayed::winner);
                // System.out.println("    events: " + roundPlayedEvents);
                var nonDrawEvents = roundPlayedEvents
                        .select(w -> !w.equals(Winner.DRAW));
                var bag = Bags.mutable.withAll(nonDrawEvents).toImmutableBag();
                var topWinners = bag.topOccurrences(1);
                // System.out.println("   topWinners: " + topWinners);
                Winner winner ;
                if (topWinners.isEmpty() ||
                        (topWinners.size() == 2 && topWinners.get(0).getTwo() == topWinners.get(1).getTwo())) {
                    winner = Winner.DRAW;
                } else {
                    winner = topWinners.get(0).getOne();
                }
                gameEvents.add(new GameEnded(e.gameId(), winner));
                yield gameEvents;
            }
        };
        gameEventRepository.appendEvent(gameId, events.getLast());
        return new GameState(gameId, events);
    }

    private Move determineGameMove(Move move) {
        return Move.values()[random.nextInt(Move.values().length)];
    }

    private Winner determineWinner(Move player1Move, Move player2Move) {
        if (player1Move == player2Move) {
            return Winner.DRAW;
        }
        return switch (player1Move) {
            case ROCK -> player2Move == Move.SCISSORS ? Winner.HUMAN : Winner.GAME;
            case PAPER -> player2Move == Move.ROCK ? Winner.HUMAN : Winner.GAME;
            case SCISSORS -> player2Move == Move.PAPER ? Winner.HUMAN : Winner.GAME;
        };
    }

}