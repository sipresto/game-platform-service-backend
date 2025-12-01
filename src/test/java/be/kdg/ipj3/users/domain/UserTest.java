package be.kdg.ipj3.users.domain;

import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("User Domain")
class UserTest {

    @Nested
    @DisplayName("Given a user needs to be created")
    class UserCreation {

        @Test
        @DisplayName("When creating user with profile only Then user should be created with empty games list")
        void createUser_withProfileOnly() {
            // Given
            UserId userId = new UserId(UUID.randomUUID());
            String keycloakId = "keycloak-123";
            String username = "testuser";
            Profile profile = new Profile("Test User", "Test Bio");

            // When
            User user = new User(userId, keycloakId, username, profile);

            // Then
            assertThat(user.getId()).isEqualTo(userId);
            assertThat(user.getKeycloakId()).isEqualTo(keycloakId);
            assertThat(user.getUsername()).isEqualTo(username);
            assertThat(user.getOwnedGames()).isEmpty();
            assertThat(user.getProfile()).isEqualTo(profile);
        }

        @Test
        @DisplayName("When creating user from Keycloak credentials Then user should be created with Keycloak username as display name")
        void createUser_fromKeycloak() {
            // Given
            String keycloakId = "keycloak-123";
            String keycloakUsername = "keycloak_user";

            // When
            User user = User.createFromKeycloak(keycloakId, keycloakUsername);

            // Then
            assertThat(user.getId()).isNotNull();
            assertThat(user.getKeycloakId()).isEqualTo(keycloakId);
            assertThat(user.getUsername()).isEqualTo(keycloakUsername);
            assertThat(user.getOwnedGames()).isEmpty();
            assertThat(user.getProfile().displayName()).isEqualTo(keycloakUsername);
            assertThat(user.getProfile().bio()).isEmpty();
        }

        @Test
        @DisplayName("When creating multiple users from Keycloak Then each user should have unique ID")
        void createMultipleUsers_fromKeycloak() {
            // Given
            String keycloakId1 = "keycloak-1";
            String keycloakId2 = "keycloak-2";

            // When
            User user1 = User.createFromKeycloak(keycloakId1, "user1");
            User user2 = User.createFromKeycloak(keycloakId2, "user2");

            // Then
            assertThat(user1.getId()).isNotEqualTo(user2.getId());
        }
    }

    @Nested
    @DisplayName("Given a user exists")
    class ExistingUser {

        @Nested
        @DisplayName("When updating profile")
        class ProfileUpdate {

            @Test
            @DisplayName("Then both profile and username should be updated")
            void updateProfile_withValidData() {
                // Given
                User user = createTestUser();
                Profile newProfile = new Profile("New Display Name", "New Bio");

                // When
                user.updateProfile(newProfile);

                // Then
                assertThat(user.getProfile()).isEqualTo(newProfile);
                assertThat(user.getUsername()).isEqualTo("New Display Name");
            }

            @Test
            @DisplayName("When new display name is blank Then current display name should be preserved")
            void updateProfile_withBlankDisplayName() {
                // Given
                User user = createTestUser();
                String originalDisplayName = user.getProfile().displayName();
                Profile newProfile = new Profile("   ", "New Bio");

                // When
                user.updateProfile(newProfile);

                // Then
                assertThat(user.getProfile().displayName()).isEqualTo(originalDisplayName);
                assertThat(user.getProfile().bio()).isEqualTo("New Bio");
                assertThat(user.getUsername()).isEqualTo(originalDisplayName);
            }

            @Test
            @DisplayName("When new bio is null Then current bio should be preserved")
            void updateProfile_withNullBio() {
                // Given
                User user = createTestUser();
                String originalBio = user.getProfile().bio();
                Profile newProfile = new Profile("New Display Name", null);

                // When
                user.updateProfile(newProfile);

                // Then
                assertThat(user.getProfile().displayName()).isEqualTo("New Display Name");
                assertThat(user.getProfile().bio()).isEqualTo(originalBio);
                assertThat(user.getUsername()).isEqualTo("New Display Name");
            }
        }

        @Nested
        @DisplayName("When managing games")
        class GameManagement {

            @Test
            @DisplayName("When adding a new game Then game should be added to owned games")
            void addGame_withNewGame() {
                // Given
                User user = createTestUser();
                GameId gameId = new GameId(UUID.randomUUID());

                // When
                user.addGame(gameId);

                // Then
                assertThat(user.getOwnedGames()).containsExactly(gameId);
            }

            @Test
            @DisplayName("When adding an existing game Then exception should be thrown")
            void addGame_withExistingGame() {
                // Given
                User user = createTestUser();
                GameId gameId = new GameId(UUID.randomUUID());
                user.addGame(gameId);

                // When & Then
                assertThatThrownBy(() -> user.addGame(gameId))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("Game already owned by this user");
            }

            @Test
            @DisplayName("When adding multiple different games Then all games should be added")
            void addGame_withMultipleGames() {
                // Given
                User user = createTestUser();
                GameId gameId1 = new GameId(UUID.randomUUID());
                GameId gameId2 = new GameId(UUID.randomUUID());
                GameId gameId3 = new GameId(UUID.randomUUID());

                // When
                user.addGame(gameId1);
                user.addGame(gameId2);
                user.addGame(gameId3);

                // Then
                assertThat(user.getOwnedGames()).containsExactlyInAnyOrder(gameId1, gameId2, gameId3);
            }
        }

        @Nested
        @DisplayName("When accessing properties")
        class PropertyAccess {

            @Test
            @DisplayName("Then user ID should be immutable")
            void userId_shouldBeImmutable() {
                // Given
                UserId originalId = new UserId(UUID.randomUUID());
                User user = new User(originalId, "keycloak-123", "testuser", new Profile("Test", "Bio"));

                // When & Then
                assertThat(user.getId()).isEqualTo(originalId);
            }

            @Test
            @DisplayName("Then keycloak ID should be immutable")
            void keycloakId_shouldBeImmutable() {
                // Given
                String originalKeycloakId = "keycloak-123";
                User user = new User(new UserId(UUID.randomUUID()), originalKeycloakId, "testuser", new Profile("Test", "Bio"));

                // When & Then
                assertThat(user.getKeycloakId()).isEqualTo(originalKeycloakId);
            }
        }
    }

    private User createTestUser() {
        return new User(
                new UserId(UUID.randomUUID()),
                "test-keycloak-id",
                "testuser",
                new Profile("Test User", "Test Bio")
        );
    }
}