package be.kdg.ipj3.sessions.api;

import be.kdg.ipj3.sessions.api.dto.SessionDto;
import be.kdg.ipj3.sessions.application.SessionService;
import be.kdg.ipj3.shared.domain.ResourceNotFoundException;
import be.kdg.ipj3.shared.domain.UserId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/session")
public class SessionController {
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<SessionDto>> getSessionsByUserId(
            @PathVariable UUID userId
    ) {
        List<SessionDto> dtos = sessionService.findSessionsByUserId(new UserId(userId)).stream()
                .map(SessionDto::fromDomain)
                .toList();

        if (dtos.isEmpty()) {
            throw new ResourceNotFoundException("No games found");
        }

        return ResponseEntity.ok(dtos);
    }
}
