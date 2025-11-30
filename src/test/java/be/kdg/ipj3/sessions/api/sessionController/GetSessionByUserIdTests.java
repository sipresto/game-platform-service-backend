package be.kdg.ipj3.sessions.api.sessionController;

import be.kdg.ipj3.sessions.application.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest
public class GetSessionByUserIdTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SessionService sessionService;

    // TODO: first change the getSessionsByUserId method in controller, than update these tests
    /*@Test
    void getSessionsByUserId_shouldReturnListOfSessions_whenSessionsExist() throws Exception {
        // Arrange
        UserId userId = UserId.create();

        Session session = new Session(GameId.create(), List.of(userId), Difficulty.EASY, userId, SessionResult.PLAYER_WIN);

        List<Session> sessions = List.of(session);

        given(sessionService.findSessionsByUserId(any())).willReturn(sessions);

        // Act & Assert
        mockMvc.perform(get("/api/session/{userId}", userId.id()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].gameId").value(session.getGame().id().toString()))
                .andExpect(jsonPath("$[0].playerIds[0]").value(userId.id().toString()))
                .andExpect(jsonPath("$[0].difficulty").value(session.getDifficulty().name()))
                .andExpect(jsonPath("$[0].winnerId").value(session.getWinner().id().toString()))
                .andExpect(jsonPath("$[0].result").value(session.getResult().name()));

        then(sessionService).should().findSessionsByUserId(userId);
    }

    @Test
    void getSessionsByUserId_shouldReturnEmptyList_whenNoSessionsExist() throws Exception {
        // Arrange
        UserId userId = UserId.create();

        given(sessionService.findSessionsByUserId(any())).willReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/session/{userId}", userId.id()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        then(sessionService).should().findSessionsByUserId(userId);
    }*/
}
