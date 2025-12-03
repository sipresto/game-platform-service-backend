package be.kdg.ipj3.achievements.infrastructure.jpa;

import be.kdg.ipj3.achievements.domain.AchievementProgress;
import be.kdg.ipj3.shared.domain.AchievementId;
import be.kdg.ipj3.shared.domain.UserId;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "achievement_progress")
public class JpaAchievementProgressEntity {

    @EmbeddedId
    private JpaAchievementProgressId id;

    @Column(nullable = false)
    private int currentProgress;

    private Instant completedAt;

    @Column(columnDefinition = "TEXT") // or JSONB on Postgres
    private String metadataJson;

    protected JpaAchievementProgressEntity() {
    } // for JPA

    public JpaAchievementProgressEntity(UUID userId, UUID achievementId, int currentProgress,
                                        Instant completedAt, String metadataJson) {
        this.id = new JpaAchievementProgressId(userId, achievementId);
        this.currentProgress = currentProgress;
        this.completedAt = completedAt;
        this.metadataJson = metadataJson;
    }

    public static JpaAchievementProgressEntity fromDomain(AchievementProgress progress) {
        return new JpaAchievementProgressEntity(
                progress.getId().userId().id(),
                progress.getId().achievementId().id(),
                progress.getCurrentProgress(),
                progress.getCompletedAt(),
                toJson(progress.getMetadata())
        );
    }

    public AchievementProgress toDomain() {
        return new AchievementProgress(
                new UserId(this.id.getUserId()),
                new AchievementId(this.id.getAchievementId()),
                this.currentProgress,
                this.completedAt,
                fromJson(this.metadataJson)
        );
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static String toJson(Map<String, Object> map) {
        try {
            return MAPPER.writeValueAsString(map);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize metadata", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> fromJson(String json) {
        if (json == null || json.isBlank()) return new HashMap<>();
        try {
            return MAPPER.readValue(json, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize metadata", e);
        }
    }

    public UUID getUserId() {
        return id.getUserId();
    }

    public UUID getAchievementId() {
        return id.getAchievementId();
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public String getMetadataJson() {
        return metadataJson;
    }
}
