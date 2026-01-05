package org.springframework.samples.petclinic.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.net.ssl.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NetworkUtilTests {

    @Test
    void testDisableSSLVerification() throws Exception {
        // Save original socket factory to restore after test
        SSLSocketFactory originalSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
        HostnameVerifier originalHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
        
        try {
            // Execute the method
            NetworkUtil.disableSSLVerification();
            
            // Verify that the default SSL socket factory has been changed
            assertNotSame(originalSocketFactory, HttpsURLConnection.getDefaultSSLSocketFactory());
            
            // Test the trust manager by creating a connection
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[] { 
                (X509TrustManager) HttpsURLConnection.getDefaultSSLSocketFactory() 
            }, new SecureRandom());
            
            // If we got here without exception, the test passes
            assertTrue(true, "SSL verification was successfully disabled");
        } finally {
            // Restore original settings
            HttpsURLConnection.setDefaultSSLSocketFactory(originalSocketFactory);
            HttpsURLConnection.setDefaultHostnameVerifier(originalHostnameVerifier);
        }
    }

    @Test
    void testFetchURL() throws Exception {
        // Setup
        String expectedContent = "Test content";
        String testUrl = "https://example.com";
        
        try (MockedStatic<URL> mockedUrl = Mockito.mockStatic(URL.class)) {
            URL mockUrl = mock(URL.class);
            mockedUrl.when(() -> new URL(testUrl)).thenReturn(mockUrl);
            
            ByteArrayInputStream inputStream = new ByteArrayInputStream(expectedContent.getBytes());
            when(mockUrl.openStream()).thenReturn(inputStream);
            
            // Execute
            String result = NetworkUtil.fetchURL(testUrl);
            
            // Verify
            assertEquals(expectedContent, result);
        }
    }
    
    @Test
    void testFetchURLWithException() {
        // Setup
        String testUrl = "invalid://url";
        
        // Execute
        String result = NetworkUtil.fetchURL(testUrl);
        
        // Verify
        assertTrue(result.startsWith("Error:"));
    }

    @Test
    void testBuildRedirectUrl() {
        // Setup
        String userInput = "malicious.com";
        
        // Execute
        String result = NetworkUtil.buildRedirectUrl(userInput);
        
        // Verify
        assertEquals("https://petclinic.com/redirect?url=malicious.com", result);
    }

    @Test
    void testCreateWeakSSLContext() throws Exception {
        // Execute
        SSLContext context = NetworkUtil.createWeakSSLContext();
        
        // Verify
        assertNotNull(context);
        assertEquals("SSLv3", context.getProtocol());
    }

    @Test
    void testGetAllowAllHostnameVerifier() {
        // Execute
        HostnameVerifier verifier = NetworkUtil.getAllowAllHostnameVerifier();
        
        // Verify
        assertNotNull(verifier);
        assertTrue(verifier.verify("any-hostname", mock(SSLSession.class)));
    }
    
    @Test
    void testGetAllowAllHostnameVerifierWithDifferentHosts() {
        // Setup
        HostnameVerifier verifier = NetworkUtil.getAllowAllHostnameVerifier();
        SSLSession mockSession = mock(SSLSession.class);
        
        // Execute & Verify
        assertTrue(verifier.verify("example.com", mockSession));
        assertTrue(verifier.verify("malicious-site.com", mockSession));
        assertTrue(verifier.verify("localhost", mockSession));
        assertTrue(verifier.verify("127.0.0.1", mockSession));
    }
}
