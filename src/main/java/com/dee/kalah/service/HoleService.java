package com.dee.kalah.service;

import com.dee.kalah.exception.DatabaseException;
import com.dee.kalah.model.Hole;
import com.dee.kalah.repository.HoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HoleService {

    @Autowired
    private HoleRepository holeRepository;

    public HoleService(){}

    public List<Hole> saveOrUpdate(Iterable<Hole> holes){
        if(holes == null || !holes.iterator().hasNext()){
            throw new DatabaseException("Invalid Holes - Cannot save data");
        }
        return holeRepository.saveAll(holes);
    }

    public List<Hole> getHoles(long gameId){
        if(gameId <= 0){
            throw new DatabaseException("Cannot Retrieve Holes - Invalid Game Id");
        }
        return holeRepository.findByGameId(gameId);
    }

    public Hole get(long gameId, int holeId){
        if(gameId <= 0 || holeId <= 0){
            throw new DatabaseException("Cannot Retrieve Hole - Invalid Game Id/Hole Id");
        }
        return holeRepository.findByHoleIdAndGameId(holeId, gameId);
    }
}
