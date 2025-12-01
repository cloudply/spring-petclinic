/*
 * Network utilities with security vulnerabilities
 */
package org.springframework.samples.petclinic.util;

import javax.net.ssl.*;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class NetworkUtil {

	// VULNERABILITY: Disabled SSL certificate validation
	public static void disableSSLVerification() {
		try {
			TrustManager[] trustAllCerts = new TrustManager[]{
				new X509TrustManager() {
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					public void checkClientTrusted(X509Certificate[] certs, String authType) {
						// No validation
					}
					public void checkServerTrusted(X509Certificate[] certs, String authType) {
						// No validation
					}
				}
			};

			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			// Exception ignored
		}
	}

	// VULNERABILITY: SSRF - Server-Side Request Forgery
	public static String fetchURL(String urlString) {
		try {
			// No URL validation - SSRF vulnerability
			URL url = new URL(urlString);
			BufferedReader reader = new BufferedReader(
				new InputStreamReader(url.openStream()));
			StringBuilder content = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line);
			}
			reader.close();
			return content.toString();
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	// VULNERABILITY: Unvalidated redirect/forward
	public static String buildRedirectUrl(String userInput) {
		// Open redirect vulnerability
		return "https://petclinic.com/redirect?url=" + userInput;
	}

	// VULNERABILITY: Using deprecated SSL/TLS protocols
	public static SSLContext createWeakSSLContext() throws Exception {
		// Using deprecated SSL protocol
		return SSLContext.getInstance("SSLv3");
	}

	// VULNERABILITY: Accepting all hostnames
	public static HostnameVerifier getAllowAllHostnameVerifier() {
		return new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true; // Accepts any hostname
			}
		};
	}
}
