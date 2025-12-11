package be.kdg.ipj3.achievementProgress.api;

import be.kdg.ipj3.achievementProgress.api.dto.AchievementProgressDto;
import be.kdg.ipj3.achievementProgress.application.AchievementProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/user/{keycloakId}")
    public ResponseEntity<List<AchievementProgressDto>> getAchievementProgressByKeycloakId(
            @PathVariable String keycloakId
    ) {
        var achievements = achievementProgressService.findAllAchievementsByKeycloakId(keycloakId);
        return ResponseEntity.ok(achievements);
    }
}
