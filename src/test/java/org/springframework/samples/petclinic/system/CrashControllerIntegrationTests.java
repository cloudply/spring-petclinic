/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.system;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

/**
 * Integration Test for {@link CrashController}.
 *
 * @author Alex Lutz
 */
@SpringBootTest(webEnvironment = RANDOM_PORT,
		properties = { "server.error.include-message=ALWAYS", "management.endpoints.enabled-by-default=false" })
class CrashControllerIntegrationTests {

	@SpringBootApplication
	static class TestConfiguration {

	}

	@Value(value = "${local.server.port}")
	private int port;

	@Autowired(required = false)
	private RestClient.Builder restClientBuilder;

	@Test
	void testTriggerExceptionJson() {
		RestClient restClient = RestClient.create();
		ResponseEntity<Map<String, Object>> resp = restClient.get()
			.uri("http://localhost:" + port + "/oups")
			.retrieve()
			.toEntity(new ParameterizedTypeReference<Map<String, Object>>() {
			});
		assertThat(resp).isNotNull();
		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(resp.getBody()).containsKey("timestamp");
		assertThat(resp.getBody()).containsKey("status");
		assertThat(resp.getBody()).containsKey("error");
		assertThat(resp.getBody()).containsEntry("message",
				"Expected: controller used to showcase what happens when an exception is thrown");
		assertThat(resp.getBody()).containsEntry("path", "/oups");
	}

	@Test
	void testTriggerExceptionHtml() {
		RestClient restClient = RestClient.create();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(List.of(MediaType.TEXT_HTML));
		ResponseEntity<String> resp = restClient.get()
			.uri("http://localhost:" + port + "/oups")
			.headers(h -> h.addAll(headers))
			.retrieve()
			.toEntity(String.class);
		assertThat(resp).isNotNull();
		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(resp.getBody()).isNotNull();
		// html:
		assertThat(resp.getBody()).containsSubsequence("<body>", "<h2>", "Something happened...", "</h2>", "<p>",
				"Expected:", "controller", "used", "to", "showcase", "what", "happens", "when", "an", "exception", "is",
				"thrown", "</p>", "</body>");
		// Not the whitelabel error page:
		assertThat(resp.getBody()).doesNotContain("Whitelabel Error Page",
				"This application has no explicit mapping for");
	}

}
