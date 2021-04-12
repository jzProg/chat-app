import com.jzprog.chatapp.src.Application;
import com.jzprog.chatapp.src.services.encryption.EncryptionService;
import com.jzprog.chatapp.src.utils.EncodingHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
public class ChatAppTests {

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private EncodingHelper encodingHelper;

    @Test
    public void testMessageEncryption() throws Exception {
        String MESSAGES_ENCRYPT_KEY = "giannis";
        String messageToEncrypt = "hello all";
        encryptionService.setKeys(MESSAGES_ENCRYPT_KEY);
        String result = encodingHelper.getUrlDecodedMessage(encryptionService.encrypt(messageToEncrypt, true));
        String initial = encryptionService.decrypt(result, true);
        Assert.assertEquals(messageToEncrypt, initial);
    }

}
