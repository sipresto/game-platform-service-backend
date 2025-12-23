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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static javax.imageio.ImageIO.write;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class GetProfileImageIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaUserRepository userRepository;

    private UUID testUserId;
    private String testKeycloakId;
    private final String testEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUserId = UUID.randomUUID();
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

    private void uploadTestImage() throws Exception {
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
    }

    @Test
    void getProfileImage_shouldReturnImage_whenUserHasImage() throws Exception {
        uploadTestImage();

        mockMvc.perform(get("/api/platform/users/{userId}/profile-image", testUserId)
                        .with(validJwt())
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/png"))
                .andExpect(header().exists("Content-Length"));
    }

    @Test
    void getProfileImage_shouldReturnDefaultImage_whenUserHasNoImage() throws Exception {
        mockMvc.perform(get("/api/platform/users/{userId}/profile-image", testUserId)
                        .with(validJwt())
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/svg+xml"))
                .andExpect(header().exists("Content-Length"))
                .andExpect(content().string(containsString("<svg")));
    }

    @Test
    void getProfileImage_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        UUID nonExistentUserId = UUID.randomUUID();

        mockMvc.perform(get("/api/platform/users/{userId}/profile-image", nonExistentUserId)
                        .with(validJwt())
                        .accept(MediaType.ALL))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProfileImage_shouldReturnImage_whenUserIsOwnerAndProfileIsPrivate() throws Exception {
        JpaUserEntity privateUser = new JpaUserEntity(
                testUserId,
                testKeycloakId,
                "testuser",
                new JpaProfile("Test User", "Test bio", null, null),
                0,
                Visibility.PRIVATE,
                testEmail
        );
        userRepository.save(privateUser);
        uploadTestImage();

        mockMvc.perform(get("/api/platform/users/{userId}/profile-image", testUserId)
                        .with(validJwt())
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/png"));
    }
}
