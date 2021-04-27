package serviceTests;

import com.jzprog.chatapp.src.Application;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Ignore
@RunWith(SpringRunner.class)
public class UserServiceTests {
}
