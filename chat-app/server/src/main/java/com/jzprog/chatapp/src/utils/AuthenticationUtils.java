package com.jzprog.chatapp.src.utils;

import java.security.MessageDigest;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.codec.binary.Hex;

public class AuthenticationUtils {
	
	public static String getHashedPassword(String plainTextPassword) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
	    md.update(plainTextPassword.getBytes());
	    return Hex.encodeHexString(md.digest()).toUpperCase();
	}
}
