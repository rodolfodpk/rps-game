package com.rpsg.controller;

import com.rpsg.model.GameEvent;
import com.rpsg.model.GameState;
import com.rpsg.model.Move;
import com.rpsg.model.Winner;
import com.rpsg.model.handlers.EndGameHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/games")
public class EndGameController {
    private final EndGameHandler endGameHandler;
    private final GameRepresentationMapper mapper;

    public EndGameController(EndGameHandler endGameHandler, GameRepresentationMapper mapper) {
        this.endGameHandler = endGameHandler;
        this.mapper = mapper;
    }

    @PutMapping("/{gameId}/endings")
    public Mono<ResponseEntity<GameRepresentation>> endGame(@PathVariable String gameId) {
        return Mono.fromCallable(() -> {
                    var gameState = endGameHandler.handle(gameId);
                    return new ResponseEntity<>(mapper.map(gameState), HttpStatus.OK);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }


    public record GameStats(Map<String, Map<Move, Long>> moves, Map<Winner, Long> winners) {
    }

    public record GameRepresentation(String gameId, String playerId, Winner winner, GameStats stats) {
    }

    @Component
    public static class GameRepresentationMapper {

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

}