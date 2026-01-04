package be.kdg.ipj3.lobbies.api.dto;

import java.util.List;

public record OpenLobbiesDto(
        List<LobbyListDto> availableLobbies,
        LobbyDetailDto currentLobby,
        String differentGameName
) {
}
