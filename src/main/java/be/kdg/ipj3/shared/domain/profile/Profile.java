package be.kdg.ipj3.shared.domain.profile;

import be.kdg.ipj3.shared.domain.game.Achievement;
import be.kdg.ipj3.shared.domain.game.GameId;
import be.kdg.ipj3.shared.domain.user.User;
import be.kdg.ipj3.shared.domain.user.UserId;
import be.kdg.ipj3.shared.domain.user.UserName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Profile extends User {
    private DisplayName displayName;
    private Bio bio;
    private Visibility visibility;
    private boolean receiveEmails;
    private boolean receiveNotifications;
    private final List<UserId> friendIds = new ArrayList<>();
    private final List<FriendRequest> friendRequests = new ArrayList<>();
    private final List<AchievementProgress> achievementProgresses = new ArrayList<>();
    private final List<Notification> notifications = new ArrayList<>();
    private List<Statistic> statistics = new ArrayList<>();

    public Profile(UserName username, DisplayName displayName, Bio bio, Visibility visibility, boolean receiveEmails, boolean receiveNotifications) {
        super(username);

        this.displayName = displayName;
        this.bio = bio;
        this.visibility = visibility;
        this.receiveEmails = receiveEmails;
        this.receiveNotifications = receiveNotifications;
    }

    //for jpa
    public Profile(UserId userId, UserName username, DisplayName displayName, Bio bio, Visibility visibility, boolean receiveEmails, boolean receiveNotifications, List<Statistic> statistics) {
        super(userId, username);
        this.displayName = displayName;
        this.bio = bio;
        this.visibility = visibility;
        this.receiveEmails = receiveEmails;
        this.receiveNotifications = receiveNotifications;
        this.statistics = statistics;
    }

    public DisplayName displayName() {
        return displayName;
    }

    public List<Statistic> statistics() {
        return statistics;
    }

    public Bio bio() {
        return bio;
    }

    public Visibility visibility() {
        return visibility;
    }

    public boolean receiveEmails() {
        return receiveEmails;
    }

    public boolean receiveNotifications() {
        return receiveNotifications;
    }

    public void updateDisplayName(DisplayName displayName) {
        this.displayName = displayName;
    }

    public void updateBio(Bio bio) {
        this.bio = bio;
    }

    public void changeVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public void toggleReceiveEmails() {
        receiveEmails = !receiveEmails;
    }

    public void toggleReceiveNotifications() {
        receiveNotifications = !receiveNotifications;
    }

    public List<UserId> friendIds() {
        return Collections.unmodifiableList(friendIds);
    }

    public List<Notification> notifications() {
        return notifications;
    }

    public List<FriendRequest> friendRequests() {
        return Collections.unmodifiableList(friendRequests);
    }

    public void addFriendRequest(FriendRequest friendRequest) {
        friendRequests.add(friendRequest);
    }

    public boolean denyFriendRequest(FriendRequest friendRequest) {
        return this.friendRequests.remove(friendRequest);
    }

    public void acceptFriendRequest(FriendRequest friendRequest) {
        if(!friendRequests.contains(friendRequest))
            throw new IllegalArgumentException("Friend request not found!");
        friendIds.add(friendRequest.playerFrom());
        // from user friends list updated automatically when saving
    }

    public List<AchievementProgress> achievementProgresses() {
        return Collections.unmodifiableList(achievementProgresses);
    }

    public List<AchievementProgress> getAchievementProgressForGame(GameId gameId) {
        return achievementProgresses.stream()
                .filter(ap -> ap.gameId().equals(gameId))
                .toList();
    }

    public void addAchievementProgress(GameId gameId, int amountNeeded, int progress, Achievement achievement) {
        Optional<AchievementProgress> existingProgress = getAchievementProgressForGame(gameId).stream().filter(ap -> ap.achievement().equals(achievement)).findAny();

        if (existingProgress.isPresent()) {
            existingProgress.get().addProgress(progress);
        } else {
            AchievementProgress newProgress = new AchievementProgress(userId(), gameId, amountNeeded, progress, achievement);
            achievementProgresses.add(newProgress);
        }
    }

    public void addNotification(Notification notification) {
        this.notifications.add(notification);
    }

    public void removeNotification(Notification notification) {
        this.notifications.remove(notification);
    }

    public void markNotificationAsRead(NotificationId notificationId) {
        this.notifications.stream()
                .filter(notification -> notification.id().equals(notificationId))
                .findFirst()
                .ifPresent(Notification::read);
    }

    public void addStatistic(Statistic statistic) {
        this.statistics.add(statistic);
    }

    public void addGameWon(GameId gameId) {
        this.statistics.stream().filter(s -> s.gameId().equals(gameId)).findAny().ifPresent(Statistic::addGameWon);
    }

    public void addGameLost(GameId gameId) {
        this.statistics.stream().filter(s -> s.gameId().equals(gameId)).findAny().ifPresent(Statistic::addGameLost);
    }
}
