package com.dee.kalah.service;

import com.dee.kalah.exception.DatabaseException;
import com.dee.kalah.model.Game;
import com.dee.kalah.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GameService {

    @Autowired
    GameRepository gameRepository;

    public GameService(){  }

    public Game saveOrUpdate(Game game){
        if(game == null){
            throw new DatabaseException("Cannot Save Game - Invalid Game");
        }
        return gameRepository.save(game);
    }

    public Game getGameById(long gameId){
        if (gameId <= 0){
            throw new DatabaseException("Cannot Retrieve Holes - Invalid Game Id");
        }
        Optional<Game> game = gameRepository.findById(gameId);
        if (!game.isPresent()){
            return  null;
        }
        return game.get();
    }



}
