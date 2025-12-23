package be.kdg.ipj3.social.api.chatController;

import be.kdg.ipj3.social.api.dto.CreateChatDto;
import be.kdg.ipj3.social.domain.Chat;
import be.kdg.ipj3.social.domain.ChatId;
import be.kdg.ipj3.social.domain.ChatRepository;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.UserRepository;
import be.kdg.ipj3.users.shared.UserId;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class CreateChatTests {

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

    @Test
    void createChat_shouldReturnChatDto_whenValidRequest() throws Exception {
        // Arrange
        UserId userId1 = createTestUser1();
        UserId userId2 = createTestUser2();

        CreateChatDto dto = new CreateChatDto(Set.of(userId2.id()));

        // Act
        var result = mockMvc.perform(post("/api/chats")
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.participants[*].id",
                        containsInAnyOrder(
                                userId1.id().toString(),
                                userId2.id().toString()
                        )))
                .andReturn();

        // Assert
        String responseBody = result.getResponse().getContentAsString();
        String chatId = JsonPath.parse(responseBody).read("$.id");

        Optional<Chat> optionalChat = chatRepository.findById(new ChatId(UUID.fromString(chatId)));

        assertThat(optionalChat).isPresent();

        Chat chat = optionalChat.get();
        assertThat(chat.getParticipants()).contains(userId1, userId2);
    }

    @Test
    void createChat_shouldReturnUnauthorized_whenUserNotLoggedIn() throws Exception {
        // Arrange
        UserId userId1 = createTestUser1();
        UserId userId2 = createTestUser2();

        CreateChatDto dto = new CreateChatDto(Set.of(userId2.id()));

        // Act & Assert
        mockMvc.perform(post("/api/chats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createChat_shouldReturnBadRequest_whenInvalidParticipant() throws Exception {
        // Arrange
        UserId userId1 = createTestUser1();
        UserId userId2 = UserId.create();

        CreateChatDto dto = new CreateChatDto(Set.of(userId2.id()));

        // Act & Assert
        mockMvc.perform(post("/api/chats")
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createChat_shouldReturnExistingChat_whenChatExists() throws Exception {
        // Arrange
        UserId userId1 = createTestUser1();
        UserId userId2 = createTestUser2();

        Chat chat = new Chat(null, Set.of(userId1, userId2));
        chatRepository.save(chat);

        CreateChatDto dto = new CreateChatDto(Set.of(userId2.id()));

        // Act & Assert
        mockMvc.perform(post("/api/chats")
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.participants[*].id",
                        containsInAnyOrder(
                                userId1.id().toString(),
                                userId2.id().toString()
                        )))
                .andExpect(jsonPath("$.id").value(chat.getId().id().toString()));
    }
}
