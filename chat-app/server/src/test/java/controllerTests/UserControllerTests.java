package controllerTests;

import com.jzprog.chatapp.src.Application;
import com.jzprog.chatapp.src.model.*;
import com.jzprog.chatapp.src.services.JwtUserDetailsService;
import com.jzprog.chatapp.src.services.MessagingService;
import com.jzprog.chatapp.src.services.UserService;
import com.jzprog.chatapp.src.services.validation.ValidationStrategy;
import com.jzprog.chatapp.src.utils.JwtUtil;
import com.jzprog.chatapp.src.utils.SystemMessages;
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
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJsonTesters
@ActiveProfiles("test")
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtTokenUtil;

    @MockBean
    private UserService userService;

    @MockBean
    private ValidationStrategy validationStrategy;

    @MockBean
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private JacksonTester<UserInfo> userInfoJacksonTester;

    @Before
    public void setup() {
        User testUser = createTestUser();

        given(jwtTokenUtil.getUsernameFromToken("1234")).willReturn("testUser");
        given(userService.searchForUserByUsername("testUser")).willReturn(testUser);
        given(userDetailsService.loadUserByUsername(anyString())).willReturn(null);
        given(jwtTokenUtil.generateToken(any())).willReturn("12345");

    }

    @Test
    public void userAuthHappyPathTest() throws Exception {
        ValidationResponse validationResponseSuccess = new ValidationResponse(true, null);
        given(validationStrategy.provideValidation(eq(SystemMessages.ValidationTypes.USER_EXISTENCE), anyObject())).willReturn(validationResponseSuccess);
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername("testUser");
        userInfo.setPassword("1234");
        userInfo.setEmail("jz@test.com");
        MvcResult result = mockMvc.perform(post("/api/user/auth")
                .content(userInfoJacksonTester.write(userInfo).getJson())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk()).andReturn();
        Assert.assertEquals("testUser", userInfoJacksonTester.parse(result.getResponse().getContentAsString()).getObject().getUsername());
        verify(validationStrategy, times(1)).provideValidation(SystemMessages.ValidationTypes.USER_EXISTENCE, userInfo);
    }

    @Test
    public void userAuthFailureTest() throws Exception {
        ValidationResponse validationResponseFail = new ValidationResponse(false, null);
        given(validationStrategy.provideValidation(eq(SystemMessages.ValidationTypes.USER_EXISTENCE), anyObject())).willReturn(validationResponseFail);
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername("giannis");
        userInfo.setPassword("1234");
        userInfo.setEmail("jz@test.com");
        mockMvc.perform(post("/api/user/auth")
                .content(userInfoJacksonTester.write(userInfo).getJson())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isUnauthorized());
        verify(validationStrategy, times(1)).provideValidation(SystemMessages.ValidationTypes.USER_EXISTENCE, userInfo);
    }

    @Test
    public void registerUserHappyPathTest() throws Exception {
        ValidationResponse validationResponseFail = new ValidationResponse(true, null);
        given(validationStrategy.provideValidation(eq(SystemMessages.ValidationTypes.REGISTRATION_CHECK), anyObject())).willReturn(validationResponseFail);
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername("giannis");
        userInfo.setPassword("1234");
        userInfo.setEmail("jz@test.com");
        mockMvc.perform(post("/api/user/registerUser")
                .content(userInfoJacksonTester.write(userInfo).getJson())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
        verify(validationStrategy, times(1)).provideValidation(SystemMessages.ValidationTypes.REGISTRATION_CHECK, userInfo);
        verify(userService, times(1)).createNewUser(any(), anyString());
    }

    @Test
    public void registerUserFailureTest() throws Exception {
        ValidationResponse validationResponseFail = new ValidationResponse(false, null);
        given(validationStrategy.provideValidation(eq(SystemMessages.ValidationTypes.REGISTRATION_CHECK), anyObject())).willReturn(validationResponseFail);
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername("giannis");
        userInfo.setPassword("1234");
        userInfo.setEmail("jz@test.com");
        mockMvc.perform(post("/api/user/registerUser")
                .content(userInfoJacksonTester.write(userInfo).getJson())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isFound());
        verify(validationStrategy, times(1)).provideValidation(SystemMessages.ValidationTypes.REGISTRATION_CHECK, userInfo);
    }

    private User createTestUser() {
        User user = new User("testUser", "1234", "jz@test.com");
        user.setId(1);
        return user;
    }
}
