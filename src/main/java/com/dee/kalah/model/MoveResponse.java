package com.dee.kalah.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class MoveResponse {
    @JsonProperty("id")
    public long gameId;
    @JsonProperty("uri")
    public String gameURI;
    @JsonProperty("status")
    public Map<Integer, Integer> holeContent;

    public MoveResponse (long gameId, Map<Integer, Integer> holeContent){
        this.gameId = gameId;
        this.gameURI = "";
        this.holeContent = holeContent;
    }
}
