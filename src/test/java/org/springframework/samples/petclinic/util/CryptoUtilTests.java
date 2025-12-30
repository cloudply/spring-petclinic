package org.springframework.samples.petclinic.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CryptoUtilTests {

    @Test
    void testHashPasswordMD5() {
        // Given
        String password = "password123";
        
        // When
        String hash = CryptoUtil.hashPasswordMD5(password);
        
        // Then
        assertNotNull(hash);
        assertEquals(32, hash.length());
        // MD5 of "password123" is "482c811da5d5b4bc6d497ffa98491e38"
        assertEquals("482c811da5d5b4bc6d497ffa98491e38", hash);
    }
    
    @Test
    void testHashPasswordMD5_EmptyString() {
        // Given
        String password = "";
        
        // When
        String hash = CryptoUtil.hashPasswordMD5(password);
        
        // Then
        assertNotNull(hash);
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", hash);
    }
    
    @Test
    void testHashSHA1() throws Exception {
        // Given
        String input = "test123";
        
        // When
        String hash = CryptoUtil.hashSHA1(input);
        
        // Then
        assertNotNull(hash);
        
        // Verify the hash is correct by comparing with a known SHA-1 hash
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] expectedHash = digest.digest(input.getBytes());
        assertEquals(new String(expectedHash), hash);
    }
    
    @Test
    void testEncryptDES() throws Exception {
        // Given
        String data = "sensitive data";
        String key = "12345678"; // DES requires 8-byte key
        
        // When
        byte[] encrypted = CryptoUtil.encryptDES(data, key);
        
        // Then
        assertNotNull(encrypted);
        
        // Verify decryption works
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        
        assertEquals(data, new String(decrypted));
    }
    
    @Test
    void testEncryptDES_InvalidKeySize() {
        // Given
        String data = "sensitive data";
        String key = "123"; // Invalid key size for DES
        
        // When
        byte[] encrypted = CryptoUtil.encryptDES(data, key);
        
        // Then
        assertNull(encrypted); // Method should return null on exception
    }
    
    @Test
    void testGenerateToken() {
        // Given
        
        // When
        String token1 = CryptoUtil.generateToken();
        String token2 = CryptoUtil.generateToken();
        
        // Then
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2); // Tokens should be different
    }
    
    @Test
    void testEncryptWithDefaultKey() throws Exception {
        // Given
        String data = "test data";
        String hardcodedKey = "MyDefaultKey1234";
        
        // When
        String encrypted = CryptoUtil.encryptWithDefaultKey(data);
        
        // Then
        assertNotNull(encrypted);
        
        // Verify encryption was done with the hardcoded key
        SecretKeySpec keySpec = new SecretKeySpec(hardcodedKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        
        try {
            // This will throw an exception if the encrypted data is not valid
            // or if it wasn't encrypted with the expected key
            byte[] decrypted = cipher.doFinal(encrypted.getBytes());
            assertEquals(data, new String(decrypted, StandardCharsets.UTF_8));
        } catch (Exception e) {
            // The test should fail if decryption fails
            fail("Decryption failed: " + e.getMessage());
        }
    }
    
    @Test
    void testEncryptWithDefaultKey_EmptyString() {
        // Given
        String data = "";
        
        // When
        String encrypted = CryptoUtil.encryptWithDefaultKey(data);
        
        // Then
        assertNotNull(encrypted);
    }
}
