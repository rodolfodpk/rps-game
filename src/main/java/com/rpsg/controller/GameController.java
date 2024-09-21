package com.rpsg.controller;

import com.rpsg.model.GameCommand;
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

import java.util.UUID;

@RestController
public class GameController {

    private final StartGameHandler startGameHandler;
    private final PlayRoundHandler playRoundHandler;
    private final EndGameHandler endGameHandler;

    private final GameCommand.EndGame endGameCommand = new GameCommand.EndGame();

    @Autowired
    public GameController(StartGameHandler startGameHandler,
                          PlayRoundHandler playRoundHandler,
                          EndGameHandler endGameHandler) {
        this.startGameHandler = startGameHandler;
        this.playRoundHandler = playRoundHandler;
        this.endGameHandler = endGameHandler;
    }

    @PostMapping("/startGame")
    public Mono<GameState> startGame(@RequestParam("playerName") String playerName) {
        var gameId = UUID.randomUUID().toString();
        var gameState = startGameHandler.handle(gameId, new GameCommand.StartGame(playerName));
        return Mono.just(gameState);
    }

    @PutMapping("/playRound/{gameId}")
    public Mono<ResponseEntity<GameState>> playRound(@PathVariable String gameId,
                                                     @RequestParam("playerMove") Move playerMove) {
        var gameState = playRoundHandler.handle(gameId, new GameCommand.PlayRound(playerMove));
        return Mono.just(new ResponseEntity<>(gameState, HttpStatus.OK));
    }

    @PutMapping("/endGame/{gameId}")
    public Mono<ResponseEntity<GameState>> endGame(@PathVariable String gameId) {
        var gameState = endGameHandler.handle(gameId, endGameCommand);
        return Mono.just(new ResponseEntity<>(gameState, HttpStatus.OK));
    }
}