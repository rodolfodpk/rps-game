package com.rpsg.model.handlers;

import com.rpsg.model.GameCommand;
import com.rpsg.model.GameEvent;
import com.rpsg.model.GameState;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface AbstractCommandHandler<C extends GameCommand> {

    static <E extends GameEvent> List<E> appendElement(List<E> original, E newElement) {
        original.forEach(System.out::println);
        return Stream.concat(original.stream(), Stream.of(newElement))
                .collect(Collectors.toList());
    }

    GameState handle(C command);

}
