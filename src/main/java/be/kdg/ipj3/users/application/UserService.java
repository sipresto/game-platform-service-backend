package be.kdg.ipj3.users.application;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.perks.shared.PerkId;
import be.kdg.ipj3.shared.domain.exceptions.NotFoundException;
import be.kdg.ipj3.users.domain.*;
import be.kdg.ipj3.users.shared.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class UserService implements UsersApi {
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public UserService(UserRepository userRepository, ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Register or get existing user from Keycloak JWT
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
    public List<GameId> findOwnedGamesById(UUID id, String viewerKeycloakId) {
        var target = findById(id);
        var viewer = findByKeycloakId(viewerKeycloakId);

        return target.getVisibleOwnedGames(viewer);
    }

    @Transactional(readOnly = true)
    public List<GameId> findFavoriteGamesByUserId(UUID id){
        return userRepository.findFavoriteGamesByUserId(new UserId(id));
    }

    public void addPoints(UUID id, int points) {
        var userId = new UserId(id);

        User user = userRepository.findByIdForUpdate(userId)
                .orElseThrow(userId::notFound);

        user.addPoints(points);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByKeycloakId(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException("User with keycloak id [" + keycloakId + "] not found"));
    }

    public void updatePrivacySettingsByKeycloakId(Visibility visibility, String keycloakId) {
        User user = findByKeycloakId(keycloakId);
        user.updateVisibility(visibility);
        userRepository.save(user);
    }

    public void updateFieldVisibility(String keycloakId, VisibleField field, Visibility visibility) {
        User user = findByKeycloakId(keycloakId);
        user.updateVisibilityForField(field, visibility);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public VisibilitySettings getVisibilitySettings(String keycloakId) {
        User user = findByKeycloakId(keycloakId);
        return user.getVisibilitySettings();
    }

    @Transactional(readOnly = true)
    public List<User> findAllPublicUsers() {
        return userRepository.findAll();
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

    @Override
    public boolean checkBlock(Set<UserId> participantIds) {
        return participantIds.stream()
                .map(userRepository::findById)
                .flatMap(Optional::stream)
                .anyMatch(user ->
                        user.getBlockedUsers().stream()
                                .anyMatch(participantIds::contains)
                );
    }

    @Override
    public void purchasePerk(UUID perkIdParam, int pointCost, UserId userId) {
        User user = findById(userId.id());
        int availablePoints = user.getPoints();

        if (availablePoints < pointCost) {
            throw new IllegalArgumentException("Not enough points to purchase this perk");
        }

        var perkId = new PerkId(perkIdParam);
        if (user.hasPerk(perkId)) {
            throw new IllegalArgumentException("User already owns this perk");
        }

        user.removePoints(pointCost);
        user.addPerk(perkId);

        userRepository.save(user);
    }

    public int findPoints(String keycloakId) {
        User user = findByKeycloakId(keycloakId);
        return user.getPoints();
    }

    public void equipBorder(UserId userId, UUID perkId) {
        User user = userRepository.findById(userId).orElseThrow(userId::notFound);
        PerkId borderId = new PerkId(perkId);
        if (!user.hasPerk(borderId)) {
            throw new IllegalArgumentException("User does not own this border");
        }
        user.equipBorder(borderId);
        userRepository.save(user);
    }

    public void equipTheme(UserId userId, UUID perkId) {
        User user = userRepository.findById(userId).orElseThrow(userId::notFound);
        PerkId themeId = new PerkId(perkId);
        if (!user.hasPerk(themeId)) {
            throw new IllegalArgumentException("User does not own this theme");
        }
        user.equipTheme(themeId);
        userRepository.save(user);
    }

    public void equipTitle(UserId userId, UUID perkId) {
        User user = userRepository.findById(userId).orElseThrow(userId::notFound);
        PerkId titleId = new PerkId(perkId);
        if (!user.hasPerk(titleId)) {
            throw new IllegalArgumentException("User does not own this title");
        }
        user.equipTitle(new PerkId(perkId));
        userRepository.save(user);
    }

    @Override
    public void unequipBorder(UserId userId) {
        User user = userRepository.findById(userId).orElseThrow(userId::notFound);
        user.unequipBorder();
        userRepository.save(user);
    }

    @Override
    public void unequipTheme(UserId userId) {
        User user = userRepository.findById(userId).orElseThrow(userId::notFound);
        user.unequipTheme();
        userRepository.save(user);
    }

    @Override
    public void unequipTitle(UserId userId) {
        User user = userRepository.findById(userId).orElseThrow(userId::notFound);
        user.unequipTitle();
        userRepository.save(user);
    }

    public void sendFriendRequest(UUID receiver, String loggedInUserKeycloakId) {
        User receiverUser = findById(receiver);
        User senderUser = findByKeycloakId(loggedInUserKeycloakId);
        receiverUser.validateFriendRequests(senderUser.getId());
        receiverUser.addIncomingFriendRequest(new FriendRequest(senderUser.getId()));
        senderUser.addOutgoingFriendRequest(new FriendRequest(receiverUser.getId()));
        userRepository.save(receiverUser);
        userRepository.save(senderUser);

        eventPublisher.publishEvent(new FriendRequestSentEvent(
                senderUser.getId(),
                receiverUser.getId(),
                senderUser.getProfile().displayName()
        ));
    }

    public void acceptFriendRequest(String keycloakId, UUID sender) {
        User receiverUser = findByKeycloakId(keycloakId);
        User senderUser = findById(sender);
        receiverUser.acceptFriendRequest(senderUser.getId());
        senderUser.addFriend(receiverUser.getId());
        senderUser.removeOutgoingFriendRequest(receiverUser.getId());
        userRepository.save(receiverUser);
        userRepository.save(senderUser);
    }

    public void rejectFriendRequest(String keycloakId, UUID sender) {
        User receiverUser = findByKeycloakId(keycloakId);
        User senderUser = findById(sender);
        receiverUser.removeIncomingFriendRequest(senderUser.getId());
        senderUser.removeOutgoingFriendRequest(receiverUser.getId());
        userRepository.save(receiverUser);
        userRepository.save(senderUser);
    }

    public void removeFriend(String keycloakId, UUID friendId) {
        User user = findByKeycloakId(keycloakId);
        if(user.getId().id().equals(friendId)) {
            throw new IllegalArgumentException("FriendId can't be the same as UserId");
        }
        User friend = findById(friendId);
        user.removeFriend(friend.getId());
        friend.removeFriend(user.getId());
        userRepository.save(user);
        userRepository.save(friend);
    }

    public void blockUser(String keycloakId, UUID blockId) {
        User user = findByKeycloakId(keycloakId);
        if (user.getId().id().equals(blockId)) {
            throw new IllegalArgumentException("You can't block yourself");
        }
        User blockUser = findById(blockId);
        user.block(blockUser.getId());
        if(blockUser.getFriends().contains(user.getId())) {
            blockUser.removeFriend(user.getId());
            userRepository.save(blockUser);
        }
        userRepository.save(user);
    }

    public List<User> findBlockedUsers(String keycloakId) {
        User user = findByKeycloakId(keycloakId);
        return new ArrayList<>(userRepository.findBlockedUsers(user.getId()));
    }

    public void unblockUser(String keycloakId, UUID unblockId) {
        User user = findByKeycloakId(keycloakId);
        if (user.getId().id().equals(unblockId)) {
            throw new IllegalArgumentException("You can't unblock yourself");
        }
        user.unblock(new UserId(unblockId));
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserCosmeticsDto getUserCosmetics(UserId userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(userId::notFound);

        UserCosmetics cosmetics = user.getCosmetics();

        return new UserCosmeticsDto(
                cosmetics.getUnlockedPerkIds().stream()
                        .map(PerkId::id)
                        .collect(java.util.stream.Collectors.toSet()),
                cosmetics.getEquippedBorderId() != null ? cosmetics.getEquippedBorderId().id() : null,
                cosmetics.getEquippedThemeId() != null ? cosmetics.getEquippedThemeId().id() : null,
                cosmetics.getEquippedTitleId() != null ? cosmetics.getEquippedTitleId().id() : null
        );
    }
}