package com.rpsg.controller;

import com.rpsg.model.GameCommand;
import com.rpsg.model.GameState;
import com.rpsg.model.handlers.EndGameHandler;
import com.rpsg.model.handlers.PlayRoundHandler;
import com.rpsg.model.handlers.StartGameHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

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
    public Mono<GameState> startGame(@RequestBody GameCommand.StartGame command) {
        var gameId = UUID.randomUUID().toString();
        var gameState = startGameHandler.handle(gameId, command);
        return Mono.just(gameState);
    }

    @PutMapping("/playRound/{gameId}")
    public Mono<ResponseEntity<GameState>> playRound(@PathVariable String gameId,
                                                     @RequestBody GameCommand.PlayRound playRoundCommand) {
        var gameState = playRoundHandler.handle(gameId, playRoundCommand);
        return Mono.just(new ResponseEntity<>(gameState, HttpStatus.OK));
    }

    @PutMapping("/endGame/{gameId}")
    public Mono<ResponseEntity<GameState>> endGame(@PathVariable String gameId,
                                                   @RequestBody GameCommand.EndGame endGameCommand) {
        var gameState = endGameHandler.handle(gameId, endGameCommand);
        return Mono.just(new ResponseEntity<>(gameState, HttpStatus.OK));
    }
}