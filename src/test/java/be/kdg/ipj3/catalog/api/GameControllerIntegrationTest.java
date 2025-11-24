package be.kdg.ipj3.catalog.api;

import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.catalog.domain.Achievement;
import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.Money;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
@AutoConfigureMockMvc(addFilters = false)
class GameControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameService gameService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllGamesIntegration() throws Exception {
        // Arrange
        Game game = new Game("Tic-Tac-Toe", "Description", Money.of(BigDecimal.valueOf(1.99)), List.of(Category.NATIVE, Category.BOARDGAME));
        when(gameService.getAllGames()).thenReturn(List.of(game));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Tic-Tac-Toe"))
                .andExpect(jsonPath("$[0].description").value("Description"))
                .andExpect(jsonPath("$[0].price").value(1.99))
                .andExpect(jsonPath("$[0].categories[0]").value("NATIVE"))
                .andExpect(jsonPath("$[0].categories[1]").value("BOARDGAME"));
    }

    @Test
    void testGetAllGamesIntegration_NoGamesFound() throws Exception {
        // Arrange
        when(gameService.getAllGames()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/games"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFilterGames_ByCategories() throws Exception {
        // Arrange
        Game game = new Game(
                "Poker",
                "Card game",
                Money.of(BigDecimal.valueOf(5.99)),
                List.of(Category.CARDGAME)
        );
        GameFilterRequest filterRequest = new GameFilterRequest(
                null,
                null,
                List.of(Category.CARDGAME)
        );

        when(gameService.filterGames(any(), any(), anyList()))
                .thenReturn(List.of(game));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/games/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Poker"))
                .andExpect(jsonPath("$[0].categories[0]").value("CARDGAME"));
    }

    @Test
    void testFilterGames_CombinedFilters() throws Exception {
        // Arrange
        Game game = new Game(
                GameId.create(),
                "Strategy Board Game",
                "Expensive strategy board game",
                Money.of(BigDecimal.valueOf(49.99)),
                List.of(new Achievement("Master", "Complete all levels", 50, GameId.create())),
                List.of(Category.BOARDGAME, Category.STRATEGY)
        );
        GameFilterRequest filterRequest = new GameFilterRequest(
                Money.of(BigDecimal.valueOf(40.00)),
                Money.of(BigDecimal.valueOf(60.00)),
                List.of(Category.BOARDGAME, Category.STRATEGY)
        );

        when(gameService.filterGames(any(Money.class), any(Money.class), anyList()))
                .thenReturn(List.of(game));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/games/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Strategy Board Game"))
                .andExpect(jsonPath("$[0].price").value(49.99))
                .andExpect(jsonPath("$[0].categories").isArray())
                .andExpect(jsonPath("$[0].categories.length()").value(2));
    }

    @Test
    void testFilterGames_NoMatchingGames() throws Exception {
        // Arrange
        GameFilterRequest filterRequest = new GameFilterRequest(
                Money.of(BigDecimal.valueOf(100.00)),
                null,
                List.of(Category.EXTERNAL)
        );

        when(gameService.filterGames(any(Money.class), any(), anyList()))
                .thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/games/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFilterGames_WithEmptyCategories() throws Exception {
        // Arrange
        Game game = new Game("Any Game", "Description", Money.of(BigDecimal.valueOf(12.99)), List.of(Category.STRATEGY));
        GameFilterRequest filterRequest = new GameFilterRequest(
                null,
                null,
                List.of()
        );

        when(gameService.filterGames(any(), any(), anyList()))
                .thenReturn(List.of(game));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/games/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Any Game"));
    }

    @Test
    void testFilterGames_WithMultipleGames() throws Exception {
        // Arrange
        Game game1 = new Game("Chess", "Classic game", Money.of(BigDecimal.valueOf(15.99)), List.of(Category.BOARDGAME));
        Game game2 = new Game("Checkers", "Simple game", Money.of(BigDecimal.valueOf(8.99)), List.of(Category.BOARDGAME));

        GameFilterRequest filterRequest = new GameFilterRequest(
                null,
                Money.of(BigDecimal.valueOf(20.00)),
                List.of(Category.BOARDGAME)
        );

        when(gameService.filterGames(any(), any(Money.class), anyList()))
                .thenReturn(List.of(game1, game2));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/games/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Chess"))
                .andExpect(jsonPath("$[1].name").value("Checkers"));
    }

    @Test
    void testFilterGames_WithSingleCategory() throws Exception {
        // Arrange
        Game nativeGame = new Game("Native App", "Native application", Money.of(BigDecimal.valueOf(14.99)), List.of(Category.NATIVE));
        GameFilterRequest filterRequest = new GameFilterRequest(
                null,
                null,
                List.of(Category.NATIVE)
        );

        when(gameService.filterGames(any(), any(), anyList()))
                .thenReturn(List.of(nativeGame));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/games/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Native App"))
                .andExpect(jsonPath("$[0].categories[0]").value("NATIVE"));
    }

    @Test
    void testFilterGames_WithMixedCategoryTypes() throws Exception {
        // Arrange
        Game mixedGame = new Game("Mixed Game", "Mixed categories", Money.of(BigDecimal.valueOf(22.99)),
                List.of(Category.BOARDGAME, Category.STRATEGY, Category.NATIVE));
        GameFilterRequest filterRequest = new GameFilterRequest(
                null,
                null,
                List.of(Category.BOARDGAME, Category.NATIVE)
        );

        when(gameService.filterGames(any(), any(), anyList()))
                .thenReturn(List.of(mixedGame));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/games/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Mixed Game"))
                .andExpect(jsonPath("$[0].categories.length()").value(3));
    }

    @Test
    void testFilterGames_EmptyRequestBody() throws Exception {
        // Arrange
        when(gameService.filterGames(any(), any(), anyList()))
                .thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/games/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFilterGames_WithOnlyPriceFilters() throws Exception {
        // Arrange
        Game game = new Game("Price Filtered Game", "Description", Money.of(BigDecimal.valueOf(25.99)), List.of(Category.STRATEGY));
        GameFilterRequest filterRequest = new GameFilterRequest(
                Money.of(BigDecimal.valueOf(20.00)),
                Money.of(BigDecimal.valueOf(30.00)),
                null
        );

        when(gameService.filterGames(any(Money.class), any(Money.class), any()))
                .thenReturn(List.of(game));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/games/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Price Filtered Game"));
    }

    @Test
    void testFilterGames_WithOnlyMinPrice() throws Exception {
        // Arrange
        Game game = new Game("Min Price Game", "Description", Money.of(BigDecimal.valueOf(35.99)), List.of(Category.EXTERNAL));
        GameFilterRequest filterRequest = new GameFilterRequest(
                Money.of(BigDecimal.valueOf(30.00)),
                null,
                null
        );

        when(gameService.filterGames(any(Money.class), any(), any()))
                .thenReturn(List.of(game));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/games/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Min Price Game"));
    }

    @Test
    void testFilterGames_WithOnlyMaxPrice() throws Exception {
        // Arrange
        Game game = new Game("Max Price Game", "Description", Money.of(BigDecimal.valueOf(8.99)), List.of(Category.BOARDGAME));
        GameFilterRequest filterRequest = new GameFilterRequest(
                null,
                Money.of(BigDecimal.valueOf(10.00)),
                null
        );

        when(gameService.filterGames(any(), any(Money.class), any()))
                .thenReturn(List.of(game));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/games/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Max Price Game"));
    }

    @Test
    void testGetAllGames_ServiceException() throws Exception {
        // Arrange
        when(gameService.getAllGames()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/games"))
                .andExpect(status().is5xxServerError());
    }
}