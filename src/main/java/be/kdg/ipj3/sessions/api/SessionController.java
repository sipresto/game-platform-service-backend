package be.kdg.ipj3.sessions.api;

import be.kdg.ipj3.sessions.api.dto.SessionDto;
import be.kdg.ipj3.sessions.application.SessionService;
import be.kdg.ipj3.users.application.UserService;
import be.kdg.ipj3.users.domain.User;
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
    private final UserService userService;

    public SessionController(SessionService sessionService, UserService userService) {
        this.sessionService = sessionService;
        this.userService = userService;
    }

    // TODO: change to /user/{userId} and change this to receiving UserId instead of keycloak id
    @GetMapping("/{userId}")
    public ResponseEntity<List<SessionDto>> getSessionsByUserId(
            @PathVariable String userId
    ) {
        User user = userService.findByKeycloakId(userId);

        List<SessionDto> dtos = sessionService.findSessionsByUserId(user.getId()).stream()
                .map(SessionDto::fromDomain)
                .toList();

        return ResponseEntity.ok(dtos);
    }
}
