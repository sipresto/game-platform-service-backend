package be.kdg.ipj3.notifications.api;

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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
public class GetAllNotificationsTests {

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

    private UserId createUser() {
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
        return userId;
    }

    @Test
    void getAllNotifications_shouldReturnFilledNotificationDtoList_WhenThereAreNotifications() throws Exception {
        // Arrange
        UserId userId = createUser();

        List<NotificationId> notificationIds = List.of(
                NotificationId.create(),
                NotificationId.create()
        );

        List<Notification> notifications = List.of(
                new Notification(
                        notificationIds.get(0),
                        "X-treme",
                        NotificationType.ACHIEVEMENT,
                        Instant.now(),
                        false,
                        userId
                ),
                new Notification(
                        notificationIds.get(1),
                        "Someone wants to be your friend",
                        NotificationType.FRIEND_REQUEST,
                        Instant.now(),
                        true,
                        userId
                )
        );

        notificationRepository.saveAll(notifications);

        // Act && Assert
        mockMvc.perform(get("/api/notifications")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].notificationId").value(notificationIds.get(0).id().toString()))
                .andExpect(jsonPath("$[0].userId").value(userId.id().toString()))
                .andExpect(jsonPath("$[0].description").value("X-treme"))
                .andExpect(jsonPath("$[0].notificationType").value(NotificationType.ACHIEVEMENT.toString()))
                .andExpect(jsonPath("$[0].read").value(false))
                .andExpect(jsonPath("$[1].notificationId").value(notificationIds.get(1).id().toString()))
                .andExpect(jsonPath("$[1].description").value("Someone wants to be your friend"))
                .andExpect(jsonPath("$[1].notificationType").value(NotificationType.FRIEND_REQUEST.toString()))
                .andExpect(jsonPath("$[1].read").value(true));
    }

    @Test
    void getAllNotifications_shouldReturnEmptyNotificationDtoList_WhenThereAreNoNotifications() throws Exception {
        // Arrange
        createUser();

        // Act && Assert
        mockMvc.perform(get("/api/notifications")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
