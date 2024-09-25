package com.rpsg.controller;

import com.hazelcast.map.IMap;
import com.rpsg.repository.EndGameProcessor;
import com.rpsg.model.GameRepresentation;
import com.rpsg.model.GameState;
import com.rpsg.model.handlers.EndGameHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/games")
public class EndGameController {

    private final IMap<String, GameState> gameStateMap;
    private final EndGameProcessor.GameRepresentationMapper mapper;

    public EndGameController(IMap<String, GameState> gameStateMap, EndGameProcessor.GameRepresentationMapper mapper) {
        this.gameStateMap = gameStateMap;
        this.mapper = mapper;
    }

    @PutMapping("/{gameId}/endings")
    public Mono<ResponseEntity<GameRepresentation>> endGame(@PathVariable String gameId) {
        return Mono.fromCallable(() -> {
                    var processor = new EndGameProcessor(new EndGameHandler());
                    return gameStateMap.executeOnKey(gameId, processor);
                })
                .map(gameState -> new ResponseEntity<>(gameState, HttpStatus.CREATED))
                .onErrorResume(IllegalArgumentException.class, e -> Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(IllegalStateException.class, e -> Mono.just(ResponseEntity.badRequest().build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()))
                .subscribeOn(Schedulers.boundedElastic());
    }

}