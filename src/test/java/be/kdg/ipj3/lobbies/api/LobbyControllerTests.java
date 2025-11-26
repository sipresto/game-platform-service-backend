//package be.kdg.ipj3.lobbies.api;
//
//import be.kdg.ipj3.catalog.application.GameService;
//import be.kdg.ipj3.lobbies.api.dto.GameStartDto;
//import be.kdg.ipj3.lobbies.application.LobbyService;
//import be.kdg.ipj3.lobbies.domain.exception.GameServiceUnavailableException;
//import be.kdg.ipj3.lobbies.domain.lobby.Difficulty;
//import be.kdg.ipj3.shared.domain.GameId;
//import be.kdg.ipj3.shared.domain.UserId;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//import java.util.UUID;
//
//import static org.mockito.BDDMockito.given;
//import static org.mockito.BDDMockito.then;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@AutoConfigureMockMvc
//@SpringBootTest
//public class LobbyControllerTests {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockitoBean
//    private LobbyService lobbyService;
//
//    @MockitoBean
//    private GameService gameService;
//
//    @Test
//    void startGame_shouldReturnGameUrl_whenGameServiceIsAvailable() throws Exception {
//        // Arrange
//        UUID gameId = UUID.randomUUID();
//        UUID player1 = UUID.randomUUID();
//        UUID player2 = UUID.randomUUID();
//        UUID lobbyId = UUID.randomUUID();
//        Difficulty difficulty = Difficulty.EASY;
//
//        GameStartDto dto = new GameStartDto(gameId, List.of(player1, player2), difficulty);
//        String serviceUrl = "http://test.com/service";
//        String gameUrl = "http://test.com/game";
//
//        given(gameService.findServiceByGameId(new GameId(gameId))).willReturn(serviceUrl);
//        given(lobbyService.startGame(serviceUrl, new UserId(player1), difficulty))
//                .willReturn(gameUrl);
//
//        // Act & Assert
//        mockMvc.perform(post("/api/lobby")
//                        .with(jwt())
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json"))
//                .andExpect(jsonPath("$.url").value(gameUrl));
//
//        then(gameService).should().findServiceByGameId(new GameId(gameId));
//        then(lobbyService).should().startGame(serviceUrl, List.of(new UserId(player1), new UserId(player2)), difficulty);
//    }
//
//    @Test
//    void startGame_shouldReturnServiceUnavailable_whenGameServiceFails() throws Exception {
//        // Arrange
//        UUID gameId = UUID.randomUUID();
//        UUID player = UUID.randomUUID();
//        Difficulty difficulty = Difficulty.EASY;
//
//        GameStartDto dto = new GameStartDto(gameId, List.of(player), difficulty);
//
//        given(gameService.findServiceByGameId(new GameId(gameId)))
//                .willThrow(new GameServiceUnavailableException("Game service is down"));
//
//        // Act & Assert
//        mockMvc.perform(post("/api/lobby")
//                        .with(jwt())
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isServiceUnavailable())
//                .andExpect(content().contentType("application/json"))
//                .andExpect(jsonPath("$.message").value("Game service is down"));
//
//        then(gameService).should().findServiceByGameId(new GameId(gameId));
//    }
//
//    @Test
//    void startGame_shouldReturnBadRequest_whenInvalidDtoProvided() throws Exception {
//        // Arrange
//        UUID gameId = UUID.randomUUID();
//        Difficulty difficulty = Difficulty.HARD;
//
//        GameStartDto dto = new GameStartDto(gameId, List.of(), difficulty);
//
//        // Act & Assert
//        mockMvc.perform(post("/api/lobby")
//                        .with(jwt())
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest());
//    }
//}
