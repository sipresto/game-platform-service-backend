package be.kdg.ipj3.achievementProgress.api;

import be.kdg.ipj3.achievementProgress.api.dto.AchievementProgressDto;
import be.kdg.ipj3.achievementProgress.application.AchievementProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
public class AchievementProgressController {
    private final AchievementProgressService achievementProgressService;

    public AchievementProgressController(AchievementProgressService achievementProgressService) {
        this.achievementProgressService = achievementProgressService;
    }

    @GetMapping
    public ResponseEntity<List<AchievementProgressDto>> getAchievements(
            @AuthenticationPrincipal Jwt jwt
            ) {
        String keycloakId = jwt.getSubject();
        var achievements = achievementProgressService.findAllAchievements(keycloakId);
        return ResponseEntity.ok(achievements);
    }
}
