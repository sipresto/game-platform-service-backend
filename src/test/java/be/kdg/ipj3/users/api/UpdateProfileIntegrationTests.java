package be.kdg.ipj3.users.api;

import be.kdg.ipj3.BaseIntegrationTest;
import be.kdg.ipj3.users.api.dto.UserDto;
import be.kdg.ipj3.users.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UpdateProfileIntegrationTests extends BaseIntegrationTest {

    @Test
    void updateProfile_shouldSucceed_whenValidData() throws Exception {
        // Arrange
        User user = createTestUser("kc-id1", "testuser", "Test@gmail.com");
        UserDto.ProfileDto profileDto = new UserDto.ProfileDto("Updated Display Name", "Updated bio");

        // Act
        mockMvc.perform(put("/api/platform/users/profile")
                        .with(validJwt(user.getKeycloakId(), "testuser"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileDto)))
                .andExpect(status().isOk());

        // Assert
        User updated = userRepository.findByKeycloakId(user.getKeycloakId()).orElseThrow();

        assertThat(updated.getProfile().displayName())
                .isEqualTo("Updated Display Name");
        assertThat(updated.getProfile().bio())
                .isEqualTo("Updated bio");
    }

    @Test
    void updateProfile_shouldReturnBadRequest_whenBlankDisplayName() throws Exception {
        // Arrange
        User user = createTestUser("kc-id2", "testuser", "Test@gmail.com");
        UserDto.ProfileDto profileDto = new UserDto.ProfileDto("", "Updated bio");

        // Act & Assert
        mockMvc.perform(put("/api/platform/users/profile")
                        .with(validJwt(user.getKeycloakId(), "testuser"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProfile_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Arrange
        UserDto.ProfileDto profileDto = new UserDto.ProfileDto("Display Name", "Bio");

        // Act & Assert
        mockMvc.perform(put("/api/platform/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateProfile_shouldReturnForbidden_whenWrongRole() throws Exception {
        // Arrange
        User user = createTestUser("kc-id3", "testuser", "Test@gmail.com");
        UserDto.ProfileDto profileDto = new UserDto.ProfileDto("Display Name", "Bio");

        // Act & Assert
        mockMvc.perform(put("/api/platform/users/profile")
                        .with(jwt()
                                .jwt(builder -> builder
                                        .subject(user.getKeycloakId())
                                        .claim("preferred_username", "testuser"))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileDto)))
                .andExpect(status().isForbidden());
    }
}