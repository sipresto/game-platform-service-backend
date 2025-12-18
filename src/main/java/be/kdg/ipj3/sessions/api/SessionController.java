package be.kdg.ipj3.sessions.api;

import be.kdg.ipj3.sessions.api.dto.SessionDto;
import be.kdg.ipj3.sessions.application.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// TODO: change session to sessions (in frontend as well)
@RestController
@RequestMapping("/api/session")
public class SessionController {
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    // TODO: change to /user/{userId}
    @GetMapping("/{userId}")
    public ResponseEntity<List<SessionDto>> getSessionsByUserId(
            @PathVariable String userId
    ) {
        List<SessionDto> dtos = sessionService.findFinishedSessionsByUserId(userId).stream()
                .map(SessionDto::fromDomain)
                .toList();

        return ResponseEntity.ok(dtos);
    }
}
