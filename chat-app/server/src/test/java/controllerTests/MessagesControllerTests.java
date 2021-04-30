package controllerTests;

import com.jzprog.chatapp.src.Application;
import com.jzprog.chatapp.src.model.User;
import com.jzprog.chatapp.src.model.ValidationResponse;
import com.jzprog.chatapp.src.services.MessagingService;
import com.jzprog.chatapp.src.services.UserService;
import com.jzprog.chatapp.src.services.validation.ValidationStrategy;
import com.jzprog.chatapp.src.utils.JwtUtil;
import com.jzprog.chatapp.src.utils.SystemMessages;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class MessagesControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtTokenUtil;

    @MockBean
    private MessagingService messagingService;

    @MockBean
    private UserService userService;

    @MockBean
    private ValidationStrategy validationStrategy;

    @Before
    public void setup() {
        User testUser = createTestUser();

        given(jwtTokenUtil.getUsernameFromToken("1234")).willReturn("giannis");
        given(userService.searchForUserByUsername("giannis")).willReturn(testUser);

        given(jwtTokenUtil.getUsernameFromToken("12345")).willReturn("giannis2");
        given(userService.searchForUserByUsername("giannis2")).willReturn(null);
    }

    @Test
    public void getMessagesTest() throws Exception {
        ValidationResponse validationResponseSuccess = new ValidationResponse(true, null);
        given(validationStrategy.provideValidation(eq(SystemMessages.ValidationTypes.CONVERSATION_MEMBERSHIP), anyObject(), eq("12"))).willReturn(validationResponseSuccess);
        mockMvc.perform(get("/api/messages/getConversationMessages")
                .param("id","12")
                .header("Authorization", "Bearer 1234"))
                .andExpect(status().isOk());
        verify(userService, times(1)).searchForUserByUsername("giannis");
        verify(messagingService, times(1)).fetchConversationMessages(12, 0, 20);
    }

    @Test
    public void getMessagesUnauthorizedTest() throws Exception {
        ValidationResponse validationResponseSuccess = new ValidationResponse(false, null);
        given(validationStrategy.provideValidation(eq(SystemMessages.ValidationTypes.CONVERSATION_MEMBERSHIP), anyObject(), eq("12"))).willReturn(validationResponseSuccess);
        mockMvc.perform(get("/api/messages/getConversationMessages")
                .param("id","12")
                .header("Authorization", "Bearer 1234"))
                .andExpect(status().isUnauthorized());
        verify(userService, times(1)).searchForUserByUsername("giannis");
    }

    @Test
    public void getConversationsTest() throws Exception {
        ValidationResponse validationResponseSuccess = new ValidationResponse(true, null);
        given(validationStrategy.provideValidation(eq(SystemMessages.ValidationTypes.USER_EXISTENCE), anyObject(), eq("giannis"))).willReturn(validationResponseSuccess);
        mockMvc.perform(get("/api/messages/getConversations")
                .param("page","0")
                .header("Authorization", "Bearer 1234"))
                .andExpect(status().isOk());
        verify(jwtTokenUtil, times(1)).getUsernameFromToken("1234");
        verify(messagingService, times(1)).fetchUsersConversations("giannis", 0);
    }

    @Test
    public void getConversationsUnauthorizedTest() throws Exception {
        ValidationResponse validationResponseSuccess = new ValidationResponse(false, null);
        given(validationStrategy.provideValidation(eq(SystemMessages.ValidationTypes.USER_EXISTENCE), anyObject(), eq("giannis"))).willReturn(validationResponseSuccess);
        mockMvc.perform(get("/api/messages/getConversations")
                .param("page","0")
                .header("Authorization", "Bearer 1234"))
                .andExpect(status().isUnauthorized());
        verify(jwtTokenUtil, times(1)).getUsernameFromToken("1234");
    }

    private User createTestUser() {
        User user = new User("giannis", "1234", "jz@test.com");
        user.setId(1);
        return user;
    }
}
