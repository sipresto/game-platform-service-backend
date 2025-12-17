package be.kdg.ipj3.sessions.shared;

import be.kdg.ipj3.lobbies.shared.Difficulty;
import org.springframework.modulith.NamedInterface;

import java.util.List;
import java.util.UUID;

@NamedInterface
public interface SessionsApi {
    void createSession(UUID id, UUID gameId, List<UUID> players, Difficulty difficulty);
}
