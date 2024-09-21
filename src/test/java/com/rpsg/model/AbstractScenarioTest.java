package com.rpsg.model;

import com.rpsg.infra.GameEventInMemoryRepository;
import com.rpsg.model.handlers.EndGameHandler;
import com.rpsg.model.handlers.StartGameHandler;

public interface AbstractScenarioTest {

    GameEventRepository gameEventRepository = new GameEventInMemoryRepository();
    StartGameHandler startGameHandler = new StartGameHandler(gameEventRepository);
    EndGameHandler endGameHandler = new EndGameHandler(gameEventRepository);

}
