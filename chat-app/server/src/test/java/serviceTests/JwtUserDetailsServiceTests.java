package serviceTests;

import com.jzprog.chatapp.src.Application;
import com.jzprog.chatapp.src.database.UsersRepository;
import com.jzprog.chatapp.src.model.User;
import com.jzprog.chatapp.src.services.JwtUserDetailsService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class JwtUserDetailsServiceTests {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private UsersRepository usersRepository;

    @Test
    public void loadUserByUsernameHappyPathTest() {
        User dbUser = createTestUser();
        UserDetails user = jwtUserDetailsService.loadUserByUsername(dbUser.getUsername());
        Assert.assertEquals(dbUser.getUsername(), user.getUsername());
        Assert.assertEquals(dbUser.getPassword(), user.getPassword());
        Assert.assertTrue(user.getAuthorities().isEmpty());
    }

    @Test(expected = Exception.class)
    public void loadUserByUsernameUserNotExistsCaseTest() {
        jwtUserDetailsService.loadUserByUsername("testUser2");
    }

    private User createTestUser() {
        User user = new User("testUser", "1234", "jz@test.com");
        usersRepository.save(user);
        return user;
    }
}
