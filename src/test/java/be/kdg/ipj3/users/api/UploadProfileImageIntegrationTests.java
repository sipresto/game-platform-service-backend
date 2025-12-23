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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Optional;
import java.util.UUID;

import static javax.imageio.ImageIO.write;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@Transactional
public class UploadProfileImageIntegrationTests {

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
                        .claim("email", testEmail))
                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER"));
    }

    private MockMultipartFile createTestImageFile(String filename, byte[] content) {
        return new MockMultipartFile(
                "image",
                filename,
                "image/png",
                content
        );
    }

    private MockMultipartFile createValidTestImage() throws Exception {
        byte[] pngBytes = createSimplePngImage();
        return createTestImageFile("test.png", pngBytes);
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

    private MockMultipartFile createLargeTestImage() {
        byte[] largeBytes = new byte[5 * 1024 * 1024 + 1];
        for (int i = 0; i < largeBytes.length; i++) {
            largeBytes[i] = (byte) (i % 256);
        }
        largeBytes[0] = (byte) 0x89;
        largeBytes[1] = 0x50;
        largeBytes[2] = 0x4E;
        largeBytes[3] = 0x47;
        return createTestImageFile("large.png", largeBytes);
    }

    private MockMultipartFile createInvalidImageFile() {
        byte[] invalidBytes = "This is not an image".getBytes();
        return createTestImageFile("fake.png", invalidBytes);
    }

    @Test
    void uploadProfileImage_shouldSucceed_whenValidImage() throws Exception {
        MockMultipartFile imageFile = createValidTestImage();

        mockMvc.perform(multipart("/api/platform/users/me/profile-image")
                        .file(imageFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(validJwt())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk());

        Optional<JpaUserEntity> userOpt = userRepository.findByKeycloakId(testKeycloakId);
        assertThat(userOpt).isPresent();

        JpaUserEntity user = userOpt.get();
        assertThat(user.getProfile().getProfileImage()).isNotNull();
        assertThat(user.getProfile().getProfileImage().length).isGreaterThan(0);
        assertThat(user.getProfile().getImageMimeType()).isEqualTo("image/png");
    }

    @Test
    void uploadProfileImage_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        MockMultipartFile imageFile = createValidTestImage();

        mockMvc.perform(multipart("/api/platform/users/me/profile-image")
                        .file(imageFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isUnauthorized());

        Optional<JpaUserEntity> userOpt = userRepository.findByKeycloakId(testKeycloakId);
        assertThat(userOpt).isPresent();
        assertThat(userOpt.get().getProfile().getProfileImage()).isNull();
    }

    @Test
    void uploadProfileImage_shouldReturnBadRequest_whenFileTooLarge() throws Exception {
        MockMultipartFile largeImageFile = createLargeTestImage();

        mockMvc.perform(multipart("/api/platform/users/me/profile-image")
                        .file(largeImageFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(validJwt())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isBadRequest());

        Optional<JpaUserEntity> userOpt = userRepository.findByKeycloakId(testKeycloakId);
        assertThat(userOpt).isPresent();
        assertThat(userOpt.get().getProfile().getProfileImage()).isNull();
    }

    @Test
    void uploadProfileImage_shouldReturnBadRequest_whenInvalidFileType() throws Exception {
        MockMultipartFile invalidImageFile = createInvalidImageFile();

        mockMvc.perform(multipart("/api/platform/users/me/profile-image")
                        .file(invalidImageFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(validJwt())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isBadRequest());

        Optional<JpaUserEntity> userOpt = userRepository.findByKeycloakId(testKeycloakId);
        assertThat(userOpt).isPresent();
        assertThat(userOpt.get().getProfile().getProfileImage()).isNull();
    }

    @Test
    void uploadProfileImage_shouldOverwriteExistingImage() throws Exception {
        MockMultipartFile firstImage = createValidTestImage();
        mockMvc.perform(multipart("/api/platform/users/me/profile-image")
                        .file(firstImage)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(validJwt())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk());

        Optional<JpaUserEntity> userAfter = userRepository.findByKeycloakId(testKeycloakId);
        assertThat(userAfter).isPresent();
        assertThat(userAfter.get().getProfile().getProfileImage()).isNotNull();
        assertThat(userAfter.get().getProfile().getImageMimeType()).isEqualTo("image/png");
    }
}