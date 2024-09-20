package com.rpsg.model.handlers;

import com.rpsg.model.Move;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class GameMoveDecider {

    private final Random random = new Random();

    public Move determineGameMove() {
        return Move.values()[random.nextInt(Move.values().length)];
    }

}
