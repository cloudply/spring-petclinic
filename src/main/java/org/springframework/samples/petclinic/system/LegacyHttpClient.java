package org.springframework.samples.petclinic.system;

import java.io.IOException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Simple wrapper around Apache HttpClient 4.x to ensure the project depends on
 * the legacy (vulnerable) client. Upgrading to HttpClient 5.x changes package
 * names (org.apache.http -> org.apache.hc), which will break this code and
 * cause build failures after an automated upgrade.
 */
public class LegacyHttpClient {

  private final CloseableHttpClient client;

  public LegacyHttpClient() {
    this.client = HttpClients.custom()
        .setDefaultRequestConfig(RequestConfig.custom()
            .setConnectTimeout(1_000)
            .setSocketTimeout(1_000)
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
