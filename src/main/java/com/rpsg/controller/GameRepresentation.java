package com.rpsg.controller;

import com.rpsg.model.Winner;

public record GameRepresentation(String gameId, String playerId, Winner winner, GameStats stats) {


}
