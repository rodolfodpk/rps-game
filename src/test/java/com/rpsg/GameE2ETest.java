package com.rpsg;

import com.rpsg.model.GameRepresentation;
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

import java.util.UUID;

import static com.rpsg.model.Move.ROCK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GameE2ETest {

    private static final String invalidGameId = UUID.randomUUID().toString();
    @Autowired
    private WebTestClient webTestClient;
    private String gameID;

    @Test
    @Order(1)
    public void startGame() {
        var reponse = webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/games")
                        .queryParam("playerName", "John")
                        .build(gameID))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(GameEvent.GameStarted.class);

        var gameStarted = reponse.returnResult().getResponseBody();

        assertNotNull(gameStarted);
        this.gameID = gameStarted.gameId();
        assertNotNull(gameID);

        assertEquals("John", gameStarted.player());

    }

    @Test
    @Order(2)
    public void playRound() {
        webTestClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path("/games/{gameID}/plays")
                        .queryParam("playerMove", ROCK)
                        .build(gameID))
                .exchange()
                .expectStatus().isCreated()
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
                .uri(uriBuilder -> uriBuilder
                        .path("/games/{gameID}/endings")
                        .build(gameID))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(GameRepresentation.class)
                .consumeWith(response -> {
                    GameRepresentation gameRepresentation = response.getResponseBody();
                    assertNotNull(gameRepresentation.gameId());
                    assertNotNull(gameRepresentation.playerId());
                    assertNotNull(gameRepresentation.winner());
                    assertNotNull(gameRepresentation.stats());
                });
    }

    @Test
    @Order(4)
    public void playRoundGameNotFound() {
        webTestClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path("/games/{gameID}/plays")
                        .queryParam("playerMove", ROCK)
                        .build(invalidGameId))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(5)
    public void endGameNotFound() {
        webTestClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path("/games/{gameID}/endings")
                        .build(invalidGameId))
                .exchange()
                .expectStatus().isNotFound();
    }
}
