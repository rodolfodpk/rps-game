package com.rpsg.controller;

import com.hazelcast.map.IMap;
import com.rpsg.repository.StartGameProcessor;
import com.rpsg.model.GameEvent;
import com.rpsg.model.GameState;
import com.rpsg.model.handlers.StartGameHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@RestController
@RequestMapping("/games")
public class StartGameController {

    private final IMap<String, GameState> gameStateMap;
    private final StartGameHandler startGameHandler;

    public StartGameController(IMap<String, GameState> gameStateMap, StartGameHandler startGameHandler) {
        this.gameStateMap = gameStateMap;
        this.startGameHandler = startGameHandler;
    }

    @PostMapping()
    public Mono<ResponseEntity<GameEvent.GameStarted>> startGame(@RequestParam("playerName") String playerName) {
        var gameId = UUID.randomUUID().toString();
        return Mono.fromCallable(() -> {
                    var processor = new StartGameProcessor(playerName, startGameHandler);
                    return gameStateMap.executeOnKey(gameId, processor);
                })
                .map(gameStarted -> new ResponseEntity<>(gameStarted, HttpStatus.CREATED))
                .onErrorResume(IllegalStateException.class, e -> Mono.just(ResponseEntity.badRequest().build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()))
                .subscribeOn(Schedulers.boundedElastic());
    }
}