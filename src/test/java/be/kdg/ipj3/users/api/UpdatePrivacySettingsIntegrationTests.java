package be.kdg.ipj3.users.api;

import be.kdg.ipj3.BaseIntegrationTest;
import be.kdg.ipj3.users.domain.Visibility;
import be.kdg.ipj3.users.domain.VisibleField;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UpdatePrivacySettingsIntegrationTests extends BaseIntegrationTest {

    @Test
    void updatePrivacyTemplate_shouldSucceed_whenValidVisibility() throws Exception {
        // Arrange
        String keycloakId = "test-keycloak-id";
        String username = "testuser";
        createTestUser(keycloakId, username, "Test@gmail.com");

        // Act
        mockMvc.perform(put("/api/platform/users/privacy")
                        .with(validJwt(keycloakId, username))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"FRIENDS_ONLY\""))
                .andExpect(status().isOk());

        // Assert
        var user = userRepository.findByKeycloakId(keycloakId).orElseThrow();
        var settings = user.getVisibilitySettings();

        assertThat(settings.getVisibility(VisibleField.PROFILE)).isEqualTo(Visibility.PUBLIC);
        assertThat(settings.getVisibility(VisibleField.POINTS)).isEqualTo(Visibility.FRIENDS_ONLY);
        assertThat(settings.getVisibility(VisibleField.FAVORITE_GAMES)).isEqualTo(Visibility.FRIENDS_ONLY);
        assertThat(settings.getVisibility(VisibleField.OWNED_GAMES)).isEqualTo(Visibility.FRIENDS_ONLY);
        assertThat(settings.getVisibility(VisibleField.FRIENDS_LIST)).isEqualTo(Visibility.FRIENDS_ONLY);
    }

    @Test
    void updatePrivacyTemplate_shouldReturnBadRequest_whenInvalidVisibility() throws Exception {
        // Arrange
        String keycloakId = "test-keycloak-id";
        String username = "testuser";
        createTestUser(keycloakId, username, "Test@gmail.com");

        // Act & Assert
        mockMvc.perform(put("/api/platform/users/privacy")
                        .with(validJwt(keycloakId, username))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"INVALID_VISIBILITY\""))
                .andExpect(status().isBadRequest());
    }
}