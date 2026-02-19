package org.springframework.samples.petclinic.system;

import java.io.IOException;
import org.apache.hc.client5.http.classic.CloseableHttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.util.Timeout;

/**
 * Simple wrapper around Apache HttpClient 5.x.
 */
public class LegacyHttpClient {

  private final CloseableHttpClient client;

  public LegacyHttpClient() {
    this.client = HttpClients.custom()
        .setDefaultRequestConfig(RequestConfig.custom()
            .setConnectTimeout(Timeout.ofMilliseconds(1_000))
            .setResponseTimeout(Timeout.ofMilliseconds(1_000))
            .build())
        .build();
  }

  public int ping(String url) throws IOException {
    HttpGet request = new HttpGet(url);
    try (CloseableHttpResponse response = client.execute(request)) {
      return response.getStatusLine().getStatusCode();
    }
  }
}
