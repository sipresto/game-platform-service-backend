package be.kdg.ipj3.notifications.api;

import be.kdg.ipj3.notifications.domain.NotificationRepository;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.UserRepository;
import be.kdg.ipj3.users.shared.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
public class GetNotificationPreferencesTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        notificationRepository.deleteAll();
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt() {
        return jwt().authorities(() -> "ROLE_PLAYER")
                .jwt(builder -> builder
                        .subject("keycloakId")
                        .claim("preferred_username", "username"));
    }

    private void createUser() {
        UserId userId = UserId.create();
        User user = new User(
                userId,
                "keycloakId",
                "username",
                List.of(),
                0,
                new Profile("username", "Bio"),
                "Test@gmail.com"
        );
        userRepository.save(user);
    }

    @Test
    void getPreferences_shouldReturnUserPreferences() throws Exception {
        // Arrange
        createUser();

        // Act & Assert
        mockMvc.perform(get("/api/notifications/preferences")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.FRIEND_REQUEST.IN_APP").value(true))
                .andExpect(jsonPath("$.FRIEND_REQUEST.EMAIL").value(true))
                .andExpect(jsonPath("$.ACHIEVEMENT.IN_APP").value(true))
                .andExpect(jsonPath("$.ACHIEVEMENT.EMAIL").value(true))
                .andExpect(jsonPath("$.YOUR_TURN.IN_APP").value(false))
                .andExpect(jsonPath("$.YOUR_TURN.EMAIL").value(false));
    }

    @Test
    void getPreferences_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        // Arrange - No user is created

        // Act & Assert
        mockMvc.perform(get("/api/notifications/preferences")
                        .with(validJwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPreferences_shouldReturnNotFound_whenJwtIsInvalid() throws Exception {
        // Arrange
        createUser();

        // Act & Assert
        mockMvc.perform(get("/api/notifications/preferences")
                        .with(jwt().authorities(() -> "ROLE_PLAYER")
                                .jwt(builder -> builder
                                        .subject("differentKeycloakId")
                                        .claim("preferred_username", "username"))))
                .andExpect(status().isNotFound());
    }
}