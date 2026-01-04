package be.kdg.ipj3.social.shared;

import be.kdg.ipj3.lobbies.shared.LobbyId;
import be.kdg.ipj3.users.shared.UserId;
import org.springframework.modulith.NamedInterface;


@NamedInterface
public interface ChatsApi {
    void sendInvite(String keycloakId, String gameName, String password, LobbyId lobbyId, UserId senderId, UserId receiverId);
    void blockChat(UserId blockerId, UserId blockedId);
    void unblockChat(UserId unblockerId, UserId unblockedId);
}
