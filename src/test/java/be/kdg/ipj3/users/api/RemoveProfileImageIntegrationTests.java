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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RemoveProfileImageIntegrationTests extends BaseIntegrationTest {

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

    private void uploadTestImage(String keycloakId, String username) throws Exception {
        MockMultipartFile imageFile = createValidTestImage();

        mockMvc.perform(multipart("/api/platform/users/me/profile-image")
                        .file(imageFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(validJwt(keycloakId, username))
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    void removeProfileImage_shouldSucceed_whenAuthenticatedAndHasImage() throws Exception {
        // Arrange
        User user = createTestUser("kc-id1", "testuser", "Test@gmail.com");
        uploadTestImage(user.getKeycloakId(), user.getUsername());

        Optional<User> userBefore = userRepository.findByKeycloakId(user.getKeycloakId());
        assertThat(userBefore).isPresent();
        assertThat(userBefore.get().getProfile().profileImage()).isNotNull();

        // Act
        mockMvc.perform(delete("/api/platform/users/me/delete/profile-image")
                        .with(validJwt(user.getKeycloakId(), user.getUsername())))
                .andExpect(status().isNoContent());

        // Assert
        Optional<User> userAfter = userRepository.findByKeycloakId(user.getKeycloakId());
        assertThat(userAfter).isPresent();
        assertThat(userAfter.get().getProfile().profileImage()).isNull();
        assertThat(userAfter.get().getProfile().imageMimeType()).isNull();
    }

    @Test
    void removeProfileImage_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/platform/users/me/delete/profile-image"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void removeProfileImage_shouldSucceedEvenWhenNoImageExists() throws Exception {
        // Arrange
        User user = createTestUser("kc-id1", "testuser", "Test@gmail.com");
        Optional<User> userBefore = userRepository.findByKeycloakId(user.getKeycloakId());

        assertThat(userBefore).isPresent();
        assertThat(userBefore.get().getProfile().profileImage()).isNull();

        // Act
        mockMvc.perform(delete("/api/platform/users/me/delete/profile-image")
                        .with(validJwt(user.getKeycloakId(), user.getUsername())))
                .andExpect(status().isNoContent());

        // Assert
        Optional<User> userAfter = userRepository.findByKeycloakId(user.getKeycloakId());
        assertThat(userAfter).isPresent();
        assertThat(userAfter.get().getProfile().profileImage()).isNull();
    }

    @Test
    void removeProfileImage_shouldOnlyRemoveOwnImage() throws Exception {
        // Arrange
        User user1 = createTestUser("kc-id1", "testuser", "Test1@gmail.com");
        User user2 = createTestUser("kc-id2", "testuser2", "Test2@gmail.com");
        uploadTestImage(user1.getKeycloakId(), user1.getUsername());

        // Act
        mockMvc.perform(delete("/api/platform/users/me/delete/profile-image")
                        .with(validJwt(user2.getKeycloakId(), user2.getUsername())))
                .andExpect(status().isNoContent());

        // Assert
        Optional<User> originalUser = userRepository.findByKeycloakId(user1.getKeycloakId());
        assertThat(originalUser).isPresent();
        assertThat(originalUser.get().getProfile().profileImage()).isNotNull();
    }
}