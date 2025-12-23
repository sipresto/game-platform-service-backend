package be.kdg.ipj3.social.api.chatController;

import be.kdg.ipj3.social.api.dto.SendMessageDto;
import be.kdg.ipj3.social.domain.Chat;
import be.kdg.ipj3.social.domain.ChatRepository;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.UserRepository;
import be.kdg.ipj3.users.shared.UserId;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class SendMessageTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        chatRepository.deleteAll();
        userRepository.deleteAll();
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt() {
        return jwt().jwt(builder -> builder
                .subject("keycloak-id")
                .claim("preferred_username", "test-user"));
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt2() {
        return jwt().jwt(builder -> builder
                .subject("keycloak-id-2")
                .claim("preferred_username", "test-user-2"));
    }

    private UserId createTestUser1() {
        User user = User.createFromKeycloak("keycloak-id", "test-user", "test@gmail.com");
        userRepository.save(user);
        return user.getId();
    }

    private UserId createTestUser2() {
        User user = User.createFromKeycloak("keycloak-id-2", "test-user-2", "test2@gmail.com");
        userRepository.save(user);
        return user.getId();
    }

    private UserId createTestUser3() {
        User user = User.createFromKeycloak("keycloak-id-3", "test-user-3", "test3@gmail.com");
        userRepository.save(user);
        return user.getId();
    }

    @Test
    void sendMessage_shouldAddMultipleMessages_whenCalledMultipleTimes() throws Exception {
        // Arrange
        UserId userId1 = createTestUser1();
        UserId userId2 = createTestUser2();

        Chat chat = new Chat(Set.of(userId1, userId2));
        chatRepository.save(chat);

        SendMessageDto message1 = new SendMessageDto("First message");
        SendMessageDto message2 = new SendMessageDto("Second message");
        SendMessageDto message3 = new SendMessageDto("Third message");

        // Act
        mockMvc.perform(post("/api/chats/" + chat.getId().id() + "/message")
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(message1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/chats/" + chat.getId().id() + "/message")
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(message2)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/chats/" + chat.getId().id() + "/message")
                        .with(validJwt2())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(message3)))
                .andExpect(status().isOk());

        // Assert
        Chat updatedChat = chatRepository.findById(chat.getId()).orElseThrow();
        assertThat(updatedChat.getMessages()).hasSize(3);
        assertThat(updatedChat.getMessages().get(0).getContent()).isEqualTo("First message");
        assertThat(updatedChat.getMessages().get(0).getSender()).isEqualTo(userId1);
        assertThat(updatedChat.getMessages().get(1).getContent()).isEqualTo("Second message");
        assertThat(updatedChat.getMessages().get(2).getContent()).isEqualTo("Third message");
        assertThat(updatedChat.getMessages().get(2).getSender()).isEqualTo(userId2);
        assertThat(updatedChat.getLastMessageAt()).isNotNull();
    }

    @Test
    void sendMessage_shouldReturnForbidden_whenUserNotParticipant() throws Exception {
        // Arrange
        UserId userId1 = createTestUser1();
        UserId userId2 = createTestUser2();
        UserId userId3 = createTestUser3();

        Chat chat = new Chat(Set.of(userId2, userId3));
        chatRepository.save(chat);

        SendMessageDto dto = new SendMessageDto("Unauthorized message");

        // Act & Assert
        mockMvc.perform(post("/api/chats/" + chat.getId().id() + "/message")
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());

        Chat updatedChat = chatRepository.findById(chat.getId()).orElseThrow();
        assertThat(updatedChat.getMessages()).isEmpty();
    }

    @Test
    void sendMessage_shouldReturnNotFound_whenChatDoesNotExist() throws Exception {
        // Arrange
        createTestUser1();

        SendMessageDto dto = new SendMessageDto("Message to non-existent chat");

        // Act & Assert
        mockMvc.perform(post("/api/chats/" + UUID.randomUUID() + "/message")
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void sendMessage_shouldReturnUnauthorized_whenUserNotLoggedIn() throws Exception {
        // Arrange
        UserId userId1 = createTestUser1();
        UserId userId2 = createTestUser2();

        Chat chat = new Chat(Set.of(userId1, userId2));
        chatRepository.save(chat);

        SendMessageDto dto = new SendMessageDto("Unauthenticated message");

        // Act & Assert
        mockMvc.perform(post("/api/chats/" + chat.getId().id() + "/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void sendMessage_shouldSetCorrectSender_whenDifferentUsersPost() throws Exception {
        // Arrange
        UserId userId1 = createTestUser1();
        UserId userId2 = createTestUser2();

        Chat chat = new Chat(Set.of(userId1, userId2));
        chatRepository.save(chat);

        SendMessageDto dto = new SendMessageDto("Message from user 2");

        // Act
        mockMvc.perform(post("/api/chats/" + chat.getId().id() + "/message")
                        .with(validJwt2()) // User 2 sending
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        // Assert
        Chat updatedChat = chatRepository.findById(chat.getId()).orElseThrow();
        assertThat(updatedChat.getMessages()).hasSize(1);
        assertThat(updatedChat.getMessages().getFirst().getSender()).isEqualTo(userId2);
    }
}