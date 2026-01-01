package be.kdg.ipj3.users.api;

import be.kdg.ipj3.BaseIntegrationTest;
import be.kdg.ipj3.users.domain.*;
import be.kdg.ipj3.users.shared.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static javax.imageio.ImageIO.write;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class GetProfileImageIntegrationTests extends BaseIntegrationTest {

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

    private void uploadTestImage(User user) throws Exception {
        MockMultipartFile imageFile = createValidTestImage();
        mockMvc.perform(multipart("/api/platform/users/me/profile-image")
                        .file(imageFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(validJwt(user.getKeycloakId(), user.getUsername()))
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    void getProfileImage_shouldReturnImage_whenUserHasImage() throws Exception {
        User user = createTestUser("kc-id", "testuser", "test@example.com");
        uploadTestImage(user);

        mockMvc.perform(get("/api/platform/users/profile-image/{userId}", user.getId().id())
                        .with(validJwt(user.getKeycloakId(), user.getUsername()))
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/png"))
                .andExpect(header().exists("Content-Length"));
    }

    @Test
    void getProfileImage_shouldReturnDefaultImage_whenUserHasNoImage() throws Exception {
        User user = createTestUser("kc-id", "testuser", "test@example.com");

        mockMvc.perform(get("/api/platform/users/profile-image/{userId}", user.getId().id())
                        .with(validJwt(user.getKeycloakId(), user.getUsername()))
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/svg+xml"))
                .andExpect(header().exists("Content-Length"))
                .andExpect(content().string(containsString("<svg")));
    }

    @Test
    void getProfileImage_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        UUID nonExistentUserId = UUID.randomUUID();

        mockMvc.perform(get("/api/platform/users/profile-image/{userId}", nonExistentUserId)
                        .with(validJwt())
                        .accept(MediaType.ALL))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProfileImage_shouldReturnImage_whenUserIsOwnerAndProfileIsPrivate() throws Exception {
        Map<VisibleField, Visibility> privateVisibility = VisibilitySettings.defaultPublic().fieldVisibilities();
        privateVisibility.put(VisibleField.PROFILE, Visibility.PRIVATE);
        var privateVisibilitySettings = new VisibilitySettings(privateVisibility);

        User privateUser = new User(
                new UserId(UUID.randomUUID()),
                "kc-private",
                "testuser",
                List.of(),
                List.of(),
                null,
                0,
                new Profile("Test User", "Test bio", null, null),
                privateVisibilitySettings,
                "test@example.com"
        );
        userRepository.save(privateUser);

        uploadTestImage(privateUser);

        mockMvc.perform(get("/api/platform/users/profile-image/{userId}", privateUser.getId().id())
                        .with(validJwt(privateUser.getKeycloakId(), privateUser.getUsername()))
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/png"));
    }
}