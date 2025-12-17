package be.kdg.ipj3.users.api;

import be.kdg.ipj3.users.domain.FriendRequest;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.Visibility;
import be.kdg.ipj3.users.infrastructure.jpa.JpaProfile;
import be.kdg.ipj3.users.infrastructure.jpa.JpaUserEntity;
import be.kdg.ipj3.users.infrastructure.jpa.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class AcceptFriendRequestIntegrationTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private JpaUserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    private JpaUserEntity createUser(String keycloakId, String username) {
        return userRepository.save(new JpaUserEntity(
                UUID.randomUUID(), keycloakId, username,
                new JpaProfile(username, "Bio"), 0, Visibility.PUBLIC
        ));
    }

    private JwtRequestPostProcessor validJwt() {
        return jwt().jwt(b -> b.subject("kc-receiver").claim("preferred_username", "user"))
                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER"));
    }

    @Test
    @Transactional
    void acceptFriendRequest_ShouldSucceed_WhenRequestExists() throws Exception {
        // Arrange
        JpaUserEntity senderEntity = createUser("kc-sender", "sender");
        JpaUserEntity receiverEntity = createUser("kc-receiver", "receiver");

        User receiverDomain = receiverEntity.toDomain();
        User senderDomain = senderEntity.toDomain();

        receiverDomain.addFriendRequest(FriendRequest.create(senderDomain.getId()));
        userRepository.save(JpaUserEntity.fromDomain(receiverDomain));

        // Act
        mockMvc.perform(put("/api/acceptFriendRequest/{userId}", senderEntity.getId())
                        .with(validJwt()))
                .andExpect(status().isNoContent());

        // Assert
        JpaUserEntity updatedReceiver = userRepository.findById(receiverEntity.getId()).orElseThrow();
        JpaUserEntity updatedSender = userRepository.findById(senderEntity.getId()).orElseThrow();

        assertThat(updatedReceiver.getFriends()).contains(senderEntity.getId());
        assertThat(updatedSender.getFriends()).contains(receiverEntity.getId());

        assertThat(updatedReceiver.toDomain().getFriendRequests()).isEmpty();
    }

    @Test
    void acceptFriendRequest_ShouldFail_WhenNoRequestExists() throws Exception {
        // Arrange
        JpaUserEntity sender = createUser("kc-sender", "sender");
        JpaUserEntity receiver = createUser("kc-receiver", "receiver");

        // Act & Assert
        mockMvc.perform(put("/api/acceptFriendRequest/{userId}", sender.getId())
                        .with(validJwt()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void acceptFriendRequest_ShouldFail_WhenSenderDoesNotExist() throws Exception {
        // Arrange
        createUser("kc-receiver", "receiver");

        // Act
        mockMvc.perform(put("/api/acceptFriendRequest/{userId}", UUID.randomUUID())
                        .with(validJwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void acceptFriendRequest_ShouldFail_WhenUnauthorized() throws Exception {
        mockMvc.perform(put("/api/acceptFriendRequest/{userId}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }
}