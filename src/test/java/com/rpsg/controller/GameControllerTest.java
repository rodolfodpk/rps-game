package com.rpsg.controller;

import com.rpsg.model.GameEvent;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

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
        webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/startGame")
                        .queryParam("playerName", "John")
                        .build(gameID))
                .exchange()
                .expectStatus().isOk()
                .expectBody(GameEvent.GameStarted.class)
                .consumeWith(response -> this.gameID = Objects.requireNonNull(response.getResponseBody()).gameId());
    }

    @Test
    @Order(2)
    public void playRound() {
        webTestClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path("/playRound/{gameID}")
                        .queryParam("playerMove", ROCK)
                        .build(gameID))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.gameId").isEqualTo(gameID)
                .jsonPath("$.['@type']").isEqualTo(GameEvent.RoundPlayed.class.getSimpleName())
                .jsonPath("$.gameId").isEqualTo(gameID);
    }

    @Test
    @Order(3)
    public void endGame() {
        webTestClient
                .put()
                .uri("/endGame/" + gameID)
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
