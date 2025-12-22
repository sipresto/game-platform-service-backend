package be.kdg.ipj3.users.application;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.shared.domain.exceptions.NotFoundException;
import be.kdg.ipj3.users.domain.*;
import be.kdg.ipj3.users.shared.EmailUserDto;
import be.kdg.ipj3.users.shared.SmallUserDto;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserService implements UsersApi {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Register or get existing user fromDomain Keycloak JWT
     * This is called automatically on first login
     */
    public User registerOrGetUser(String keycloakId, String keycloakUsername, String email) {
        if (keycloakId == null || keycloakId.isBlank()) {
            throw new IllegalArgumentException("Keycloak id cannot be null or blank");
        }

        return userRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> {
                    User newUser = User.createFromKeycloak(keycloakId, keycloakUsername, email);
                    userRepository.save(newUser);
                    return newUser;
                });
    }

    @Transactional(readOnly = true)
    public User findById(UUID id) {
        var userId = new UserId(id);
        return userRepository.findById(userId)
                .orElseThrow(userId::notFound);
    }

    public void updateProfile(String keycloakId, String displayName, String newBio) {
        if (keycloakId == null || keycloakId.isBlank()) {
            throw new IllegalArgumentException("Keycloak id cannot be null or blank");
        }

        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException("User with keycloak id [" + keycloakId + "] not found"));

        user.updateProfile(new Profile(displayName, newBio));
        userRepository.save(user);
    }

    public void addGame(UUID id, UUID gameId) {
        var userId = new UserId(id);

        User user = userRepository.findById(userId).orElseThrow(userId::notFound);
        user.addGame(new GameId(gameId));
        userRepository.save(user);
    }

    @Override
    public void addFavorite(String id, UUID gameIdParam) {
        var user = findByKeycloakId(id);
        var gameId = new GameId(gameIdParam);
        if (!user.getOwnedGames().contains(gameId)) {
            throw new IllegalArgumentException("You must own the game to add it to favorites.");
        }
        user.addFavorite(gameId);
        userRepository.save(user);
    }

    @Override
    public void removeFavorite(String id, UUID gameId) {
        var user = findByKeycloakId(id);
        user.removeFavorite(new GameId(gameId));
        userRepository.save(user);
    }


    @Transactional(readOnly = true)
    public List<GameId> findOwnedGamesById(UUID id) {
        return userRepository.findOwnedGamesById(new UserId(id));
    }

    public List<GameId> findFavoriteGamesByUserId(UUID id){
        return userRepository.findFavoriteGamesByUserId(new UserId(id));
    }

    @Transactional
    public void addPoints(UUID id, int points) {
        var userId = new UserId(id);

        User user = userRepository.findById(userId).orElseThrow(userId::notFound);
        user.addPoints(points);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByKeycloakId(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException("User with keycloak id [" + keycloakId + "] not found"));
    }

    public void updatePrivacySettingsByKeycloakId(Visibility visibility, User user) {
        user.updateVisibility(visibility);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> findAllPublicUsers() {
        return userRepository.findAllPublicUsers();
    }

    @Transactional(readOnly = true)
    public List<User> findFriends(User user) {
        return new ArrayList<>(userRepository.findFriends(user.getId()));
    }

    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        return userRepository.existsById(new UserId(id));
    }

    @Transactional(readOnly = true)
    public UserId findIdByKeycloakId(String keycloakId) {
        return findByKeycloakId(keycloakId).getId();
    }

    @Transactional(readOnly = true)
    public SmallUserDto findSmallUserDtoById(UUID id) {
        return SmallUserDto.fromDomain(findById(id));
    }
    @Transactional(readOnly = true)
    public EmailUserDto findEmailUserDtoByKeycloakId(UUID id){
        return EmailUserDto.fromDomain(findById(id));
    }


    @Override
    public String findEmailByUserId(UserId id) {
        return userRepository.findById(id).orElseThrow(id::notFound).getEmail();
    }

    @Override
    public String findDisplayNameById(UserId id) {
        return userRepository.findById(id)
                .orElseThrow(id::notFound)
                .getProfile()
                .displayName();
    }

    public void sendFriendRequest(UUID receiver, String loggedInUserKeycloakId) {
        User receiverUser = findById(receiver);
        User senderUser = findByKeycloakId(loggedInUserKeycloakId);
        receiverUser.validateFriendRequests(senderUser.getId());
        receiverUser.addIncomingFriendRequest(new FriendRequest(senderUser.getId()));
        senderUser.addOutgoingFriendRequest(new FriendRequest(receiverUser.getId()));
        userRepository.save(receiverUser);
        userRepository.save(senderUser);
    }

    public void acceptFriendRequest(UUID receiver, UUID sender) {
        User receiverUser = findById(receiver);
        User senderUser = findById(sender);
        receiverUser.acceptFriendRequest(senderUser.getId());
        senderUser.addFriend(receiverUser.getId());
        senderUser.removeOutgoingFriendRequest(receiverUser.getId());
        userRepository.save(receiverUser);
        userRepository.save(senderUser);
    }

    public void rejectFriendRequest(UUID receiver, UUID sender) {
        User receiverUser = findById(receiver);
        User senderUser = findById(sender);
        receiverUser.removeIncomingFriendRequest(senderUser.getId());
        senderUser.removeOutgoingFriendRequest(receiverUser.getId());
        userRepository.save(receiverUser);
        userRepository.save(senderUser);
    }
}