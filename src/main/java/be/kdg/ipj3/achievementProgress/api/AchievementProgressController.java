package be.kdg.ipj3.achievementProgress.api;

import be.kdg.ipj3.achievementProgress.api.dto.AchievementProgressDto;
import be.kdg.ipj3.achievementProgress.application.AchievementProgressService;
import be.kdg.ipj3.users.shared.UserId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/platform/achievements")
public class AchievementProgressController {
    private final AchievementProgressService achievementProgressService;

    public AchievementProgressController(AchievementProgressService achievementProgressService) {
        this.achievementProgressService = achievementProgressService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AchievementProgressDto>> getAchievementProgressByKeycloakId(
            @PathVariable UUID userId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        var achievements = achievementProgressService.findAllAchievementsByUserId(userId, jwt.getSubject());
        return ResponseEntity.ok(achievements);
    }
}
