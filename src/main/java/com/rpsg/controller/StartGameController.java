package com.rpsg.controller;

import com.hazelcast.map.IMap;
import com.rpsg.model.GameEvent;
import com.rpsg.model.GameState;
import com.rpsg.model.handlers.StartGameHandler;
import com.rpsg.repository.StartGameProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/games")
public class StartGameController {

    private static final Logger logger = LoggerFactory.getLogger(StartGameController.class);

    private final IMap<String, GameState> gameStateMap;
    private final StartGameHandler startGameHandler;

    public StartGameController(IMap<String, GameState> gameStateMap, StartGameHandler startGameHandler) {
        this.gameStateMap = gameStateMap;
        this.startGameHandler = startGameHandler;
    }

    @PutMapping("/{gameId}")
    public Mono<ResponseEntity<GameEvent.GameStarted>> startGame(@PathVariable String gameId,
                                                                 @RequestParam("playerName") String playerName) {
        return Mono.fromCallable(() -> {
                    logger.debug("start.gameId: {}", gameId);
                    var processor = new StartGameProcessor(playerName, startGameHandler);
                    return gameStateMap.executeOnKey(gameId, processor);
                })
                .map(gameStarted -> new ResponseEntity<>(gameStarted, HttpStatus.CREATED))
                .onErrorResume(IllegalArgumentException.class, e -> Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(IllegalStateException.class, e -> Mono.just(ResponseEntity.badRequest().build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()))
                .subscribeOn(Schedulers.boundedElastic());
    }
}