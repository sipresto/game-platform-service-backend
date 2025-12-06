package be.kdg.ipj3.catalog.api.gameController;

import be.kdg.ipj3.catalog.api.dto.GameFilterRequestDto;
import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.GameRepository;
import be.kdg.ipj3.shared.domain.Money;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
public class GetAllGamesByFilterTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GameRepository gameRepository;

    @BeforeEach
    void setUp() {
        gameRepository.deleteAll();
    }

    @Test
    void filterGames_shouldReturnFilteredGames_whenMatchesExist() throws Exception {
        // Arrange
        Game game = new Game(
                "Filtered Game",
                "Filtered Description",
                Money.of(BigDecimal.valueOf(20.00)),
                "http://test.com/service",
                "A rule",
                List.of(Category.CARDGAME)
        );
        gameRepository.save(game);

        GameFilterRequestDto filter = new GameFilterRequestDto(
                Money.of(BigDecimal.valueOf(10)),
                Money.of(BigDecimal.valueOf(30)),
                List.of(Category.CARDGAME)
        );

        // Act & Assert
        mockMvc.perform(post("/api/games/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Filtered Game"))
                .andExpect(jsonPath("$[0].description").value("Filtered Description"))
                .andExpect(jsonPath("$[0].price").value(20.00))
                .andExpect(jsonPath("$[0].categories[0]").value("CARDGAME"));
    }

    @Test
    void filterGames_shouldReturnEmptyList_whenNoMatchesExist() throws Exception {
        // Arrange
        GameFilterRequestDto filter = new GameFilterRequestDto(
                Money.of(BigDecimal.valueOf(100)),
                Money.of(BigDecimal.valueOf(200)),
                List.of(Category.BOARDGAME)
        );

        // Act & Assert
        mockMvc.perform(post("/api/games/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
