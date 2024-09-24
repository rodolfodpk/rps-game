package com.rpsg.controller;

import com.rpsg.model.GameEvent;
import com.rpsg.model.handlers.StartGameHandler;
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
    public Mono<GameEvent.GameStarted> startGame(@RequestParam("playerName") String playerName) {
        var gameId = UUID.randomUUID().toString();
        return Mono.fromCallable(() -> startGameHandler.handle(gameId, playerName))
                .subscribeOn(Schedulers.boundedElastic());
    }
}