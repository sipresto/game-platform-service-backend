package be.kdg.ipj3.users.api;

import be.kdg.ipj3.users.domain.*;
import be.kdg.ipj3.users.shared.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class GetUserIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    private JwtRequestPostProcessor validJwt(String subject, String username) {
        return jwt()
                .jwt(builder -> builder
                        .subject(subject)
                        .claim("preferred_username", username))
                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER"));
    }

    private UUID createUser(String keycloakId, String username) {
        UUID userId = UUID.randomUUID();

        User userEntity = new User(
                new UserId(userId),
                keycloakId,
                username,
                List.of(),
                List.of(),
                100,
                new Profile(username, "Test bio", null, null),
                VisibilitySettings.defaultPublic(),
                "Test@gmail.com"
        );
        userRepository.save(userEntity);
        return userId;
    }

    @Test
    void findUser_shouldReturnUser_whenAuthenticated() throws Exception {
        // Arrange
        String keycloakId = "test-keycloak-id";
        String username = "testuser";
        UUID userId = createUser(keycloakId, username);

        // Act & Assert
        mockMvc.perform(get("/api/platform/users/me")
                        .with(validJwt(keycloakId, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.keycloakId").value(keycloakId))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.profile.displayName").value(username))
                .andExpect(jsonPath("$.profile.bio").value("Test bio"));
    }

    @Test
    void findUser_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/platform/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findUser_shouldReturnForbidden_whenWrongRole() throws Exception {
        String keycloakId = "test-keycloak-id";
        String username = "testuser";
        createUser(keycloakId, username);

        // Act & Assert
        mockMvc.perform(get("/api/platform/users/me")
                        .with(jwt()
                                .jwt(builder -> builder
                                        .subject(keycloakId)
                                        .claim("preferred_username", username))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserById_shouldRespectVisibility_whenViewerIsNotOwner() throws Exception {
        // Arrange
        UUID friendId = UUID.randomUUID();
        String friendKeycloakId = "friend-keycloak";
        String keycloakId = "test-keycloak-id";
        String username = "testuser";
        createUser(keycloakId, username);

        Map<VisibleField, Visibility> privateVisibility = VisibilitySettings.defaultPublic().fieldVisibilities();
        privateVisibility.put(VisibleField.PROFILE, Visibility.PRIVATE);
        privateVisibility.put(VisibleField.POINTS, Visibility.PRIVATE);
        var privateVisibilitySettings = new VisibilitySettings(privateVisibility);
        User friend = new User(
                new UserId(friendId),
                friendKeycloakId,
                "friend",
                List.of(),
                List.of(),
                200,
                new Profile("Friend", "Private bio", null, null),
                privateVisibilitySettings,
                "friend@example.com"
        );
        userRepository.save(friend);

        // Act & Assert
        mockMvc.perform(get("/api/platform/users/{userId}", friendId)
                        .with(validJwt(keycloakId, username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("friend"))
                .andExpect(jsonPath("$.profile.bio").doesNotExist())
                .andExpect(jsonPath("$.points").doesNotExist());
    }

    @Test
    void getUserById_shouldShowAllData_whenViewerIsOwner() throws Exception {
        String keycloakId = "test-keycloak-id";
        String username = "testuser";
        UUID userId = createUser(keycloakId, username);

        // Act & Assert
        mockMvc.perform(get("/api/platform/users/{userId}", userId)
                        .with(validJwt(keycloakId, username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.profile.bio").value("Test bio"))
                .andExpect(jsonPath("$.points").value(100));
    }
}