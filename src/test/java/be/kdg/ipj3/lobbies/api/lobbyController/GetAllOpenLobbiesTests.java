package be.kdg.ipj3.lobbies.api.lobbyController;

import be.kdg.ipj3.lobbies.application.LobbyService;
import be.kdg.ipj3.lobbies.domain.lobby.Lobby;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class GetAllOpenLobbiesTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LobbyService lobbyService;

    private JwtRequestPostProcessor validJwt() {
        return jwt().jwt(builder -> builder
                .subject("keycloak-id")
                .claim("preferred_username", "test-user"));
    }

    @Test
    void getAllOpenLobbies_shouldReturnListOfLobbies_whenLobbiesExist() throws Exception {
        // Arrange
        Lobby lobby1 = new Lobby(
                "pass",
                4,
                GameId.create(),
                UserId.create()
        );

        Lobby lobby2 = new Lobby(
                "",
                2,
                GameId.create(),
                UserId.create()
        );

        List<Lobby> lobbies = List.of(lobby1, lobby2);

        given(lobbyService.findAllOpenLobbies()).willReturn(lobbies);

        // Act & Assert
        mockMvc.perform(get("/api/lobbies")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].currentPlayerCount").value(1))
                .andExpect(jsonPath("$[0].isPrivate").value(true))
                .andExpect(jsonPath("$[1].isPrivate").value(false));

        then(lobbyService).should().findAllOpenLobbies();
    }

    @Test
    void getAllOpenLobbies_shouldReturnEmptyList_whenNoLobbiesExist() throws Exception {
        // Arrange
        given(lobbyService.findAllOpenLobbies()).willReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/lobbies")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        then(lobbyService).should().findAllOpenLobbies();
    }

    @Test
    void getAllOpenLobbies_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/lobbies"))
                .andExpect(status().isUnauthorized());
    }
}
