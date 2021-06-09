package com.dee.kalah.service;

import com.dee.kalah.exception.DatabaseException;
import com.dee.kalah.model.Game;
import com.dee.kalah.model.GameStatus;
import com.dee.kalah.repository.GameRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GameServiceTest {

    Game testGame ;

    @MockBean
    GameRepository gameRepository;

    @Autowired
    GameService gameService;

    @Before
    public void setUp(){
        //gameService = new GameService();
        testGame = new Game();
    }

    @Test
    public void CreateGameWithSuccessTest(){
        Game sample = gameService.saveOrUpdate(testGame);
        BDDMockito.given(gameService.saveOrUpdate(testGame)).willReturn(sample);


      //  Assert.assertTrue(testGame.getGameId() > 0);
     //   Assert.assertEquals(sample.getGameStatus(), GameStatus.NEW);
    }

    @Test(expected = DatabaseException.class)
    public void CreateGameWithFailureTest() throws DatabaseException {
        Game sample = gameService.saveOrUpdate(null);
        Assert.assertEquals(sample, null);
    }
}
