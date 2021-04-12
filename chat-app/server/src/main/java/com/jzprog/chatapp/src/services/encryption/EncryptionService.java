package com.jzprog.chatapp.src.services.encryption;

public interface EncryptionService {
    void setKeys(String psk);
    String encrypt(String message, boolean transformKey) throws Exception;
    String decrypt(String message, boolean transformKey) throws Exception;
}
