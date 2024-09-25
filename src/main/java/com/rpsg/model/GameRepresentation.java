package com.rpsg.model;

import java.io.Serializable;

public record GameRepresentation(String gameId, String playerId, Winner winner,
                                 GameStats stats) implements Serializable {
}
