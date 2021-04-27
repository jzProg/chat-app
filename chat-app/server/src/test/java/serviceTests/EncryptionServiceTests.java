package serviceTests;

import com.jzprog.chatapp.src.Application;
import com.jzprog.chatapp.src.services.encryption.EncryptionService;
import com.jzprog.chatapp.src.utils.EncodingHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class EncryptionServiceTests {

    @Autowired
    @Qualifier("aes")
    private EncryptionService aesEncryption;

    @Autowired
    @Qualifier("md5")
    private EncryptionService md5Hashing;

    @Autowired
    private EncodingHelper encodingHelper;

    @Test
    public void testMessageEncryption() throws Exception {
        String MESSAGES_ENCRYPT_KEY = "giannis";
        String messageToEncrypt = "hello all";
        aesEncryption.setKeys(MESSAGES_ENCRYPT_KEY);
        String result = encodingHelper.getUrlDecodedMessage(aesEncryption.encrypt(messageToEncrypt, true));
        String initial = aesEncryption.decrypt(result, true);
        Assert.assertEquals(messageToEncrypt, initial);
    }

    @Test
    public void testAuthenticationEncryption() throws Exception {
        String messageToEncrypt = "12345";
        String firstHash = md5Hashing.encrypt(messageToEncrypt, false);
        String secondHash = md5Hashing.encrypt(messageToEncrypt, false);
        Assert.assertEquals(firstHash, secondHash);
    }

}
