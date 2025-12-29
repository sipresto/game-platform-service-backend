package be.kdg.ipj3.users.api;

import be.kdg.ipj3.users.domain.*;
import be.kdg.ipj3.users.infrastructure.jpa.JpaProfile;
import be.kdg.ipj3.users.infrastructure.jpa.JpaUserEntity;
import be.kdg.ipj3.users.infrastructure.jpa.JpaUserRepository;
import be.kdg.ipj3.users.shared.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static javax.imageio.ImageIO.write;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class RemoveProfileImageIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private final String testEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    private String createUser(String keycloakId, String username, String email) {
        UUID userId = UUID.randomUUID();

        User user = new User(
                new UserId(userId),
                keycloakId,
                username,
                List.of(),
                List.of(),
                0,
                new Profile(username, "Initial bio", null, null),
                VisibilitySettings.defaultPublic(),
                email
        );
        userRepository.save(user);
        return keycloakId;
    }

    private JwtRequestPostProcessor validJwt(String subject) {
        return jwt()
                .jwt(builder -> builder
                        .subject(subject)
                        .claim("preferred_username", "testuser")
                        .claim("email", testEmail))
                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER"));
    }

    private JwtRequestPostProcessor jwtForUser(String keycloakId) {
        return jwt().authorities(() -> "ROLE_PLAYER")
                .jwt(builder -> builder
                        .subject(keycloakId)
                        .claim("preferred_username", "someuser")
                        .claim("email", "some@example.com"));
    }

    private MockMultipartFile createTestImageFile(String filename, String contentType, byte[] content) {
        return new MockMultipartFile(
                "image",
                filename,
                contentType,
                content
        );
    }

    private MockMultipartFile createValidTestImage() throws Exception {
        byte[] pngBytes = createSimplePngImage();
        return createTestImageFile("test.png", "image/png", pngBytes);
    }

    private byte[] createSimplePngImage() throws Exception {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(java.awt.Color.RED);
        g2d.fillRect(0, 0, 100, 100);
        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        write(image, "png", baos);
        return baos.toByteArray();
    }

    private void uploadTestImage(String keycloakId) throws Exception {
        MockMultipartFile imageFile = createValidTestImage();

        mockMvc.perform(multipart("/api/platform/users/me/profile-image")
                        .file(imageFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(validJwt(keycloakId))
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    void removeProfileImage_shouldSucceed_whenAuthenticatedAndHasImage() throws Exception {
        String keycloakId = createUser("kc-id1", "testuser", "Test@gmail.com");
        uploadTestImage(keycloakId);

        Optional<User> userBefore = userRepository.findByKeycloakId(keycloakId);
        assertThat(userBefore).isPresent();
        assertThat(userBefore.get().getProfile().getProfileImage()).isNotNull();

        mockMvc.perform(delete("/api/platform/users/me/delete/profile-image")
                        .with(validJwt(keycloakId)))
                .andExpect(status().isNoContent());

        Optional<User> userAfter = userRepository.findByKeycloakId(keycloakId);
        assertThat(userAfter).isPresent();
        assertThat(userAfter.get().getProfile().getProfileImage()).isNull();
        assertThat(userAfter.get().getProfile().getImageMimeType()).isNull();
    }

    @Test
    void removeProfileImage_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        mockMvc.perform(delete("/api/platform/users/me/delete/profile-image"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void removeProfileImage_shouldSucceedEvenWhenNoImageExists() throws Exception {
        String keycloakId = createUser("kc-id1", "testuser", "Test@gmail.com");
        Optional<User> userBefore = userRepository.findByKeycloakId(keycloakId);

        assertThat(userBefore).isPresent();
        assertThat(userBefore.get().getProfile().getProfileImage()).isNull();

        mockMvc.perform(delete("/api/platform/users/me/delete/profile-image")
                        .with(validJwt(keycloakId)))
                .andExpect(status().isNoContent());

        Optional<User> userAfter = userRepository.findByKeycloakId(keycloakId);
        assertThat(userAfter).isPresent();
        assertThat(userAfter.get().getProfile().getProfileImage()).isNull();
    }

    @Test
    void removeProfileImage_shouldOnlyRemoveOwnImage() throws Exception {
        String keycloakId1 = createUser("kc-id1", "testuser", "Test1@gmail.com");
        String keycloakId2 = createUser("kc-id2", "testuser2", "Test2@gmail.com");
        uploadTestImage(keycloakId1);

        mockMvc.perform(delete("/api/platform/users/me/delete/profile-image")
                        .with(jwtForUser(keycloakId2)))
                .andExpect(status().isNoContent());

        Optional<User> originalUser = userRepository.findByKeycloakId(keycloakId1);
        assertThat(originalUser).isPresent();
        assertThat(originalUser.get().getProfile().getProfileImage()).isNotNull();
    }
}