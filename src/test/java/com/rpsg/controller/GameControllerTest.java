package com.rpsg.controller;

import com.rpsg.model.GameCommand;
import com.rpsg.model.GameEvent;
import com.rpsg.model.GameState;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.rpsg.model.Move.ROCK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GameControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    private String gameID;

    @Test
    @Order(1)
    public void startGame() {
        var startGameCommand = new GameCommand.StartGame("John");
        webTestClient
                .post()
                .uri("/startGame")
                .body(Mono.just(startGameCommand), GameCommand.StartGame.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GameState.class)
                .consumeWith(response -> this.gameID = Objects.requireNonNull(response.getResponseBody()).gameId());
    }

    @Test
    @Order(2)
    public void playRound() {
        var playRoundCommand = new GameCommand.PlayRound(ROCK);
        webTestClient
                .put()
                .uri("/playRound/" + gameID)
                .body(Mono.just(playRoundCommand), GameCommand.PlayRound.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.gameId").isEqualTo(gameID)
                .jsonPath("$.events").isNotEmpty()
                .jsonPath("$.events[0]['@type']").isEqualTo(GameEvent.GameStarted.class.getSimpleName())
                .jsonPath("$.events[0].gameId").isEqualTo(gameID)
                .jsonPath("$.events[1]['@type']").isEqualTo(GameEvent.RoundPlayed.class.getSimpleName())
                .jsonPath("$.events[1].gameId").isEqualTo(gameID)
                .jsonPath("$.events[1].playerMove").isEqualTo(ROCK.name())
                .jsonPath("$.events[1].gameMove").isNotEmpty()
                .jsonPath("$.events[1].winner").isNotEmpty();
        ;
    }

    @Test
    @Order(3)
    public void endGame() {
        var endGameCommand = new GameCommand.EndGame();
        webTestClient
                .put()
                .uri("/endGame/" + gameID)
                .body(Mono.just(endGameCommand), GameCommand.EndGame.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.gameId").isEqualTo(gameID)
                .jsonPath("$.events").isNotEmpty()
                .jsonPath("$.events[0]['@type']").isEqualTo(GameEvent.GameStarted.class.getSimpleName())
                .jsonPath("$.events[0].gameId").isEqualTo(gameID)
                .jsonPath("$.events[1]['@type']").isEqualTo(GameEvent.RoundPlayed.class.getSimpleName())
                .jsonPath("$.events[1].gameId").isEqualTo(gameID)
                .jsonPath("$.events[1].playerMove").isEqualTo(ROCK.name())
                .jsonPath("$.events[1].gameMove").isNotEmpty()
                .jsonPath("$.events[1].winner").isNotEmpty()
                .jsonPath("$.events[2]['@type']").isEqualTo(GameEvent.GameEnded.class.getSimpleName())
                .jsonPath("$.events[2].gameId").isEqualTo(gameID);
    }
}
