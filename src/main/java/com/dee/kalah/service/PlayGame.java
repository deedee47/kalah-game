package com.dee.kalah.service;

import com.dee.kalah.exception.KalahInvalidMoveException;
import com.dee.kalah.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PlayGame{

    @Autowired
    private GameService gameService;
    @Autowired
    private HoleService holeService;

    private final int NUMBER_OF_HOLES = 14;
    private final int PLAYER_1_KALAH = 7;
    private final int PLAYER_2_KALAH = 14;
    private final int DEFAULT_HOLE_COUNT = 6;
    private final int DEFAULT_KALAH_COUNT = 0;
    private final int PLAYER_1_START_INDEX = 1;
    private final int PLAYER_2_START_INDEX = 8;
    private final List<Integer> PLAYER_1_HOLES =
            new ArrayList<Integer>(){{add(1);add(2);add(3);add(4);add(5);add(6);add(7);}};
    private final List<Integer>  PLAYER_2_HOLES =
            new ArrayList<Integer>(){{add(8);add(9);add(10);add(11);add(12);add(13);add(14);}};
    private Map<Integer, Long> idMapping = new HashMap<>();

    public PlayGame(){}

    @Transactional(propagation = Propagation.REQUIRED)
    public GameResponse createGame(){
        //create game, init default holes
        //save game, holes
        Game game = gameService.saveOrUpdate(initGame());
        holeService.saveOrUpdate(initHoles(game.getGameId()));

        return new GameResponse(game.getGameId(), "");
    }

    private Game initGame(){
        return new Game();
    }

    private List<Hole> initHoles(long gameId){
        List<Hole> holeList = new ArrayList<>();
        for(int count = 1; count <= NUMBER_OF_HOLES; count++){
            if(count == PLAYER_1_KALAH || count == PLAYER_2_KALAH){
                holeList.add(new Hole(count, gameId, DEFAULT_KALAH_COUNT));
            }else{
                holeList.add(new Hole(count, gameId, DEFAULT_HOLE_COUNT));
            }
        }
        return  holeList;
    }

    public MoveResponse makeMove(long gameId, int holdId){
        //get game
        //check game status
        //if p1 turn, check if hole is in p1 range
        //if p2 turn, check if hold is in p2 range
        //check if holdId is kalah
        Game game = gameService.getGameById(gameId);

        if(game.getGameStatus() == GameStatus.DONE){
            throw new KalahInvalidMoveException("No More Moves - Game is Finished");
        }

        //Get Player
        Player currentPlayer = getPlayer(game);
        if(currentPlayer == null){
            throw new KalahInvalidMoveException("Invalid Player");
        }

        //check move
        if(!isMoveValid(currentPlayer, holdId, gameId)){
            throw new KalahInvalidMoveException("Invalid Move");
        }

        //retrieve holes
        List<Hole> holesForGame = holeService.getHoles(gameId);
        if(holesForGame.size() == 0){
            throw new KalahInvalidMoveException("Could not retrieve holes for this game");
        }
        Map<Integer, Integer> holesMap =
                holesForGame.stream().collect(Collectors.toMap(Hole :: getHoleId, Hole :: getHoleContentCount));

        //keep IDs for saving data in the future
        idMapping = holesForGame.stream().collect(Collectors.toMap(Hole :: getHoleId, Hole :: getId));

        //make move
        return move(holesMap, currentPlayer, game, holdId);
    }

    private boolean isHoleAKalah(int holdId){
        if(holdId == PLAYER_1_KALAH || holdId == PLAYER_2_KALAH) return true;

        return false;
    }

    private boolean isHoleForPlayer(Player player, int holeId){
        //player will be passed from the game
        //check if hole is in the player list
        if(player.name().equals(Player.PLAYER1.name()) && PLAYER_1_HOLES.contains(holeId)) return true;

        if(player.name().equals(Player.PLAYER2.name()) && PLAYER_2_HOLES.contains(holeId)) return true;

        return false;
    }

    private boolean isMoveValid(Player player, int holeId, long gameId){

        //starting from kalah is an invalid move
        if(isHoleAKalah(holeId)){
            throw new KalahInvalidMoveException("You cannot make a move from any Kalah");
        }

        //starting from an invalid pit number is an invalid move
        if(!PLAYER_1_HOLES.contains(holeId) && !PLAYER_2_HOLES.contains(holeId)){
            throw new KalahInvalidMoveException("Invalid Hole - Hole does not exist");
        }

        //starting from opponent's hole is an invalid move
        if(!isHoleForPlayer(player, holeId)) {
             throw new KalahInvalidMoveException("You cannot make a move from opponent's holes");
        }

        //check if hole is empty
        //then check if player has any other moves
        Hole hole = holeService.get(gameId, holeId);
        if(hole.getHoleContentCount() == 0){
            throw new KalahInvalidMoveException("Hole is Empty");
        }

        //if there's no obstruction on the player's hole
        return true;
    }

    private boolean isOpponentKalah(Player player, int holeId){
        if(player.name().equals(Player.PLAYER1.name()) && holeId == PLAYER_2_KALAH) return true;

        if(player.name().equals(Player.PLAYER2.name()) && holeId == PLAYER_1_KALAH) return true;

        return false;
    }

    private Player getPlayer(Game game){
        //get player
        Player currentPlayer = null;
        if(game.getGameStatus() == GameStatus.NEW || game.getGameStatus() == GameStatus.PLAYER1_TURN){
            currentPlayer = Player.PLAYER1;
        }else if(game.getGameStatus() == GameStatus.PLAYER2_TURN ){
            currentPlayer = Player.PLAYER2;
        }
        return currentPlayer;
    }

    private MoveResponse move(Map<Integer, Integer> holes, Player player, Game game, int startingHoleId){

        //distribute stones
        int stonesToDistribute = holes.get(startingHoleId);
        int antiClockwiseCount = startingHoleId + 1;

        //empty the starting point
        holes.put(startingHoleId, 0);

        while(stonesToDistribute > 0 && antiClockwiseCount <= NUMBER_OF_HOLES){
            //avoid filling the opponents Kalah
            if(isOpponentKalah(player, antiClockwiseCount)){
                antiClockwiseCount++;
                continue;
            }

            holes.put(antiClockwiseCount, holes.get(antiClockwiseCount)+1);
            stonesToDistribute--;

            if(antiClockwiseCount == NUMBER_OF_HOLES){
                antiClockwiseCount = 1; //go back to 1
            }else{
                antiClockwiseCount++;
            }
        }

        //apply rules
        return applyRulesAfterAMove(holes, player, game, antiClockwiseCount);
    }

    private MoveResponse applyRulesAfterAMove(Map<Integer,Integer> holes, Player player, Game game, int lastHole){
        int oppositeHole = NUMBER_OF_HOLES - lastHole;

        //last stone ends in player1's house and in an empty hole
        if(player.name().equals(Player.PLAYER1.name()) && PLAYER_1_HOLES.contains(lastHole)
                && holes.get(lastHole) == 0){

            //put stones in player1's kalah - last stone hole plus opposite hole
            holes.put(PLAYER_1_KALAH, holes.get(PLAYER_1_KALAH) + holes.get(lastHole) + holes.get(oppositeHole));

            //if the other player has no moves, keep status on player 1
            if(hasMoreMoves(Player.PLAYER2, holes)){
               //change game status to player 2's turn
                game.setGameStatus(GameStatus.PLAYER2_TURN);
            }

            //empty both holes
            holes.put(lastHole, 0);
            holes.put(oppositeHole,0);
        }

        //last stone ends in player2's house and in an empty hole
        if (player.name().equals(Player.PLAYER2.name()) && PLAYER_2_HOLES.contains(lastHole)
                && holes.get(lastHole) == 0){

            //put stones in player2's kalah - last stone hole plus opposite hole
            holes.put(PLAYER_2_KALAH, holes.get(PLAYER_2_KALAH) + holes.get(lastHole) + holes.get(oppositeHole));

            //if the other player has no moves, keep status on player 2
            if(hasMoreMoves(Player.PLAYER1, holes)){
                //change game status to player 1's turn
                game.setGameStatus(GameStatus.PLAYER1_TURN);
            }

            //empty both holes
            holes.put(lastHole, 0);
            holes.put(oppositeHole,0);
        }

        //if last stone is in opponents hole or lasthole is not empty
        if(player.name().equals(Player.PLAYER1.name()) && holes.get(lastHole) != 0){
            game.setGameStatus(GameStatus.PLAYER2_TURN);
        }
        if(player.name().equals(Player.PLAYER2.name()) && holes.get(lastHole) != 0){
            game.setGameStatus(GameStatus.PLAYER1_TURN);
        }

        //if the last move ends in a Kalah and the game is done
        if((lastHole == PLAYER_1_KALAH || lastHole == PLAYER_2_KALAH) &&
                isGameDone(holes.get(PLAYER_1_KALAH), holes.get(PLAYER_2_KALAH))){
            game.setGameStatus(GameStatus.DONE);
        }

        //if last stone is player's kalah - player repeats a turn - no need to change the game status

        return saveMove(game, holes);
    }

    private boolean isGameDone(int player1Kalah, int player2Kalah){
        if(player1Kalah + player2Kalah == ((NUMBER_OF_HOLES - 2) * DEFAULT_HOLE_COUNT)){
           return true;
        }
        return false;
    }

    private boolean hasMoreMoves(Player player, Map<Integer, Integer> holes){
        if(player.name().equals(Player.PLAYER1.name())){
            for(int count = PLAYER_1_START_INDEX; count < PLAYER_1_KALAH; count ++){
                if(holes.get(count) > 0) return true;
            }
        }
        else if (player.name().equals(Player.PLAYER2.name())){
            for(int count = PLAYER_2_START_INDEX; count < PLAYER_2_KALAH; count ++){
                if(holes.get(count) > 0) return true;
            }
        }

        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private MoveResponse saveMove(Game game, Map<Integer, Integer> holes){

        List<Hole> holesList = holes.entrySet()
                                    .stream()
                                    .map(e -> new Hole(e.getKey(), game.getGameId(), e.getValue()))
                                    .collect(Collectors.toList());

        //update the ids due to map conversion
        for(int i = 0; i< holesList.size(); i++){
            Hole hole = holesList.get(i);
            hole.setId(idMapping.get(hole.getHoleId()));
        }

        gameService.saveOrUpdate(game);
        holeService.saveOrUpdate(holesList);
        return new MoveResponse(game.getGameId(),  holes);
    }
}
