package org.springframework.samples.petclinic.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.net.ssl.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NetworkUtilTests {

    private URL mockUrl;
    private URLConnection mockConnection;

    @BeforeEach
    void setUp() throws Exception {
        // Setup mock URL and connection for testing
        mockConnection = mock(URLConnection.class);
        
        // Create a custom URL stream handler to return our mock connection
        URLStreamHandler stubURLStreamHandler = new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL u) {
                return mockConnection;
            }
        };
        
        // Create a mock URL with our custom handler
        mockUrl = new URL("https", "example.com", 443, "", stubURLStreamHandler);
    }

    @Test
    void testDisableSSLVerification() throws Exception {
        // Save original socket factory to restore after test
        SSLSocketFactory originalFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
        HostnameVerifier originalVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
        
        try {
            // Call the method
            NetworkUtil.disableSSLVerification();
            
            // Verify that the default SSL socket factory has been changed
            assertNotSame(originalFactory, HttpsURLConnection.getDefaultSSLSocketFactory());
            
            // Test the trust manager by creating a connection and verifying certificates
            // This is a bit complex to test directly, so we'll just verify the factory was changed
        } finally {
            // Restore original settings
            HttpsURLConnection.setDefaultSSLSocketFactory(originalFactory);
            HttpsURLConnection.setDefaultHostnameVerifier(originalVerifier);
        }
    }

    @Test
    void testFetchURL() throws Exception {
        // Prepare test data
        String expectedContent = "Test content";
        InputStream inputStream = new ByteArrayInputStream(expectedContent.getBytes());
        
        // Mock URL behavior using our custom URL
        when(mockConnection.getInputStream()).thenReturn(inputStream);
        
        try (MockedStatic<URL> mockedUrl = Mockito.mockStatic(URL.class)) {
            mockedUrl.when(() -> new URL(anyString())).thenReturn(mockUrl);
            
            // Call the method
            String result = NetworkUtil.fetchURL("https://example.com");
            
            // Verify the result
            assertEquals(expectedContent, result);
        }
    }

    @Test
    void testFetchURL_WithException() throws Exception {
        // Mock URL to throw exception
        when(mockConnection.getInputStream()).thenThrow(new RuntimeException("Connection failed"));
        
        try (MockedStatic<URL> mockedUrl = Mockito.mockStatic(URL.class)) {
            mockedUrl.when(() -> new URL(anyString())).thenReturn(mockUrl);
            
            // Call the method
            String result = NetworkUtil.fetchURL("https://example.com");
            
            // Verify the result contains the error message
            assertTrue(result.contains("Error: Connection failed"));
        }
    }

    @Test
    void testBuildRedirectUrl() {
        // Test with normal input
        String result = NetworkUtil.buildRedirectUrl("https://example.com");
        assertEquals("https://petclinic.com/redirect?url=https://example.com", result);
        
        // Test with potentially malicious input
        String maliciousInput = "javascript:alert(1)";
        String result2 = NetworkUtil.buildRedirectUrl(maliciousInput);
        assertEquals("https://petclinic.com/redirect?url=javascript:alert(1)", result2);
    }

    @Test
    void testCreateWeakSSLContext() throws Exception {
        // Call the method
        SSLContext sslContext = NetworkUtil.createWeakSSLContext();
        
        // Verify it's using the weak protocol
        assertEquals("SSLv3", sslContext.getProtocol());
    }

    @Test
    void testGetAllowAllHostnameVerifier() {
        // Get the hostname verifier
        HostnameVerifier verifier = NetworkUtil.getAllowAllHostnameVerifier();
        
        // Verify it accepts any hostname
        assertTrue(verifier.verify("any-hostname", mock(SSLSession.class)));
        assertTrue(verifier.verify("another-hostname", mock(SSLSession.class)));
    }
}
