package com.rpsg.model;

import com.rpsg.model.GameCommand.EndGame;
import com.rpsg.model.GameCommand.PlayRound;
import com.rpsg.model.GameCommand.StartGame;
import com.rpsg.model.handlers.EndGameHandler;
import com.rpsg.model.handlers.PlayRoundHandler;
import com.rpsg.model.handlers.StartGameHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class GameCommandHandler {

    private final GameEventRepository gameEventRepository;
    private final StartGameHandler startGameHandler;
    private final PlayRoundHandler playRoundHandler;
    private final EndGameHandler endGameHandler;

    public GameCommandHandler(GameEventRepository gameEventRepository,
                              StartGameHandler startGameHandler,
                              PlayRoundHandler playRoundHandler,
                              EndGameHandler endGameHandler) {
        this.gameEventRepository = gameEventRepository;
        this.startGameHandler = startGameHandler;
        this.playRoundHandler = playRoundHandler;
        this.endGameHandler = endGameHandler;
    }

    public GameState handle(GameCommand command) {
        return switch (command) {
            case StartGame s -> {
                var newEvent = startGameHandler.handle(s);
                gameEventRepository.appendEvent(newEvent.gameId(), newEvent);
                yield new GameState(newEvent.gameId(), List.of(newEvent));
            }
            case PlayRound p -> {
                var currentEvents = gameEventRepository.findAll(p.gameId());
                var newEvent = playRoundHandler.handle(p, currentEvents.toImmutable());
                gameEventRepository.appendEvent(p.gameId(), newEvent);
                var newEvents = appendElement(currentEvents.castToList(), newEvent);
                yield new GameState(newEvent.gameId(), newEvents);
            }
            case EndGame p -> {
                var currentEvents = gameEventRepository.findAll(p.gameId());
                var newEvent = endGameHandler.handle(p, currentEvents.toImmutable());
                gameEventRepository.appendEvent(p.gameId(), newEvent);
                var newEvents = appendElement(currentEvents.castToList(), newEvent);
                yield new GameState(newEvent.gameId(), newEvents);
            }
        };
    }

    private static <T> List<T> appendElement(List<T> original, T newElement) {
        return Stream.concat(original.stream(), Stream.of(newElement))
                .collect(Collectors.toList());
    }

}