package be.kdg.ipj3.catalog.api.gameController;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.IGameRepository;
import be.kdg.ipj3.shared.domain.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
public class GetAllGamesTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IGameRepository gameRepository;

    @BeforeEach
    void setUp() {
        gameRepository.deleteAll();
    }

    @Test
    void getAllGames_shouldReturnListOfGames_whenGamesExist() throws Exception {
        // Arrange
        Game game1 = new Game(
                "Test Game",
                "Test Description",
                Money.of(BigDecimal.valueOf(49.99)),
                "http://test.com/service",
                List.of(Category.CARDGAME, Category.NATIVE, Category.STRATEGY)
        );

        Game game2 = new Game(
                "Test Game 2",
                "Test Description 2",
                Money.of(BigDecimal.valueOf(39.99)),
                "http://test.com/service2",
                List.of(Category.BOARDGAME)
        );

        gameRepository.save(game1);
        gameRepository.save(game2);

        // Act & Assert
        mockMvc.perform(get("/api/games"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Test Game"))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].price").value(49.99))
                .andExpect(jsonPath("$[0].categories", hasItem("CARDGAME")))
                .andExpect(jsonPath("$[0].categories", hasItem("NATIVE")))
                .andExpect(jsonPath("$[0].categories", hasItem("STRATEGY")))
                .andExpect(jsonPath("$[1].name").value("Test Game 2"));
    }

    @Test
    void getAllGames_shouldReturnEmptyList_whenNoGamesExist() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/games"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
