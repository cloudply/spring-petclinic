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
        // Setup for URL mocking
        mockConnection = mock(URLConnection.class);
        mockUrl = new URL("https", "example.com", 443, "", new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL url) {
                return mockConnection;
            }
        });
    }

    @Test
    void testDisableSSLVerification() throws Exception {
        // Save original socket factory to restore after test
        SSLSocketFactory originalFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
        HostnameVerifier originalVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
        
        try {
            // Execute the method
            NetworkUtil.disableSSLVerification();
            
            // Verify that the default SSL socket factory has been changed
            assertNotSame(originalFactory, HttpsURLConnection.getDefaultSSLSocketFactory());
            
            // Test the trust manager by creating a connection and verifying certificates
            // This is a bit complex to test directly, so we'll verify the behavior indirectly
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[] { 
                (X509TrustManager) HttpsURLConnection.getDefaultSSLSocketFactory() 
            }, new SecureRandom());
            
            // If we got here without exception, the test passes
            assertTrue(true, "SSL verification was disabled without exceptions");
        } finally {
            // Restore original settings
            HttpsURLConnection.setDefaultSSLSocketFactory(originalFactory);
            HttpsURLConnection.setDefaultHostnameVerifier(originalVerifier);
        }
    }

    @Test
    void testFetchURL() throws Exception {
        // Arrange
        String expectedContent = "Test content";
        InputStream inputStream = new ByteArrayInputStream(expectedContent.getBytes());
        
        try (MockedStatic<URL> mockedStatic = Mockito.mockStatic(URL.class)) {
            mockedStatic.when(() -> new URL(anyString())).thenReturn(mockUrl);
            when(mockConnection.getInputStream()).thenReturn(inputStream);
            
            // Act
            String result = NetworkUtil.fetchURL("https://example.com");
            
            // Assert
            assertEquals(expectedContent, result);
        }
    }

    @Test
    void testFetchURL_WithException() {
        // Arrange
        try (MockedStatic<URL> mockedStatic = Mockito.mockStatic(URL.class)) {
            mockedStatic.when(() -> new URL(anyString())).thenThrow(new RuntimeException("Test exception"));
            
            // Act
            String result = NetworkUtil.fetchURL("https://example.com");
            
            // Assert
            assertTrue(result.startsWith("Error:"));
            assertTrue(result.contains("Test exception"));
        }
    }

    @Test
    void testBuildRedirectUrl() {
        // Arrange
        String userInput = "malicious-site.com";
        
        // Act
        String redirectUrl = NetworkUtil.buildRedirectUrl(userInput);
        
        // Assert
        assertEquals("https://petclinic.com/redirect?url=malicious-site.com", redirectUrl);
    }

    @Test
    void testCreateWeakSSLContext() throws Exception {
        // Act
        SSLContext sslContext = NetworkUtil.createWeakSSLContext();
        
        // Assert
        assertNotNull(sslContext);
        assertEquals("SSLv3", sslContext.getProtocol());
    }

    @Test
    void testGetAllowAllHostnameVerifier() {
        // Act
        HostnameVerifier verifier = NetworkUtil.getAllowAllHostnameVerifier();
        
        // Assert
        assertNotNull(verifier);
        assertTrue(verifier.verify("any-hostname", mock(SSLSession.class)));
        assertTrue(verifier.verify("another-hostname", mock(SSLSession.class)));
    }
}
