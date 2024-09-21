package com.rpsg.model;

import com.rpsg.model.handlers.EndGameHandler;
import com.rpsg.model.handlers.StartGameHandler;
import com.rpsg.repository.GameEventInMemoryRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AbstractScenarioTest {

    protected final GameEventRepository gameEventRepository = new GameEventInMemoryRepository();
    protected final StartGameHandler startGameHandler = new StartGameHandler(gameEventRepository);
    protected final EndGameHandler endGameHandler = new EndGameHandler(gameEventRepository);
    protected final String gameID = "testGame1";
}
