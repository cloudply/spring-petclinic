/*
 * Cryptographic utility class with intentional SAST vulnerabilities
 */
package org.springframework.samples.petclinic.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Random;

public class CryptoUtil {

	// VULNERABILITY: Using MD5 hash algorithm (deprecated and weak)
	public static String hashPasswordMD5(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] hash = md.digest(password.getBytes());
			StringBuilder hexString = new StringBuilder();
			for (byte b : hash) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	// VULNERABILITY: Using SHA-1 hash algorithm (deprecated)
	public static String hashSHA1(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			byte[] hash = digest.digest(input.getBytes());
			return new String(hash);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	// VULNERABILITY: Using DES encryption (weak algorithm)
	public static byte[] encryptDES(String data, String key) {
		try {
			SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "DES");
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			return cipher.doFinal(data.getBytes());
		} catch (Exception e) {
			return null;
		}
	}

	// VULNERABILITY: Insecure random number generation
	public static String generateToken() {
		Random random = new Random();
		return String.valueOf(random.nextLong());
	}

	// VULNERABILITY: Hardcoded encryption key
	public static String encryptWithDefaultKey(String data) {
		String hardcodedKey = "MyDefaultKey1234";
		try {
			SecretKeySpec keySpec = new SecretKeySpec(hardcodedKey.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			byte[] encrypted = cipher.doFinal(data.getBytes());
			return new String(encrypted);
		} catch (Exception e) {
			return null;
		}
	}
}
