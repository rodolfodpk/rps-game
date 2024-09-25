package com.rpsg.model;

import java.io.Serializable;
import java.util.Map;

public record GameStats(Map<String, Map<Move, Long>> moves, Map<Winner, Long> winners) implements Serializable {
}
