package com.rpsg.model;

import com.rpsg.model.handlers.EndGameHandler;
import com.rpsg.model.handlers.StartGameHandler;
import com.rpsg.repository.GameEventCaffeineRepository;
import com.rpsg.repository.GameStatusCaffeineRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AbstractScenarioTest {

    protected final GameEventRepository gameEventRepository = new GameEventCaffeineRepository();
    protected final GameStatusRepository gameStatusRepository = new GameStatusCaffeineRepository();
    protected final StartGameHandler startGameHandler = new StartGameHandler(gameEventRepository, gameStatusRepository);
    protected final EndGameHandler endGameHandler = new EndGameHandler(gameEventRepository, gameStatusRepository);
    protected final String gameID = "testGame1";
}
