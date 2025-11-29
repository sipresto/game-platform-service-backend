package be.kdg.ipj3.users.api;

import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.application.UserService;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
public class FindUserTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private be.kdg.ipj3.catalog.application.GameService gameService;

    @Test
    void findUser_shouldReturnUser_whenAuthenticated() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        String keycloakId = "test-keycloak-id";
        String keycloakUsername = "testuser";

        User user = new User(
                new UserId(userId),
                keycloakId,
                keycloakUsername,
                new Profile("Test User", "Test bio")
        );

        given(userService.registerOrGetUser(keycloakId, keycloakUsername))
                .willReturn(user);

        // Act & Assert
        mockMvc.perform(get("/api/user")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject(keycloakId)
                                        .claim("preferred_username", keycloakUsername))
                                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.keycloakId").value(keycloakId))
                .andExpect(jsonPath("$.username").value(keycloakUsername))
                .andExpect(jsonPath("$.profile.displayName").value("Test User"))
                .andExpect(jsonPath("$.profile.bio").value("Test bio"));

        then(userService).should().registerOrGetUser(keycloakId, keycloakUsername);
    }

    @Test
    void findUser_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findUser_shouldReturnForbidden_whenWrongRole() throws Exception {
        String keycloakId = "test-keycloak-id";
        String keycloakUsername = "testuser";

        mockMvc.perform(get("/api/user")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject(keycloakId)
                                        .claim("preferred_username", keycloakUsername))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}