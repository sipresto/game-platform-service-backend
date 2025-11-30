package be.kdg.ipj3.lobbies.api.lobbyController;

import be.kdg.ipj3.lobbies.application.LobbyService;
import be.kdg.ipj3.lobbies.domain.lobby.Lobby;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.application.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
public class GetOpenLobbiesForGameTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LobbyService lobbyService;

    @MockitoBean
    private UserService userService;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt() {
        return jwt().jwt(builder -> builder
                .subject("keycloak-id")
                .claim("preferred_username", "test-user"));
    }

    @Test
    void getOpenLobbiesForGame_shouldReturnListOfLobbies_whenLobbiesExist() throws Exception {
        // Arrange
        GameId gameId = GameId.create();

        Lobby lobby1 = new Lobby(
                "pass",
                4,
                gameId,
                UserId.create()
        );

        List<Lobby> lobbies = List.of(lobby1);

        given(lobbyService.findOpenLobbiesForGame(any())).willReturn(lobbies);

        // Act & Assert
        mockMvc.perform(get("/api/lobbies/{gameId}", gameId.id())
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].currentPlayerCount").value(1))
                .andExpect(jsonPath("$[0].isPrivate").value(true));

        then(lobbyService).should().findOpenLobbiesForGame(gameId);
    }

    @Test
    void getAllOpenLobbies_shouldReturnEmptyList_whenNoLobbiesExist() throws Exception {
        // Arrange
        GameId gameId = GameId.create();

        given(lobbyService.findOpenLobbiesForGame(any())).willReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/lobbies/{gameId}", gameId.id())
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        then(lobbyService).should().findOpenLobbiesForGame(gameId);
    }

    @Test
    void getAllOpenLobbies_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/lobbies/{gameId}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());

        then(lobbyService).shouldHaveNoInteractions();
    }
}
