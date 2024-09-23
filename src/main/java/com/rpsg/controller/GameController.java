package com.rpsg.controller;

import com.rpsg.model.GameCommand;
import com.rpsg.model.GameEvent;
import com.rpsg.model.GameState;
import com.rpsg.model.Move;
import com.rpsg.model.handlers.EndGameHandler;
import com.rpsg.model.handlers.PlayRoundHandler;
import com.rpsg.model.handlers.StartGameHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@RestController
public class GameController {

    private final StartGameHandler startGameHandler;
    private final PlayRoundHandler playRoundHandler;
    private final EndGameHandler endGameHandler;

    @Autowired
    public GameController(StartGameHandler startGameHandler,
                          PlayRoundHandler playRoundHandler,
                          EndGameHandler endGameHandler) {
        this.startGameHandler = startGameHandler;
        this.playRoundHandler = playRoundHandler;
        this.endGameHandler = endGameHandler;
    }

    @PostMapping("/startGame")
    public Mono<GameEvent.GameStarted> startGame(@RequestParam("playerName") String playerName) {
        var gameId = UUID.randomUUID().toString();
        return Mono.fromCallable(() -> startGameHandler.handle(gameId, new GameCommand.StartGame(playerName)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping("/playRound/{gameId}")
    public Mono<ResponseEntity<GameEvent.RoundPlayed>> playRound(@PathVariable String gameId,
                                                                 @RequestParam("playerMove") Move playerMove) {
        return Mono.fromCallable(() -> {
            var gameState = playRoundHandler.handle(gameId, new GameCommand.PlayRound(playerMove));
            return new ResponseEntity<>(gameState, HttpStatus.OK);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping("/endGame/{gameId}")
    public Mono<ResponseEntity<GameState>> endGame(@PathVariable String gameId) {
        return Mono.fromCallable(() -> {
            var gameState = endGameHandler.handle(gameId);
            return new ResponseEntity<>(gameState, HttpStatus.OK);
        }).subscribeOn(Schedulers.boundedElastic());
    }
}