//package com.rpsg.repository;
//
//import com.repository.core.Hazelcast;
//import com.repository.core.HazelcastInstance;
//import com.repository.map.IMap;
//import com.rpsg.model.GameEvent;
//import com.rpsg.model.Move;
//import com.rpsg.model.Winner;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.pcollections.TreePVector;
//
//import java.util.List;
//
//import static org.junit.Assert.assertEquals;
//
//@Disabled
//public class GamaEventTest {
//
//    private HazelcastInstance hazelcastInstance;
//
//    @BeforeEach
//    public void setUp() {
//        hazelcastInstance = Hazelcast.newHazelcastInstance();
//    }
//
//    @AfterEach
//    public void tearDown() {
//        hazelcastInstance.shutdown();
//    }
//
//    @Test
//    public void testStoreAndRetrieveGameEvents() {
//        // Step 2: Storing the List<GameEvent> in Hazelcast
//        IMap<String, List<GameEvent>> map = hazelcastInstance.getMap("eventsMap");
//
//        List<GameEvent> gameEvents = List.of(
//                new GameEvent.GameStarted("game1", "player1"),
//                new GameEvent.RoundPlayed("game1", Move.ROCK, Move.PAPER, Winner.HUMAN),
//                new GameEvent.GameEnded("game1", Winner.HUMAN)
//        );
//
//        map.put("game1", gameEvents);
//
//        // Step 3: Retrieving the List<GameEvent> from Hazelcast
//        List<GameEvent> retrievedEvents = map.get("game1");
//
//        // Verify the stored and retrieved list are the same
//        assertEquals(gameEvents, retrievedEvents);
//    }
//
//    @Test
//    public void testStoreAndRetrieveGameState() {
//        IMap<String, GameState2> map = hazelcastInstance.getMap("gameStateMap");
//
//        List<? extends GameEvent> gameEvents = List.of(
//                new GameEvent.GameStarted("game1", "player1"),
//                new GameEvent.RoundPlayed("game1", Move.ROCK, Move.PAPER, Winner.HUMAN),
//                new GameEvent.GameEnded("game1", Winner.HUMAN)
//        );
//
//        GameState2 gameState = new GameState2("game1", TreePVector.from(gameEvents));
//
//        gameState.addEvent(new GameEvent.RoundPlayed("game1", Move.ROCK, Move.PAPER, Winner.HUMAN));
//
//        map.put("game1", gameState);
//
//        GameState2 retrievedState = map.get("game1");
//
//        assertEquals(gameState, retrievedState);
//    }
//
//}
