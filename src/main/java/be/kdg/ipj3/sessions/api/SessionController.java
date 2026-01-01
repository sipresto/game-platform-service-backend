package be.kdg.ipj3.sessions.api;

import be.kdg.ipj3.sessions.api.dto.SessionDto;
import be.kdg.ipj3.sessions.application.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/platform/sessions")
public class SessionController {
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/user")
    public ResponseEntity<List<SessionDto>> getSessionsByLoggedInUserKeycloakId(
            @AuthenticationPrincipal Jwt jwt
    ) {
        List<SessionDto> dtos = sessionService.findFinishedSessionsByUserId(jwt.getSubject()).stream()
                .map(SessionDto::fromDomain)
                .toList();

        return ResponseEntity.ok(dtos);
    }
}
