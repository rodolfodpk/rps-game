package com.rpsg.controller;

import com.rpsg.model.GameEvent;
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
    private final StartGameHandler startGameHandler;

    public StartGameController(StartGameHandler startGameHandler) {
        this.startGameHandler = startGameHandler;
    }

    @PostMapping()
    public Mono<ResponseEntity<GameEvent.GameStarted>> startGame(@RequestParam("playerName") String playerName) {
        var gameId = UUID.randomUUID().toString();
        return Mono.fromCallable(() -> startGameHandler.handle(gameId, playerName))
                .map(gameStarted -> new ResponseEntity<>(gameStarted, HttpStatus.CREATED))
                .onErrorResume(IllegalStateException.class, e -> Mono.just(ResponseEntity.badRequest().build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()))
                .subscribeOn(Schedulers.boundedElastic());
    }
}