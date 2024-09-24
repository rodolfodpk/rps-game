package com.rpsg.model;

public interface GameStatusRepository {

    void setStatus(String gameId, GameStatus status);

    GameStatus getStatus(String gameId);

}
