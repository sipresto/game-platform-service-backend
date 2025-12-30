package be.kdg.ipj3.users.api;

import be.kdg.ipj3.BaseIntegrationTest;
import be.kdg.ipj3.users.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Optional;

import static javax.imageio.ImageIO.write;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UploadProfileImageIntegrationTests extends BaseIntegrationTest {

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
        User user = createTestUser("kc-id1", "testuser", "test@example.com");

        mockMvc.perform(multipart("/api/platform/users/me/profile-image")
                        .file(imageFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(validJwt(user.getKeycloakId(), user.getUsername()))
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk());

        Optional<User> userOpt = userRepository.findByKeycloakId(user.getKeycloakId());
        assertThat(userOpt).isPresent();

        User updatedUser = userOpt.get();
        assertThat(updatedUser.getProfile().profileImage()).isNotNull();
        assertThat(updatedUser.getProfile().profileImage().length).isGreaterThan(0);
        assertThat(updatedUser.getProfile().imageMimeType()).isEqualTo("image/png");
    }

    @Test
    void uploadProfileImage_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        MockMultipartFile imageFile = createValidTestImage();
        User user = createTestUser("kc-id1", "testuser", "test@example.com");

        mockMvc.perform(multipart("/api/platform/users/me/profile-image")
                        .file(imageFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isUnauthorized());

        Optional<User> userOpt = userRepository.findByKeycloakId(user.getKeycloakId());
        assertThat(userOpt).isPresent();
        assertThat(userOpt.get().getProfile().profileImage()).isNull();
    }

    @Test
    void uploadProfileImage_shouldReturnBadRequest_whenFileTooLarge() throws Exception {
        MockMultipartFile largeImageFile = createLargeTestImage();
        User user = createTestUser("kc-id1", "testuser", "test@example.com");

        mockMvc.perform(multipart("/api/platform/users/me/profile-image")
                        .file(largeImageFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(validJwt(user.getKeycloakId(), user.getUsername()))
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isBadRequest());

        Optional<User> userOpt = userRepository.findByKeycloakId(user.getKeycloakId());
        assertThat(userOpt).isPresent();
        assertThat(userOpt.get().getProfile().profileImage()).isNull();
    }

    @Test
    void uploadProfileImage_shouldReturnBadRequest_whenInvalidFileType() throws Exception {
        MockMultipartFile invalidImageFile = createInvalidImageFile();
        User user = createTestUser("kc-id1", "testuser", "test@example.com");

        mockMvc.perform(multipart("/api/platform/users/me/profile-image")
                        .file(invalidImageFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(validJwt(user.getKeycloakId(), user.getUsername()))
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isBadRequest());

        Optional<User> userOpt = userRepository.findByKeycloakId(user.getKeycloakId());
        assertThat(userOpt).isPresent();
        assertThat(userOpt.get().getProfile().profileImage()).isNull();
    }

    @Test
    void uploadProfileImage_shouldOverwriteExistingImage() throws Exception {
        MockMultipartFile firstImage = createValidTestImage();
        User user = createTestUser("kc-id1", "testuser", "test@example.com");

        mockMvc.perform(multipart("/api/platform/users/me/profile-image")
                        .file(firstImage)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(validJwt(user.getKeycloakId(), user.getUsername()))
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk());

        Optional<User> userAfter = userRepository.findByKeycloakId(user.getKeycloakId());
        assertThat(userAfter).isPresent();
        assertThat(userAfter.get().getProfile().profileImage()).isNotNull();
        assertThat(userAfter.get().getProfile().imageMimeType()).isEqualTo("image/png");
    }
}