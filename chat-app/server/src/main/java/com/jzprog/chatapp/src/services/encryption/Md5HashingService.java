package com.jzprog.chatapp.src.services.encryption;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;

@Service("md5")
public class Md5HashingService implements EncryptionService {
    @Override
    public void setKeys(String psk) {
       // no keys for this hashing
    }

    @Override
    public String encrypt(String message, boolean transformKey) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(message.getBytes());
        return Hex.encodeHexString(md.digest()).toUpperCase();
    }

    @Override
    public String decrypt(String message, boolean transformKey) throws Exception {
        return null; // one way hash - no decryption
    }
}
