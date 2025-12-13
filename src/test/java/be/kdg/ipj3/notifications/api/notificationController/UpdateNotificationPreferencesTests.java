package be.kdg.ipj3.notifications.api.notificationController;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
public class UpdateNotificationPreferencesTests {

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

    @Test
    void updatePreference_shouldUpdateUserPreference() throws Exception {
        // Arrange
        UserId userId = UserId.create();
        User user = new User(
                userId,
                "keycloakId",
                "username",
                List.of(),
                0,
                new Profile("username", "Bio")
        );
        userRepository.save(user);

        // Act & Assert
        mockMvc.perform(put("/api/notifications/preferences/FRIEND_REQUEST/EMAIL")
                        .param("enabled", "false")
                        .with(validJwt()))
                .andExpect(status().isOk());
    }

    @Test
    void updatePreference_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        // Arrange

        // Act & Assert
        mockMvc.perform(put("/api/notifications/preferences/FRIEND_REQUEST/EMAIL")
                        .param("enabled", "false")
                        .with(validJwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePreference_shouldReturnBadRequest_whenNotificationTypeIsInvalid() throws Exception {
        // Arrange
        UserId userId = UserId.create();
        User user = new User(
                userId,
                "keycloakId",
                "username",
                List.of(),
                0,
                new Profile("username", "Bio")
        );
        userRepository.save(user);

        // Act & Assert
        mockMvc.perform(put("/api/notifications/preferences/INVALID_TYPE/EMAIL")
                        .param("enabled", "false")
                        .with(validJwt()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePreference_shouldReturnBadRequest_whenChannelIsInvalid() throws Exception {
        // Arrange
        UserId userId = UserId.create();
        User user = new User(
                userId,
                "keycloakId",
                "username",
                List.of(),
                0,
                new Profile("username", "Bio")
        );
        userRepository.save(user);

        // Act & Assert
        mockMvc.perform(put("/api/notifications/preferences/FRIEND_REQUEST/INVALID_CHANNEL")
                        .param("enabled", "false")
                        .with(validJwt()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePreference_shouldReturnNotFound_whenJwtIsInvalid() throws Exception {
        // Arrange
        UserId userId = UserId.create();
        User user = new User(
                userId,
                "keycloakId",
                "username",
                List.of(),
                0,
                new Profile("username", "Bio")
        );
        userRepository.save(user);

        // Act & Assert
        mockMvc.perform(put("/api/notifications/preferences/FRIEND_REQUEST/EMAIL")
                        .param("enabled", "false")
                        .with(jwt().authorities(() -> "ROLE_PLAYER")
                                .jwt(builder -> builder
                                        .subject("differentKeycloakId")
                                        .claim("preferred_username", "username"))))
                .andExpect(status().isNotFound());
    }
}
