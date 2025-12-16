// New: ProfileImageService.java
package be.kdg.ipj3.users.application;

import be.kdg.ipj3.shared.domain.exceptions.NotFoundException;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.UserRepository;
import be.kdg.ipj3.users.shared.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

@Service
@Transactional
public class ProfileImageService {

    private static final int MAX_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final int TARGET_SIZE = 200;
    private static final String[] ALLOWED_MIME_TYPES = {"image/jpeg", "image/png", "image/gif"};
    private static final byte[] JPEG_MAGIC = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] PNG_MAGIC = {(byte) 0x89, 'P', 'N', 'G'};
    private static final byte[] GIF_MAGIC = {'G', 'I', 'F'};

    private final UserRepository userRepository;

    public ProfileImageService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void updateProfileImage(String keycloakId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("File size exceeds 5MB limit");
        }

        String mimeType = file.getContentType();
        if (mimeType == null || !Arrays.asList(ALLOWED_MIME_TYPES).contains(mimeType.toLowerCase())) {
            throw new IllegalArgumentException("Only JPEG, PNG, and GIF images are allowed");
        }

        byte[] fileBytes = file.getBytes();
        if (!isValidImage(fileBytes)) {
            throw new IllegalArgumentException("Invalid image file");
        }

        byte[] processedImage;
        if (mimeType.equals("image/gif")) {
            processedImage = fileBytes;
        } else {
            processedImage = resizeImage(fileBytes, mimeType);
        }

        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException("User with keycloak id [" + keycloakId + "] not found"));

        user.updateProfileImage(processedImage, mimeType);
        userRepository.save(user);
    }

    public byte[] getProfileImage(UserId userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(userId::notFound);

        byte[] image = user.getProfile().profileImage();
        if (image == null || image.length == 0) {
            return getDefaultProfileImage();
        }

        return image;
    }

    public String getProfileImageMimeType(UserId userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(userId::notFound);

        String mimeType = user.getProfile().imageMimeType();
        if (mimeType == null) {
            return "image/svg+xml";
        }

        return mimeType;
    }

    public void removeProfileImage(String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException("User with keycloak id [" + keycloakId + "] not found"));
        user.removeProfileImage();
        userRepository.save(user);
    }

    private byte[] resizeImage(byte[] originalBytes, String mimeType) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(originalBytes);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            BufferedImage originalImage = ImageIO.read(bis);
            if (originalImage == null) {
                throw new IOException("Failed to read image");
            }

            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            double aspectRatio = (double) originalWidth / originalHeight;

            int newWidth, newHeight;
            if (originalWidth > originalHeight) {
                newWidth = TARGET_SIZE;
                newHeight = (int) (TARGET_SIZE / aspectRatio);
            } else {
                newHeight = TARGET_SIZE;
                newWidth = (int) (TARGET_SIZE * aspectRatio);
            }

            BufferedImage scaledImage = new BufferedImage(TARGET_SIZE, TARGET_SIZE,
                    mimeType.equals("image/png") ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);

            Graphics2D g2d = scaledImage.createGraphics();

            if (mimeType.equals("image/png")) {
                scaledImage = g2d.getDeviceConfiguration().createCompatibleImage(TARGET_SIZE, TARGET_SIZE, Transparency.TRANSLUCENT);
                g2d.dispose();
                g2d = scaledImage.createGraphics();
            } else {
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, TARGET_SIZE, TARGET_SIZE);
            }

            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int x = (TARGET_SIZE - newWidth) / 2;
            int y = (TARGET_SIZE - newHeight) / 2;

            g2d.drawImage(originalImage, x, y, newWidth, newHeight, null);
            g2d.dispose();

            String formatName = mimeType.equals("image/png") ? "png" : "jpg";
            ImageIO.write(scaledImage, formatName, bos);

            return bos.toByteArray();
        }
    }

    private boolean isValidImage(byte[] bytes) {
        if (bytes.length < 4) return false;

        if (bytes[0] == JPEG_MAGIC[0] && bytes[1] == JPEG_MAGIC[1] && bytes[2] == JPEG_MAGIC[2]) {
            return true;
        }

        if (bytes[0] == PNG_MAGIC[0] && bytes[1] == PNG_MAGIC[1] &&
                bytes[2] == PNG_MAGIC[2] && bytes[3] == PNG_MAGIC[3]) {
            return true;
        }

        return bytes[0] == GIF_MAGIC[0] && bytes[1] == GIF_MAGIC[1] && bytes[2] == GIF_MAGIC[2];
    }

    private byte[] getDefaultProfileImage() {
        String svg = """
            <svg xmlns="http://www.w3.org/2000/svg" width="200" height="200" viewBox="0 0 200 200">
                <circle cx="100" cy="100" r="80" fill="#e0e0e0"/>
                <circle cx="100" cy="80" r="30" fill="#9e9e9e"/>
                <path d="M50,140 Q100,180 150,140" stroke="#9e9e9e" stroke-width="10" fill="none"/>
            </svg>
            """;
        return svg.getBytes();
    }
}