package be.kdg.ipj3.catalog.api.gameController;

import be.kdg.ipj3.catalog.api.dto.GameFilterRequestDto;
import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.shared.domain.Money;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class GetAllGamesByFilterTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GameService gameService;

    @Test
    void filterGames_shouldReturnFilteredGames_whenMatchesExist() throws Exception {
        // Arrange
        Game game = new Game(
                "Filtered Game",
                "Filtered Description",
                Money.of(BigDecimal.valueOf(20.00)),
                "http://test.com/service",
                List.of(Category.CARDGAME)
        );

        List<Game> games = List.of(game);

        GameFilterRequestDto filter = new GameFilterRequestDto(
                Money.of(BigDecimal.valueOf(10)),
                Money.of(BigDecimal.valueOf(30)),
                List.of(Category.CARDGAME)
        );

        given(gameService.filterGames(any(), any(), any())).willReturn(games);

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

        then(gameService).should().filterGames(filter.minPrice(), filter.maxPrice(), filter.categories());
    }

    @Test
    void filterGames_shouldReturnEmptyList_whenNoMatchesExist() throws Exception {
        // Arrange
        GameFilterRequestDto filter = new GameFilterRequestDto(
                Money.of(BigDecimal.valueOf(100)),
                Money.of(BigDecimal.valueOf(200)),
                List.of(Category.BOARDGAME)
        );

        given(gameService.filterGames(any(), any(), any())).willReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(post("/api/games/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        then(gameService).should().filterGames(filter.minPrice(), filter.maxPrice(), filter.categories());
    }
}
