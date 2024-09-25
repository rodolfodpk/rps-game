package com.rpsg.controller;

import com.hazelcast.map.IMap;
import com.rpsg.repository.PlayGameProcessor;
import com.rpsg.model.GameEvent;
import com.rpsg.model.GameState;
import com.rpsg.model.Move;
import com.rpsg.model.handlers.PlayRoundHandler;
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
public class PlayRoundController {
    private final IMap<String, GameState> gameStateMap;
    private final PlayRoundHandler playRoundHandler;

    public PlayRoundController(IMap<String, GameState> gameStateMap, PlayRoundHandler playRoundHandler) {
        this.gameStateMap = gameStateMap;
        this.playRoundHandler = playRoundHandler;
    }

    @PutMapping("/{gameId}/plays")
    public Mono<ResponseEntity<GameEvent.RoundPlayed>> playRound(@PathVariable String gameId,
                                                                 @RequestParam("playerMove") Move playerMove) {
        return Mono.fromCallable(() -> {
                    var processor = new PlayGameProcessor(playRoundHandler, playerMove);
                    return gameStateMap.executeOnKey(gameId, processor);
                })
                .map(roundPlayed -> new ResponseEntity<>(roundPlayed, HttpStatus.CREATED))
                .onErrorResume(IllegalArgumentException.class, e -> Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(IllegalStateException.class, e -> Mono.just(ResponseEntity.badRequest().build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()))
                .subscribeOn(Schedulers.boundedElastic());
    }
}