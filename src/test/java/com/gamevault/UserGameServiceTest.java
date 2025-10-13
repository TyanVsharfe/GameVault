package com.gamevault;

import com.gamevault.db.model.Game;
import com.gamevault.db.model.User;
import com.gamevault.db.model.UserGame;
import com.gamevault.db.repository.GameRepository;
import com.gamevault.db.repository.UserGameRepository;
import com.gamevault.dto.input.GameForm;
import com.gamevault.enums.Enums;
import com.gamevault.service.GameService;
import com.gamevault.service.UserGameService;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserGameServiceTest {

    @MockitoBean
    private UserGameRepository userGameRepository;
    @MockitoBean
    private GameRepository gameRepository;
    @MockitoBean
    private GameService gameService;

    @Autowired
    private UserGameService userGameService;

    private User user;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setUsername("testuser");
    }

    @Test
    public void add_shouldSaveUserGame_whenGameIsNew() {
        Long igdbId = 1979L;
        Game game = new Game(new GameForm(
                igdbId, "Battlefield 4", "//images.igdb.com/igdb/image/upload/t_thumb/co1nmf.jpg",
                "Battlefield 4 is the genre-defining action blockbuster created by Dice", Enums.categoryIGDB.main_game
        ));

        when(userGameRepository.findUserGameByGame_IgdbIdAndUser_Username(igdbId, user.getUsername()))
                .thenReturn(Optional.empty());
        when(gameRepository.findById(igdbId)).thenReturn(Optional.of(game));
        when(userGameRepository.save(any(UserGame.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserGame saved = userGameService.add(igdbId, user);

        assertEquals(user.getUsername(), saved.getUser().getUsername());
        assertEquals(igdbId, saved.getGame().getIgdbId());
        verify(userGameRepository).save(any(UserGame.class));
    }

    @Test
    public void add_shouldThrowException_whenGameAlreadyExistsForUser() {
        Long igdbId = 1979L;

        when(userGameRepository.findUserGameByGame_IgdbIdAndUser_Username(igdbId, user.getUsername()))
                .thenReturn(Optional.of(new UserGame()));

        assertThrows(EntityExistsException.class, () -> userGameService.add(igdbId, user));
    }

    @Test
    public void add_shouldCallGameServiceAdd_whenGameNotInRepository() {
        Long igdbId = 1979L;
        Game newGame = new Game(new GameForm(
                igdbId, "Battlefield 4", "//images.igdb.com/igdb/image/upload/t_thumb/co1nmf.jpg",
                "Battlefield 4 is the genre-defining action blockbuster created by Dice", Enums.categoryIGDB.main_game
        ));

        when(userGameRepository.findUserGameByGame_IgdbIdAndUser_Username(igdbId, user.getUsername()))
                .thenReturn(Optional.empty());
        when(gameRepository.findById(igdbId)).thenReturn(Optional.empty());
        when(gameService.add(igdbId)).thenReturn(newGame);
        when(userGameRepository.save(any(UserGame.class))).thenAnswer(i -> i.getArgument(0));

        UserGame userGame = userGameService.add(igdbId, user);

        assertEquals(igdbId, userGame.getGame().getIgdbId());
        verify(gameService).add(igdbId);
    }

    @Test
    public void add_shouldThrowException_whenGameNotFoundInAPI() {
        Long igdbId = 1979L;

        when(userGameRepository.findUserGameByGame_IgdbIdAndUser_Username(igdbId, user.getUsername()))
                .thenReturn(Optional.empty());
        when(gameRepository.findById(igdbId)).thenReturn(Optional.empty());
        when(gameService.add(igdbId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> userGameService.add(igdbId, user));
    }

    @Test
    public void delete_shouldThrowException_whenUserGameNotFoundInRepository() {
        assertThrows(IllegalArgumentException.class, () ->  userGameService.delete(1979L, user));
    }
}
