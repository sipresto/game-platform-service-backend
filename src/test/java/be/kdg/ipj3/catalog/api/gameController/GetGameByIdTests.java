package be.kdg.ipj3.catalog.api.gameController;

import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.Money;
import be.kdg.ipj3.shared.domain.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
public class GetGameByIdTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameService gameService;

    @Test
    void getGameById_shouldReturnGame_whenGameExists() throws Exception {
        // Arrange
        GameId gameId = GameId.create();

        Game game = new Game(
                "Test Game",
                "Test Description",
                Money.of(BigDecimal.valueOf(49.99)),
                "http://test.com/service",
                List.of(Category.CARDGAME)
        );

        given(gameService.findGameById(any())).willReturn(game);

        // Act & Assert
        mockMvc.perform(get("/api/games/" + gameId.id()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Game"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.price").value(49.99))
                .andExpect(jsonPath("$.categories[0]").value("CARDGAME"));

        then(gameService).should().findGameById(gameId);
    }

    @Test
    void getGameById_shouldReturnNotFound_whenGameDoesNotExist() throws Exception {
        // Arrange
        GameId gameId = GameId.create();

        given(gameService.findGameById(any()))
                .willThrow(new NotFoundException("Game not found"));

        // Act & Assert
        mockMvc.perform(get("/api/games/" + gameId.id()))
                .andExpect(status().isNotFound());

        then(gameService).should().findGameById(gameId);
    }
}
