package org.springframework.samples.petclinic.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CryptoUtil}.
 */
class CryptoUtilTests {

    @Test
    void testHashPasswordMD5() {
        // Given
        String password = "password123";
        
        // When
        String hash = CryptoUtil.hashPasswordMD5(password);
        
        // Then
        assertNotNull(hash);
        assertEquals(32, hash.length()); // MD5 produces 32 character hex string
        
        // Verify idempotence - same input should produce same hash
        String secondHash = CryptoUtil.hashPasswordMD5(password);
        assertEquals(hash, secondHash);
        
        // Different inputs should produce different hashes
        String differentHash = CryptoUtil.hashPasswordMD5("different");
        assertNotEquals(hash, differentHash);
    }
    
    @Test
    void testHashSHA1() {
        // Given
        String input = "test-input";
        
        // When
        String hash = CryptoUtil.hashSHA1(input);
        
        // Then
        assertNotNull(hash);
        
        // Verify idempotence
        String secondHash = CryptoUtil.hashSHA1(input);
        assertEquals(hash, secondHash);
        
        // Different inputs should produce different hashes
        String differentHash = CryptoUtil.hashSHA1("different-input");
        assertNotEquals(hash, differentHash);
    }
    
    @Test
    void testEncryptDES() {
        // Given
        String data = "sensitive-data";
        String key = "12345678"; // DES requires 8-byte key
        
        // When
        byte[] encrypted = CryptoUtil.encryptDES(data, key);
        
        // Then
        assertNotNull(encrypted);
        assertTrue(encrypted.length > 0);
        
        // Verify the encrypted data is different from original
        assertNotEquals(data, new String(encrypted));
        
        // Verify encryption is deterministic with same key
        byte[] secondEncryption = CryptoUtil.encryptDES(data, key);
        assertArrayEquals(encrypted, secondEncryption);
    }
    
    @Test
    void testEncryptDESWithInvalidKey() {
        // Given - DES requires exactly 8 bytes key
        String data = "test-data";
        String invalidKey = "123"; // Too short
        
        // When/Then
        assertNull(CryptoUtil.encryptDES(data, invalidKey));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"", "test", "longerString123"})
    void testGenerateToken(String unused) {
        // When
        String token1 = CryptoUtil.generateToken();
        String token2 = CryptoUtil.generateToken();
        
        // Then
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2); // Tokens should be different
    }
    
    @Test
    void testEncryptWithDefaultKey() {
        // Given
        String data = "data-to-encrypt";
        
        // When
        String encrypted = CryptoUtil.encryptWithDefaultKey(data);
        
        // Then
        assertNotNull(encrypted);
        assertNotEquals(data, encrypted);
        
        // Verify encryption is deterministic with hardcoded key
        String secondEncryption = CryptoUtil.encryptWithDefaultKey(data);
        assertEquals(encrypted, secondEncryption);
        
        // Different data should produce different encryption
        String differentEncryption = CryptoUtil.encryptWithDefaultKey("different-data");
        assertNotEquals(encrypted, differentEncryption);
    }
    
    @Test
    void testEncryptionRoundTrip() throws Exception {
        // This test verifies that we can decrypt what we encrypt
        // using the same algorithm and key as in the CryptoUtil class
        
        // Given
        String data = "test-data-for-roundtrip";
        String key = "MyDefaultKey1234";
        
        // When - encrypt using the utility method
        String encryptedData = CryptoUtil.encryptWithDefaultKey(data);
        
        // Then - decrypt manually and verify
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        
        byte[] decrypted = cipher.doFinal(encryptedData.getBytes());
        String decryptedString = new String(decrypted);
        
        assertEquals(data, decryptedString);
    }
}
