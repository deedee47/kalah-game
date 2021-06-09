package com.dee.kalah.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Hole")
public class Hole {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "HoleId")
    private int holeId;

    @Column(name = "GameId")
    @NotNull
    private long gameId;

    @NotNull
    @Column(name = "HoleContentCount")
    private int holeContentCount;

    public Hole(int holeId, long gameId, int holeContentCount) {
        this.holeId = holeId;
        this.gameId = gameId;
        this.holeContentCount = holeContentCount;
    }

    public Hole(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getHoleId() {
        return holeId;
    }

    public int getHoleContentCount() {
        return holeContentCount;
    }

    public void setHoleContentCount(int content) {
        holeContentCount = content;
    }

    public void setHoleId(int holeId) {
        this.holeId = holeId;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }
}
