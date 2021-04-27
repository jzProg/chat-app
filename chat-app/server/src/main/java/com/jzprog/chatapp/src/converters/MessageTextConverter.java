package com.jzprog.chatapp.src.converters;

import com.jzprog.chatapp.src.services.encryption.AesEncryptionService;
import com.jzprog.chatapp.src.services.encryption.EncryptionService;
import com.jzprog.chatapp.src.utils.EncodingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 attribute converter for handling the encryption/decryption of chat messages during storing/fetching from database
 */
@Converter
@Component
public class MessageTextConverter implements AttributeConverter<String, String> {

    private static String MESSAGES_ENCRYPT_KEY;

    private static EncryptionService encryptionService;

    private static EncodingHelper encodingHelper;

    @Autowired
    public void setEncryptionKey(@Value("${encryption.private.key}") String encryptionPrivateKey) {
        MessageTextConverter.MESSAGES_ENCRYPT_KEY = encryptionPrivateKey;
    }

    @Autowired
    public void initMyRepository(EncodingHelper encodingHelper, AesEncryptionService encryptionService) {
        MessageTextConverter.encodingHelper = encodingHelper;
        MessageTextConverter.encryptionService = encryptionService;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        encryptionService.setKeys(MESSAGES_ENCRYPT_KEY);
        try {
            return encodingHelper.getUrlDecodedMessage(encryptionService.encrypt(attribute, true));
        } catch (Exception e) {
            return attribute;
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        encryptionService.setKeys(MESSAGES_ENCRYPT_KEY);
        try {
            return encryptionService.decrypt(dbData, true);
        } catch (Exception e) {
            return dbData;
        }
    }
}

