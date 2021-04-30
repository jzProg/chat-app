package controllerTests;

import com.jzprog.chatapp.src.Application;
import com.jzprog.chatapp.src.model.ConversationDTO;
import com.jzprog.chatapp.src.model.NotificationDTO;
import com.jzprog.chatapp.src.model.User;
import com.jzprog.chatapp.src.services.MessagingService;
import com.jzprog.chatapp.src.services.PushNotificationService;
import com.jzprog.chatapp.src.services.UserService;
import com.jzprog.chatapp.src.utils.JwtUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJsonTesters
@ActiveProfiles("test")
public class PushNotificationsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtTokenUtil;

    @MockBean
    private MessagingService messagingService;

    @MockBean
    private UserService userService;

    @MockBean
    private PushNotificationService pushNotificationService;

    @Autowired
    private JacksonTester<NotificationDTO> notificationDTOJacksonTester;

    @Autowired
    private JacksonTester<ConversationDTO> conversationDTOJacksonTester;

    @Before
    public void setup() {
        User testUser = createTestUser();

        given(jwtTokenUtil.getUsernameFromToken("1234")).willReturn("giannis");
        given(userService.searchForUserByUsername("giannis")).willReturn(testUser);
        given(pushNotificationService.storePushNotificationSubInfo(any(), any())).willReturn(2);

        given(jwtTokenUtil.getUsernameFromToken("12345")).willReturn("giannis2");
        given(userService.searchForUserByUsername("giannis2")).willReturn(null);
    }

    @Test
    public void storePushInfoTest() throws Exception {
       NotificationDTO notificationDTO = new NotificationDTO();
       notificationDTO.setPushSubInfo("12345");
       MvcResult result = mockMvc.perform(post("/api/notifications/sendPushSubInfo")
               .content(notificationDTOJacksonTester.write(notificationDTO).getJson())
               .contentType(MediaType.APPLICATION_JSON_UTF8)
               .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer 1234"))
                .andExpect(status().isOk()).andReturn();
        String actualJson = result.getResponse().getContentAsString();
        Assert.assertEquals(2 , Integer.parseInt(actualJson));
        verify(pushNotificationService, times(1)).storePushNotificationSubInfo(any(), any());
        verify(userService, times(1)).searchForUserByUsername("giannis");
    }

    @Test
    public void storePushInfoUserNotExistCaseTest() throws Exception {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setPushSubInfo("12345");
        mockMvc.perform(post("/api/notifications/sendPushSubInfo")
                .content(notificationDTOJacksonTester.write(notificationDTO).getJson())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer 12345"))
                .andExpect(status().isNoContent());
        verify(pushNotificationService, times(0)).storePushNotificationSubInfo(any(), any());
    }

    @Test
    public void broadcastMessageTest() throws Exception {
        ConversationDTO conversationDTO = new ConversationDTO();
        conversationDTO.setId(1);
        mockMvc.perform(post("/api/notifications/sendMessageEvent")
                .content(conversationDTOJacksonTester.write(conversationDTO).getJson())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer 1234"))
                .andExpect(status().isOk());
        verify(userService, times(1)).searchForUserByUsername("giannis");
    }

    @Test
    public void broadcastLoginTest() throws Exception {
        mockMvc.perform(post("/api/notifications/sendLoginEvent")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer 1234"))
                .andExpect(status().isOk());
        verify(userService, times(1)).searchForUserByUsername("giannis");
    }

    private User createTestUser() {
        User user = new User("giannis", "1234", "jz@test.com");
        user.setId(1);
        return user;
    }
}
