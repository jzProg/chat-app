package com.jzprog.chatapp.src.utils;

import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
public class EncodingHelper {

    public String getUrlDecodedMessage(String message) {
        String urlDecodedMessage = message;
        if (message.contains("%")) {
            try {
                urlDecodedMessage = java.net.URLDecoder.decode(message, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                return message;
            }
        }
        return urlDecodedMessage;
    }

    public String getUrlEncodedMessage(String message) {
        String urlEncodedMessage = message;
        if (!message.contains("%")) {
            try {
                urlEncodedMessage = java.net.URLEncoder.encode(message, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                return message;
            }
        }
        return urlEncodedMessage;
    }
}
