//package be.kdg.ipj3.notifications.api;
//
//import be.kdg.ipj3.notifications.domain.Notification;
//import be.kdg.ipj3.notifications.domain.NotificationId;
//import be.kdg.ipj3.notifications.domain.NotificationRepository;
//import be.kdg.ipj3.notifications.shared.NotificationType;
//import be.kdg.ipj3.users.domain.Profile;
//import be.kdg.ipj3.users.domain.User;
//import be.kdg.ipj3.users.domain.UserRepository;
//import be.kdg.ipj3.users.shared.UserId;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//import org.testcontainers.utility.TestcontainersConfiguration;
//
//import java.time.Instant;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@AutoConfigureMockMvc
//@SpringBootTest
//@Import(TestcontainersConfiguration.class)
//@TestPropertySource("classpath:application-test.properties")
//public class ReadNotificationTests {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private NotificationRepository notificationRepository;
//
//    @BeforeEach
//    void setup() {
//        userRepository.deleteAll();
//        notificationRepository.deleteAll();
//    }
//
//    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt() {
//        return jwt().authorities(() -> "ROLE_PLAYER")
//                .jwt(builder -> builder
//                        .subject("keycloakId")
//                        .claim("preferred_username", "username"));
//    }
//
//    private UserId createUser() {
//        UserId userId = UserId.create();
//        User user = new User(
//                userId,
//                "keycloakId",
//                "username",
//                List.of(),
//                List.of(),
//                0,
//                new Profile("username", "Bio"),
//                "Test@gmail.com"
//        );
//        userRepository.save(user);
//        return userId;
//    }
//
//    @Test
//    void readNotification_shouldMarkNotificationAsRead() throws Exception {
//        // Arrange
//        UserId userId = createUser();
//
//        NotificationId notificationId = NotificationId.create();
//        Notification notification = new Notification(
//                notificationId,
//                "Test title",
//                "Test notification",
//                NotificationType.FRIEND_REQUEST,
//                Instant.now(),
//                false,
//                userId,
//                null
//        );
//        notificationRepository.save(notification);
//
//        // Act & Assert
//        mockMvc.perform(patch("/api/platform/notifications/{id}/read", notificationId.id().toString())
//                        .with(validJwt()))
//                .andExpect(status().isOk());
//
//        Optional<Notification> updatedNotification = notificationRepository.findById(notificationId);
//        assertTrue(updatedNotification.isPresent() && updatedNotification.get().isRead());
//    }
//
//    @Test
//    void readNotification_shouldReturnNotFound_whenNotificationDoesNotExist() throws Exception {
//        // Arrange
//        createUser();
//
//        UUID nonExistentNotificationId = UUID.randomUUID();
//
//        // Act & Assert
//        mockMvc.perform(patch("/api/platform/notifications/{id}/read", nonExistentNotificationId.toString())
//                        .with(validJwt()))
//                .andExpect(status().isNotFound());
//    }
//
//    // TODO: test when user does not exist
//
//    // TODO: test when user not logged in
//}
