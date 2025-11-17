package be.kdg.ipj3.shared.infrastructure.profile;

import be.kdg.ipj3.shared.domain.profile.Bio;
import be.kdg.ipj3.shared.domain.profile.DisplayName;
import be.kdg.ipj3.shared.domain.profile.Profile;
import be.kdg.ipj3.shared.domain.profile.Visibility;
import be.kdg.ipj3.shared.domain.user.UserId;
import be.kdg.ipj3.shared.domain.user.UserName;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "profiles")
public class ProfileJpaEntity {
    @Id
    private UUID userId;
    private String username;
    private String displayName;
    private String bio;
    @Enumerated(EnumType.STRING)
    private Visibility visibility;
    private boolean receiveEmails;
    private boolean receiveNotifications;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "profile_friends",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<ProfileJpaEntity> friends = new ArrayList<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AchievementProgressJpaEntity> achievementProgresses = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationJpaEntity> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StatisticJpaEntity> statistics = new ArrayList<>();

    @OneToMany(mappedBy = "playerFrom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendRequestJpaEntity> sentFriendRequests = new ArrayList<>();

    @OneToMany(mappedBy = "playerTo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendRequestJpaEntity> receivedFriendRequests = new ArrayList<>();

    public static ProfileJpaEntity fromDomain(Profile profile) {
        ProfileJpaEntity entity = new ProfileJpaEntity();
        entity.setUserId(profile.userId().userId());
        entity.setUsername(profile.username().username());
        entity.setDisplayName(profile.displayName().displayName());
        entity.setBio(profile.bio().bio());
        entity.setVisibility(profile.visibility());
        entity.setReceiveEmails(profile.receiveEmails());
        entity.setReceiveNotifications(profile.receiveNotifications());
        return entity;
    }

    public Profile toDomain() {

        return new Profile(
                new UserId(userId),
                new UserName(this.username),
                new DisplayName(this.displayName),
                new Bio(this.bio),
                this.visibility,
                this.receiveEmails,
                this.receiveNotifications,
                this.statistics.stream().map(StatisticJpaEntity::toDomain).toList()
        );
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public void setReceiveEmails(boolean receiveEmails) {
        this.receiveEmails = receiveEmails;
    }

    public void setReceiveNotifications(boolean receiveNotifications) {
        this.receiveNotifications = receiveNotifications;
    }

    public UUID getUserId() {
        return userId;
    }
}
