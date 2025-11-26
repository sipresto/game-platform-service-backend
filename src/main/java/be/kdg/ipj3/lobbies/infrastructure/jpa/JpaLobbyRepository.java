package be.kdg.ipj3.lobbies.infrastructure.jpa;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaLobbyRepository extends JpaRepository<JpaLobbyEntity, UUID> {
    List<JpaLobbyEntity> findByStatus(String status);
}