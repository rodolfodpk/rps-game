package com.rpsg.model;

import com.rpsg.model.handlers.EndGameHandler;
import com.rpsg.model.handlers.StartGameHandler;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.ArrayList;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AbstractScenarioTest {

    protected final StartGameHandler startGameHandler = new StartGameHandler();
    protected final EndGameHandler endGameHandler = new EndGameHandler();
    protected final String gameID = "testGame1";
    protected final ArrayList<GameEvent> events = new ArrayList<>();
}
