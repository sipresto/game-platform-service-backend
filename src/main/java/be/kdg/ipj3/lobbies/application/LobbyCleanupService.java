package be.kdg.ipj3.lobbies.application;

import be.kdg.ipj3.lobbies.domain.Lobby;
import be.kdg.ipj3.lobbies.domain.LobbyRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class LobbyCleanupService {
    private final LobbyRepository lobbyRepository;

    @Value("${lobby.inactive.cutoff.minutes:30}")
    private int inactiveLobbyCutoffMinutes;

    public LobbyCleanupService(LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    // 10 minutes
    @Scheduled(fixedRate = 600000)
    @Transactional
    public void cleanupInactiveLobbies() {
        Instant inactiveCutoff = Instant.now()
                .minus(Duration.ofMinutes(inactiveLobbyCutoffMinutes));

        List<Lobby> inactiveLobbies = lobbyRepository
                .findByLastActivityBeforeInstant(inactiveCutoff);

        for (Lobby lobby : inactiveLobbies) {
            lobbyRepository.deleteById(lobby.getId());
        }
    }
}