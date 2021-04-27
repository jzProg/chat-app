package serviceTests;

import com.jzprog.chatapp.src.Application;
import com.jzprog.chatapp.src.database.UsersRepository;
import com.jzprog.chatapp.src.model.User;
import com.jzprog.chatapp.src.model.UserInfo;
import com.jzprog.chatapp.src.services.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class UserServiceTests {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UserService userService;

    @Test
    public void searchUserTest() {
        User searchResult = userService.searchForUserByUsername("testSearchUserServiceUser1");
        Assert.assertNull(searchResult);

        User user = createTestUser("testSearchUserServiceUser1");
        searchResult = userService.searchForUserByUsername("testSearchUserServiceUser1");
        Assert.assertEquals(user.toString(), searchResult.toString());

        searchResult = userService.searchForUserByUserId(user.getId());
        Assert.assertEquals(user.toString(), searchResult.toString());

        List<User> searchList = userService.searchForUsersMatchingString("gian");
        Assert.assertTrue(searchList.isEmpty());

        searchList = userService.searchForUsersMatchingString("testSearchUser");
        Assert.assertEquals(1, searchList.size());
        Assert.assertEquals(user.toString(), searchList.get(0).toString());

        User user2 = createTestUser("testSearchUserServiceUser2");
        searchList = userService.searchForUsersMatchingString("testSearchUser");
        Assert.assertEquals(2, searchList.size());
        User matchingUser = searchList.stream().filter(u -> u.getId().equals(user2.getId())).collect(Collectors.toList()).get(0);
        Assert.assertEquals(user2.toString(), matchingUser.toString());
    }

    @Test
    @Transactional
    public void createNewUserTest() {
        UserInfo userInfo = new UserInfo("testUserServiceUser4", "1234", "jz@test.com");
        userService.createNewUser(userInfo, "1234");

        User dbUser = usersRepository.findUserByUsername("testUserServiceUser4");
        Assert.assertEquals(userInfo.getUsername(), dbUser.getUsername());
        Assert.assertEquals(userInfo.getPassword(), dbUser.getPassword());
        Assert.assertEquals(userInfo.getEmail(), dbUser.getEmail());
    }

    @Test
    public void updateProfilePictureTest() {
        createTestUser("testUserServiceUser3");
        String fakeImage = "fakeImage";
        userService.updateProfileImage("testUserServiceUser3", fakeImage.getBytes());
        User user = usersRepository.findUserByUsername("testUserServiceUser3");
        Assert.assertEquals(fakeImage, new String(user.getImage()));
    }

    private User createTestUser(String username) {
        User user = new User(username, "1234", "jz@test.com");
        usersRepository.save(user);
        return user;
    }
}
