package serviceTests;

import com.jzprog.chatapp.src.Application;
import com.jzprog.chatapp.src.database.ConversationsRepository;
import com.jzprog.chatapp.src.database.MessagesRepository;
import com.jzprog.chatapp.src.database.UsersRepository;
import com.jzprog.chatapp.src.model.Conversation;
import com.jzprog.chatapp.src.model.Message;
import com.jzprog.chatapp.src.model.User;
import com.jzprog.chatapp.src.services.MessagingService;
import com.jzprog.chatapp.src.services.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class MessagingServiceTests {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MessagingService messagingService;

    @Autowired
    private ConversationsRepository conversationsRepository;

    @Autowired
    private MessagesRepository messagesRepository;

    @Before
    public void setup() {
        createTestUser("testMessagingUser");
        createTestUser("testMessagingUser2");
        createTestUser("testMessagingUser3");
    }

    @Test
    @Transactional
    public void createConversationTest() {
        User user1 = userService.searchForUserByUsername("testMessagingUser");
        User user2 = userService.searchForUserByUsername("testMessagingUser2");
        Conversation newConversation = messagingService.createNewConversation(user1.getId(), "testConversation", new Date(), Arrays.asList(user1.getUsername(), user2.getUsername()));

        Conversation newConversationDB = conversationsRepository.findById(newConversation.getId());

        Set<User> members = newConversationDB.getUsers();
        Assert.assertEquals(2, members.size());
        Assert.assertTrue(members.contains(user1));
        Assert.assertTrue(members.contains(user2));
    }

    @Test
    public void addNewMessageToConversationTest() {
        User user1 = userService.searchForUserByUsername("testMessagingUser2");
        User user2 = userService.searchForUserByUsername("testMessagingUser3");
        Conversation newConversation = messagingService.createNewConversation(user1.getId(), "testConversation2", new Date(), Arrays.asList(user1.getUsername(), user2.getUsername()));

        Message message = messagingService.addNewMessageToConversation(newConversation.getId(), "hello", new Date(), user1.getId());

        Message messageBD = messagesRepository.findById(message.getId());
        Assert.assertEquals("hello", messageBD.getText());
        Assert.assertEquals(user1.getId(), messageBD.getPostedBy());
        Assert.assertEquals(newConversation.getId(), messageBD.getConversation().getId());
    }

    @Test
    @Transactional
    public void removeConversationTest() {
        User user1 = userService.searchForUserByUsername("testMessagingUser2");
        User user2 = userService.searchForUserByUsername("testMessagingUser3");
        Conversation newConversation = messagingService.createNewConversation(user1.getId(), "testConversation3", new Date(), Arrays.asList(user1.getUsername(), user2.getUsername()));

        messagingService.deleteConversation(newConversation.getId());
        Conversation conversationDB = conversationsRepository.findById(newConversation.getId());
        Assert.assertNull(conversationDB);
    }

    @Test
    @Transactional
    public void removeConversationMemberTest() {
        User user1 = userService.searchForUserByUsername("testMessagingUser2");
        User user2 = userService.searchForUserByUsername("testMessagingUser3");
        Conversation newConversation = messagingService.createNewConversation(user1.getId(), "testConversation4", new Date(), Arrays.asList(user1.getUsername(), user2.getUsername()));

        messagingService.removeConversationMember(newConversation.getId(), user2);
        Conversation conversationDB = conversationsRepository.findById(newConversation.getId());

        Set<User> members = conversationDB.getUsers();
        Assert.assertEquals(1, members.size());
        Assert.assertTrue(members.contains(user1));
        Assert.assertFalse(members.contains(user2));
    }

    @Test
    @Transactional
    public void addConversationMemberTest() {
        User user1 = userService.searchForUserByUsername("testMessagingUser2");
        User user2 = userService.searchForUserByUsername("testMessagingUser3");
        Conversation newConversation = messagingService.createNewConversation(user1.getId(), "testConversation4", new Date(), Arrays.asList(user1.getUsername(), user2.getUsername()));

        User user3 = userService.searchForUserByUsername("testMessagingUser");
        messagingService.addMemberToConversation(newConversation.getId(), Arrays.asList(user3.getUsername()));
        Conversation conversationDB = conversationsRepository.findById(newConversation.getId());

        Set<User> members = conversationDB.getUsers();
        Assert.assertEquals(3, members.size());
        Assert.assertTrue(members.contains(user1));
        Assert.assertTrue(members.contains(user2));
        Assert.assertTrue(members.contains(user3));
    }

    private void createTestUser(String username) {
        User user = new User(username, "1234", "jz@test.com");
        usersRepository.save(user);
    }
}
