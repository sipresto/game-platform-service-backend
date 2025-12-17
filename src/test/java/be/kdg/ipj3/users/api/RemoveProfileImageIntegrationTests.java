package be.kdg.ipj3.users.api;

import be.kdg.ipj3.users.domain.Visibility;
import be.kdg.ipj3.users.infrastructure.jpa.JpaProfile;
import be.kdg.ipj3.users.infrastructure.jpa.JpaUserEntity;
import be.kdg.ipj3.users.infrastructure.jpa.JpaUserRepository;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@Transactional
public class RemoveProfileImageIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaUserRepository userRepository;

    private String testKeycloakId;
    private final String testEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        UUID testUserId = UUID.randomUUID();
        testKeycloakId = "test-keycloak-id-" + UUID.randomUUID();

        JpaUserEntity user = new JpaUserEntity(
                testUserId,
                testKeycloakId,
                "testuser",
                new JpaProfile("Test User", "Test bio", null, null),
                0,
                Visibility.PUBLIC,
                testEmail
        );
        userRepository.save(user);
    }

    private JwtRequestPostProcessor validJwt() {
        return jwt()
                .jwt(builder -> builder
                        .subject(testKeycloakId)
                        .claim("preferred_username", "testuser")
                        .claim("email", testEmail));
    }

    private JwtRequestPostProcessor jwtForUser(String keycloakId) {
        return jwt()
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
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(100, 100, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = image.createGraphics();
        g2d.setColor(java.awt.Color.RED);
        g2d.fillRect(0, 0, 100, 100);
        g2d.dispose();

        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        javax.imageio.ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    private void uploadTestImage() throws Exception {
        MockMultipartFile imageFile = createValidTestImage();
        mockMvc.perform(multipart("/api/me/profile-image")
                        .file(imageFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(validJwt())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    void removeProfileImage_shouldSucceed_whenAuthenticatedAndHasImage() throws Exception {
        uploadTestImage();

        Optional<JpaUserEntity> userBefore = userRepository.findByKeycloakId(testKeycloakId);
        assertThat(userBefore).isPresent();
        assertThat(userBefore.get().getProfile().getProfileImage()).isNotNull();

        mockMvc.perform(delete("/api/me/delete/profile-image")
                        .with(validJwt()))
                .andExpect(status().isNoContent());

        Optional<JpaUserEntity> userAfter = userRepository.findByKeycloakId(testKeycloakId);
        assertThat(userAfter).isPresent();
        assertThat(userAfter.get().getProfile().getProfileImage()).isNull();
        assertThat(userAfter.get().getProfile().getImageMimeType()).isNull();
    }

    @Test
    void removeProfileImage_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        mockMvc.perform(delete("/api/me/delete/profile-image"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void removeProfileImage_shouldSucceedEvenWhenNoImageExists() throws Exception {
        Optional<JpaUserEntity> userBefore = userRepository.findByKeycloakId(testKeycloakId);
        assertThat(userBefore).isPresent();
        assertThat(userBefore.get().getProfile().getProfileImage()).isNull();

        mockMvc.perform(delete("/api/me/delete/profile-image")
                        .with(validJwt()))
                .andExpect(status().isNoContent());

        Optional<JpaUserEntity> userAfter = userRepository.findByKeycloakId(testKeycloakId);
        assertThat(userAfter).isPresent();
        assertThat(userAfter.get().getProfile().getProfileImage()).isNull();
    }

    @Test
    void removeProfileImage_shouldOnlyRemoveOwnImage() throws Exception {
        uploadTestImage();

        String differentKeycloakId = "different-keycloak-id";
        UUID differentUserId = UUID.randomUUID();
        JpaUserEntity differentUser = new JpaUserEntity(
                differentUserId,
                differentKeycloakId,
                "differentuser",
                new JpaProfile("Different User", "", null, null),
                0,
                Visibility.PUBLIC,
                "different@example.com"
        );
        userRepository.save(differentUser);

        mockMvc.perform(delete("/api/me/delete/profile-image")
                        .with(jwtForUser(differentKeycloakId)))
                .andExpect(status().isNoContent());

        Optional<JpaUserEntity> originalUser = userRepository.findByKeycloakId(testKeycloakId);
        assertThat(originalUser).isPresent();
        assertThat(originalUser.get().getProfile().getProfileImage()).isNotNull();
    }
}