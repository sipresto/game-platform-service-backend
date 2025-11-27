package be.kdg.ipj3.sessions.api;

import be.kdg.ipj3.sessions.api.dto.SessionDto;
import be.kdg.ipj3.sessions.application.SessionService;
import be.kdg.ipj3.shared.domain.ResourceNotFoundException;
import be.kdg.ipj3.users.application.UserService;
import be.kdg.ipj3.users.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/session")
public class SessionController {
    private final SessionService sessionService;
    private final UserService userService;

    public SessionController(SessionService sessionService, UserService userService) {
        this.sessionService = sessionService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<SessionDto>> getSessionsByKeycloakId(
            @PathVariable String id
    ) {
        User user = userService.findByKeycloakId(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with keycloak id " + id));

        List<SessionDto> dtos = sessionService.findSessionsByUserId(user.getId()).stream()
                .map(SessionDto::fromDomain)
                .toList();

        if (dtos.isEmpty()) {
            throw new ResourceNotFoundException("No games found");
        }

        return ResponseEntity.ok(dtos);
    }
}
