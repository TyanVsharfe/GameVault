package com.gamevault;

import com.gamevault.db.model.Game;
import com.gamevault.db.repository.GameRepository;
import com.gamevault.form.GameForm;
import com.gamevault.service.GameService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class GameServiceTest {

    @Autowired
    private GameService gameService;

    @MockBean
    private GameRepository gameRepository;

    @Test
    public void addGame() {
        GameForm newGameForm = new GameForm(1L, "Battlefield", "https://www.digiseller.ru/preview/509425/p1_3993666_ec9fa229.jpeg");
        Game newGame = new Game(newGameForm);
        gameService.saveGame(newGame);

        Assertions.assertNotNull(newGameForm);
        Assertions.assertEquals(newGameForm.title(), "Battlefield");

        Mockito.verify(gameRepository, Mockito.times(1)).save(newGame);
    }

    @Test
    public void deleteGame() {

    }

    @Test
    public void updateGame() {

    }
}
