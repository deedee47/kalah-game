package com.dee.kalah.controller;


import com.dee.kalah.model.GameResponse;
import com.dee.kalah.model.MoveResponse;
import com.dee.kalah.service.PlayGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/")
public class GameController {

    @Autowired
    PlayGame playGame;

    @PostMapping
    @RequestMapping(value = "/games")
    public ResponseEntity<GameResponse> newGame(HttpServletRequest request){
         GameResponse gameResponse = playGame.createGame();
         gameResponse.gameURI = String.format("%s://%s:%d/games/%d",request.getScheme(),  request.getServerName(),
                 request.getServerPort(), gameResponse.gameId);
         return new ResponseEntity<>(gameResponse, HttpStatus.CREATED);
    }

    @PutMapping
    @RequestMapping(value = "/games/{gameId}/pits/{pitId}")
    public ResponseEntity<MoveResponse> makeMove(@PathVariable("gameId") long gameId,
                                                 @PathVariable("pitId") int holeId,
                                                 HttpServletRequest request){
        MoveResponse moveResponse = playGame.makeMove(gameId, holeId);
        moveResponse.gameURI = String.format("%s://%s:%d/games/%d",request.getScheme(),  request.getServerName(),
                request.getServerPort(), moveResponse.gameId);
        return new ResponseEntity<>(moveResponse, HttpStatus.OK);
    }

}
