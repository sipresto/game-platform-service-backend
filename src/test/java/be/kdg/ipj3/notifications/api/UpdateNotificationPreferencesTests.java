package be.kdg.ipj3.notifications.api;

import be.kdg.ipj3.notifications.api.dto.UpdatePreferenceDto;
import be.kdg.ipj3.notifications.domain.NotificationChannel;
import be.kdg.ipj3.notifications.domain.NotificationRepository;
import be.kdg.ipj3.notifications.domain.UserNotificationPreferences;
import be.kdg.ipj3.notifications.domain.UserNotificationPreferencesRepository;
import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.UserCosmetics;
import be.kdg.ipj3.users.domain.UserRepository;
import be.kdg.ipj3.users.shared.UserId;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserNotificationPreferencesRepository preferencesRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        notificationRepository.deleteAll();
        preferencesRepository.deleteAll();
    }

    private JwtRequestPostProcessor validJwt() {
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
                List.of(),
                new UserCosmetics(),
                0,
                new Profile("username", "Bio"),
                "Test@gmail.com"
        );
        userRepository.save(user);
        return userId;
    }

    private UpdatePreferenceDto createValidUpdatePreferenceDto() {
        return new UpdatePreferenceDto(NotificationType.FRIEND_REQUEST, NotificationChannel.EMAIL, false);
    }

    private UpdatePreferenceDto createUpdatePreferenceDto(NotificationType notificationType,
                                                          NotificationChannel channel,
                                                          boolean enabled) {
        return new UpdatePreferenceDto(notificationType, channel, enabled);
    }

    @Test
    void updatePreference_shouldUpdateUserPreference() throws Exception {
        // Arrange
        UserId userId = createUser();
        UpdatePreferenceDto requestDto = createValidUpdatePreferenceDto();
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // Act & Assert
        mockMvc.perform(put("/api/platform/notifications/preferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(validJwt()))
                .andExpect(status().isOk());

        Optional<UserNotificationPreferences> preferences = preferencesRepository.findByUserId(userId);
        assertTrue(preferences.isPresent());

        UserNotificationPreferences savedPreferences = preferences.get();
        boolean isEmailEnabled = savedPreferences.shouldSendViaChannel(NotificationType.FRIEND_REQUEST,
                NotificationChannel.EMAIL);
        assertFalse(isEmailEnabled);
    }

    @Test
    void updatePreference_shouldReturnBadRequest_whenNotificationTypeIsInvalid() throws Exception {
        // Arrange
        createUser();

        String invalidRequestBody = """
            {
                "notificationType": "INVALID_TYPE",
                "channel": "EMAIL",
                "enabled": false
            }
            """;

        // Act & Assert
        mockMvc.perform(put("/api/platform/notifications/preferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody)
                        .with(validJwt()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePreference_shouldReturnBadRequest_whenChannelIsInvalid() throws Exception {
        // Arrange
        createUser();

        String invalidRequestBody = """
            {
                "notificationType": "FRIEND_REQUEST",
                "channel": "INVALID_CHANNEL",
                "enabled": false
            }
            """;

        // Act & Assert
        mockMvc.perform(put("/api/platform/notifications/preferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody)
                        .with(validJwt()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePreference_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        // Arrange
        UpdatePreferenceDto requestDto = createValidUpdatePreferenceDto();
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // Act & Assert
        mockMvc.perform(put("/api/platform/notifications/preferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(validJwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePreference_shouldReturnUnauthorized_whenUserNotLoggedIn() throws Exception {
        // Arrange
        createUser();
        UpdatePreferenceDto requestDto = createValidUpdatePreferenceDto();
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // Act & Assert
        mockMvc.perform(put("/api/platform/notifications/preferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updatePreference_shouldHandleAllNotificationTypes() throws Exception {
        // Arrange
        UserId userId = createUser();

        for (NotificationType type : NotificationType.values()) {
            for (NotificationChannel channel : NotificationChannel.values()) {
                UpdatePreferenceDto requestDto = createUpdatePreferenceDto(type, channel, false);
                String requestBody = objectMapper.writeValueAsString(requestDto);

                // Act & Assert
                mockMvc.perform(put("/api/platform/notifications/preferences")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                                .with(validJwt()))
                        .andExpect(status().isOk());
            }
        }

        Optional<UserNotificationPreferences> preferences = preferencesRepository.findByUserId(userId);
        assertTrue(preferences.isPresent());
    }

    @Test
    void updatePreference_shouldTogglePreference() throws Exception {
        // Arrange
        UserId userId = createUser();

        UpdatePreferenceDto disableDto = createUpdatePreferenceDto(
                NotificationType.ACHIEVEMENT,
                NotificationChannel.IN_APP,
                false
        );
        String disableRequestBody = objectMapper.writeValueAsString(disableDto);

        mockMvc.perform(put("/api/platform/notifications/preferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(disableRequestBody)
                        .with(validJwt()))
                .andExpect(status().isOk());

        UpdatePreferenceDto enableDto = createUpdatePreferenceDto(
                NotificationType.ACHIEVEMENT,
                NotificationChannel.IN_APP,
                true
        );
        String enableRequestBody = objectMapper.writeValueAsString(enableDto);

        mockMvc.perform(put("/api/platform/notifications/preferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(enableRequestBody)
                        .with(validJwt()))
                .andExpect(status().isOk());

        Optional<UserNotificationPreferences> preferences = preferencesRepository.findByUserId(userId);
        assertTrue(preferences.isPresent());

        UserNotificationPreferences savedPreferences = preferences.get();
        boolean isEnabled = savedPreferences.shouldSendViaChannel(
                NotificationType.ACHIEVEMENT,
                NotificationChannel.IN_APP
        );
        assertTrue(isEnabled);
    }

    @Test
    void updatePreference_shouldWorkWithDifferentChannelsForSameType() throws Exception {
        // Arrange
        UserId userId = createUser();

        UpdatePreferenceDto emailDto = createUpdatePreferenceDto(
                NotificationType.FRIEND_REQUEST,
                NotificationChannel.EMAIL,
                false
        );
        String emailRequestBody = objectMapper.writeValueAsString(emailDto);

        mockMvc.perform(put("/api/platform/notifications/preferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emailRequestBody)
                        .with(validJwt()))
                .andExpect(status().isOk());

        UpdatePreferenceDto inAppDto = createUpdatePreferenceDto(
                NotificationType.FRIEND_REQUEST,
                NotificationChannel.IN_APP,
                true
        );
        String inAppRequestBody = objectMapper.writeValueAsString(inAppDto);

        mockMvc.perform(put("/api/platform/notifications/preferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inAppRequestBody)
                        .with(validJwt()))
                .andExpect(status().isOk());

        Optional<UserNotificationPreferences> preferences = preferencesRepository.findByUserId(userId);
        assertTrue(preferences.isPresent());

        UserNotificationPreferences savedPreferences = preferences.get();
        boolean emailEnabled = savedPreferences.shouldSendViaChannel(
                NotificationType.FRIEND_REQUEST,
                NotificationChannel.EMAIL
        );
        boolean inAppEnabled = savedPreferences.shouldSendViaChannel(
                NotificationType.FRIEND_REQUEST,
                NotificationChannel.IN_APP
        );

        assertFalse(emailEnabled);
        assertTrue(inAppEnabled);
    }
}