package be.kdg.ipj3.perks.api;

import be.kdg.ipj3.perks.api.dto.PerkDto;
import be.kdg.ipj3.perks.application.PerkService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/platform/perks")
public class PerkController {

    private final PerkService perkService;

    public PerkController(PerkService perkService) {
        this.perkService = perkService;
    }

    @GetMapping
    public ResponseEntity<List<PerkDto>> getAllPerks() {
        List<PerkDto> dtos = perkService.findAllPerks().stream()
                .map(PerkDto::fromDomain)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{perkId}")
    public PerkDto getPerkById(
            @PathVariable UUID perkId) {
        return PerkDto.fromDomain(perkService.findPerkById(perkId));
    }
    @PostMapping("/perk/purchase/{perkId}")
    public ResponseEntity<Void> buyPerk(
            @PathVariable UUID perkId,
            @AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        perkService.purchasePerk(perkId, keycloakId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{perkId}/equip")
    public ResponseEntity<Void> equipPerk(
            @PathVariable UUID perkId,
            @AuthenticationPrincipal Jwt jwt) {

        String keycloakId = jwt.getSubject();
        perkService.equipPerk(perkId, keycloakId);
        return ResponseEntity.ok().build();
    }
}