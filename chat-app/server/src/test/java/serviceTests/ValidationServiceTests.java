package serviceTests;

import com.jzprog.chatapp.src.Application;
import com.jzprog.chatapp.src.database.UsersRepository;
import com.jzprog.chatapp.src.model.Conversation;
import com.jzprog.chatapp.src.model.User;
import com.jzprog.chatapp.src.model.UserInfo;
import com.jzprog.chatapp.src.model.ValidationResponse;
import com.jzprog.chatapp.src.services.MessagingService;
import com.jzprog.chatapp.src.services.validation.ValidationStrategy;
import com.jzprog.chatapp.src.utils.SystemMessages;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.Date;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class ValidationServiceTests {

    @Autowired
    private ValidationStrategy validationStrategy;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MessagingService messagingService;

    @Test
    public void testUserAuthValidation() {
        ValidationResponse validationResponse = validationStrategy.provideValidation(SystemMessages.ValidationTypes.USER_EXISTENCE, null, "testUser");
        Assert.assertFalse(validationResponse.isSuccess());
        Assert.assertEquals(SystemMessages.USER_NOT_EXIST, validationResponse.getErrorMessage());
        User dbUser = createTestUser("testUser");
        UserInfo userInfo = new UserInfo(dbUser.getUsername(), dbUser.getPassword(), dbUser.getEmail());
        validationResponse = validationStrategy.provideValidation(SystemMessages.ValidationTypes.USER_EXISTENCE, userInfo);
        Assert.assertTrue(validationResponse.isSuccess());
        validationResponse = validationStrategy.provideValidation(SystemMessages.ValidationTypes.USER_EXISTENCE, null, "testUser");
        Assert.assertTrue(validationResponse.isSuccess());
    }

    @Test
    public void testUserRegistrationValidation() {
        UserInfo userInfo = new UserInfo("testUser2", null, null);
        ValidationResponse validationResponse = validationStrategy.provideValidation(SystemMessages.ValidationTypes.REGISTRATION_CHECK, userInfo);
        Assert.assertTrue(validationResponse.isSuccess());
        createTestUser("testUser2");
        validationResponse = validationStrategy.provideValidation(SystemMessages.ValidationTypes.REGISTRATION_CHECK, userInfo);
        Assert.assertFalse(validationResponse.isSuccess());
        Assert.assertEquals(SystemMessages.USER_ALREADY_REGISTERED, validationResponse.getErrorMessage());
    }

    @Test
    @Transactional
    public void testConversationValidation() {
        User dbUser = createTestUser("testUser3");
        ValidationResponse validationResponse = validationStrategy.provideValidation(SystemMessages.ValidationTypes.CONVERSATION_MEMBERSHIP, dbUser, "1");
        Assert.assertFalse(validationResponse.isSuccess());
        Assert.assertEquals(SystemMessages.USER_NO_ACCESS, validationResponse.getErrorMessage());

        Conversation conversation = messagingService.createNewConversation(dbUser.getId(), "testConversation", new Date(), Arrays.asList(dbUser.getUsername()));
        validationResponse = validationStrategy.provideValidation(SystemMessages.ValidationTypes.CONVERSATION_MEMBERSHIP, dbUser, String.valueOf(conversation.getId()));
        Assert.assertTrue(validationResponse.isSuccess());

        validationResponse = validationStrategy.provideValidation(SystemMessages.ValidationTypes.CONVERSATION_OWNERSHIP, dbUser, conversation);
        Assert.assertTrue(validationResponse.isSuccess());
    }

    private User createTestUser(String username) {
        User user = new User(username, "1234", "jz@test.com");
        usersRepository.save(user);
        return user;
    }
}
