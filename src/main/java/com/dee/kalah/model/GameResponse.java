package com.dee.kalah.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GameResponse {
    @JsonProperty("id")
    public long gameId;
    @JsonProperty("uri")
    public String gameURI;

    public GameResponse(long gameId, String gameURI){
        this.gameId = gameId;
        this.gameURI = gameURI;
    }
}
