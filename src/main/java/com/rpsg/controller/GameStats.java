package com.rpsg.controller;

import com.rpsg.model.Move;
import com.rpsg.model.Winner;

import java.util.Map;

public record GameStats(Map<String, Map<Move, Long>> moves, Map<Winner, Long> winners) {

}
