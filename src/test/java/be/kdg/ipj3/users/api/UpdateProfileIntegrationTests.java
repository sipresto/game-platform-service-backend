package be.kdg.ipj3.users.api;

import be.kdg.ipj3.users.api.dto.UserDto;
import be.kdg.ipj3.users.domain.*;
import be.kdg.ipj3.users.infrastructure.jpa.JpaProfile;
import be.kdg.ipj3.users.infrastructure.jpa.JpaUserEntity;
import be.kdg.ipj3.users.infrastructure.jpa.JpaUserRepository;
import be.kdg.ipj3.users.shared.UserId;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class UpdateProfileIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    private String createUser(String keycloakId, String username) {
        UUID userId = UUID.randomUUID();
        Map<VisibleField, Visibility> visibility = VisibilitySettings.defaultPublic().fieldVisibilities();

        User user = new User(
                new UserId(userId),
                keycloakId,
                username,
                List.of(),
                List.of(),
                0,
                new Profile(username, "Initial bio", null, null),
                VisibilitySettings.defaultPublic(),
                "Test@gmail.com"
        );
        userRepository.save(user);
        return keycloakId;
    }

    @Test
    void updateProfile_shouldSucceed_whenValidData() throws Exception {
        String keycloakId = createUser("kc-id1", "testuser");
        UserDto.ProfileDto profileDto = new UserDto.ProfileDto("Updated Display Name", "Updated bio");

        mockMvc.perform(put("/api/platform/users/profile")
                        .with(validJwt(keycloakId, "testuser"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileDto)))
                .andExpect(status().isOk());

        Optional<User> opt = userRepository.findByKeycloakId(keycloakId);
        User updated = opt.orElseThrow(
                () -> new AssertionError("User not found in database after profile update")
        );

        assertThat(updated.getProfile().getDisplayName())
                .isEqualTo("Updated Display Name");
        assertThat(updated.getProfile().getBio())
                .isEqualTo("Updated bio");
    }

    @Test
    void updateProfile_shouldReturnBadRequest_whenBlankDisplayName() throws Exception {
        String keycloakId = createUser("kc-id2", "testuser");
        UserDto.ProfileDto profileDto = new UserDto.ProfileDto("", "Updated bio");

        mockMvc.perform(put("/api/platform/users/profile")
                        .with(validJwt(keycloakId, "testuser"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProfile_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        UserDto.ProfileDto profileDto = new UserDto.ProfileDto("Display Name", "Bio");

        mockMvc.perform(put("/api/platform/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateProfile_shouldReturnForbidden_whenWrongRole() throws Exception {
        String keycloakId = createUser("kc-id3", "testuser");
        UserDto.ProfileDto profileDto = new UserDto.ProfileDto("Display Name", "Bio");

        mockMvc.perform(put("/api/platform/users/profile")
                        .with(jwt()
                                .jwt(builder -> builder
                                        .subject(keycloakId)
                                        .claim("preferred_username", "testuser"))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileDto)))
                .andExpect(status().isForbidden());
    }
}
