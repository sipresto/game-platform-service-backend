package be.kdg.ipj3.catalog.api.gameController;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.IGameRepository;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class GetGameByIdTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IGameRepository gameRepository;

    @BeforeEach
    void setUp() {
        gameRepository.deleteAll();
    }

    @Test
    void getGameById_shouldReturnGame_whenGameExists() throws Exception {
        // Arrange
        Game game = new Game(
                "Test Game",
                "Test Description",
                Money.of(BigDecimal.valueOf(49.99)),
                "http://test.com/service",
                List.of(Category.CARDGAME, Category.NATIVE, Category.STRATEGY)
        );

        gameRepository.save(game);

        // Act & Assert
        mockMvc.perform(get("/api/games/" + game.getId().id()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Game"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.price").value(49.99))
                .andExpect(jsonPath("$.categories", hasItem("CARDGAME")))
                .andExpect(jsonPath("$.categories", hasItem("NATIVE")))
                .andExpect(jsonPath("$.categories", hasItem("STRATEGY")));
    }

    @Test
    void getGameById_shouldReturnNotFound_whenGameDoesNotExist() throws Exception {
        // Arrange
        GameId gameId = GameId.create();

        // Act & Assert
        mockMvc.perform(get("/api/games/" + gameId.id()))
                .andExpect(status().isNotFound());
    }
}
