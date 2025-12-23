package be.kdg.ipj3.perks.api;

import be.kdg.ipj3.perks.api.dto.PerkDto;
import be.kdg.ipj3.perks.application.PerkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/perks")
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
}