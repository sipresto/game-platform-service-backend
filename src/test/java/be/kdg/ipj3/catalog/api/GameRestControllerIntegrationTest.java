package be.kdg.ipj3.catalog.api;

import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.shared.domain.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameRestController.class)
class GameRestControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameService gameService;

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
}
