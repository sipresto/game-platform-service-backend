package be.kdg.ipj3.notifications.api.notificationController;

import be.kdg.ipj3.notifications.domain.Notification;
import be.kdg.ipj3.notifications.domain.NotificationId;
import be.kdg.ipj3.notifications.domain.NotificationRepository;
import be.kdg.ipj3.notifications.shared.NotificationType;
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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
public class MarkNotificationAsReadTests {

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
    void readNotification_shouldMarkNotificationAsRead() throws Exception {
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

        NotificationId notificationId = NotificationId.create();
        Notification notification = new Notification(
                notificationId,
                "Test notification",
                NotificationType.FRIEND_REQUEST,
                Instant.now(),
                false,
                userId
        );
        notificationRepository.save(notification);

        // Act & Assert
        mockMvc.perform(patch("/api/notifications/{id}/read", notificationId.id().toString())
                        .with(validJwt()))
                .andExpect(status().isOk());
    }

    @Test
    void readNotification_shouldReturnNotFound_whenNotificationDoesNotExist() throws Exception {
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

        UUID nonExistentNotificationId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(patch("/api/notifications/{id}/read", nonExistentNotificationId.toString())
                        .with(validJwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void readNotification_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        // Arrange - No user is created
        NotificationId notificationId = NotificationId.create();

        // Act & Assert
        mockMvc.perform(patch("/api/notifications/{id}/read", notificationId.id().toString())
                        .with(validJwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void readNotification_shouldReturnNotFound_whenJwtIsInvalid() throws Exception {
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

        NotificationId notificationId = NotificationId.create();
        Notification notification = new Notification(
                notificationId,
                "Test notification",
                NotificationType.FRIEND_REQUEST,
                Instant.now(),
                false,
                userId
        );
        notificationRepository.save(notification);

        // Act & Assert
        mockMvc.perform(patch("/api/notifications/{id}/read", notificationId.id().toString())
                        .with(jwt().authorities(() -> "ROLE_PLAYER")
                                .jwt(builder -> builder
                                        .subject("differentKeycloakId")
                                        .claim("preferred_username", "username"))))
                .andExpect(status().isNotFound());
    }
}
