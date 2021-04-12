package com.jzprog.chatapp.src.services.encryption;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service("aes")
public class AesEncryptionService implements EncryptionService {

    private final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private final byte[] IV = new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    private byte[] keyBinary = new byte[]{};

    @Override
    public void setKeys(String psk) {
        this.keyBinary = psk.getBytes();
    }

    @Override
    public String encrypt(String message, boolean transformKey) throws Exception {
        final Cipher cipher = Cipher.getInstance(ALGORITHM);
        if (transformKey) {
            keyBinary = getFixedKey(cipher.getBlockSize());
        }
        SecretKey key = new SecretKeySpec(keyBinary, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV));
        final byte[] plainTextBytes = message.getBytes(StandardCharsets.UTF_8);
        final byte[] cipherText = cipher.doFinal(plainTextBytes);
        String ret = DatatypeConverter.printBase64Binary(cipherText);
        ret = URLEncoder.encode(ret, "UTF-8");
        return ret;
    }

    @Override
    public String decrypt(String message, boolean transformKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        if (transformKey) {
            keyBinary = getFixedKey(cipher.getBlockSize());
        }
        SecretKey secret = new SecretKeySpec(keyBinary, "AES");
        byte[] bytes = DatatypeConverter.parseBase64Binary(message);
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(IV));
        return new String(cipher.doFinal(bytes), StandardCharsets.UTF_8);
    }

    private byte[] getFixedKey(int blockSize) {
        byte[] keyBytes = new byte[blockSize];
        System.arraycopy(keyBinary, 0, keyBytes, 0, keyBinary.length);
        return keyBytes;
    }
}
