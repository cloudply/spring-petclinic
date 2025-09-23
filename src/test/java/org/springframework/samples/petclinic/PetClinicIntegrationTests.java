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

package org.springframework.samples.petclinic;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PetClinicIntegrationTests {

	@LocalServerPort
	int port;

	@Autowired
	private VetRepository vets;

	@Autowired
	private RestTemplateBuilder builder;

	@Test
	void testResponsiveMetaTag() throws Exception {
	    RestTemplate template = builder.rootUri("http://localhost:" + port).build();
	    ResponseEntity<String> response = template.getForEntity("/", String.class);
	    assertThat(response.getBody())
            .contains("viewport")
            .contains("<title>PetClinic :: a Spring Framework demonstration</title>")
            .contains("Welcome")
            .contains("Spring Framework");
	}

	@Test
	void testFindAll() throws Exception {
		assertThat(vets.findAll()).hasSizeGreaterThan(0);
		assertThat(vets.findAll()).hasSizeGreaterThan(0); // Verify cache works
	}

	@Test
	void testOwnerDetails() {
		RestTemplate template = builder.rootUri("http://localhost:" + port).build();
		ResponseEntity<String> result = template.exchange(RequestEntity.get("/owners/1").build(), String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody())
            .contains("George Franklin")
            .contains("110 W. Liberty St.")
            .contains("Madison");
	}

	@Test
	void testInvalidOwnerId() {
		RestTemplate template = builder.rootUri("http://localhost:" + port).build();
		ResponseEntity<String> result = template.exchange(RequestEntity.get("/owners/999").build(), String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void testErrorPage() {
		RestTemplate template = builder.rootUri("http://localhost:" + port).build();
		ResponseEntity<String> result = template.exchange(RequestEntity.get("/oups").build(), String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(result.getBody()).contains("Something happened...");
	}

	@Test
	void testMainMethod() {
		assertThatNoException().isThrownBy(() -> PetClinicIntegrationTests.main(new String[]{}));
	}

	public static void main(String[] args) {
		SpringApplication.run(PetClinicApplication.class, args);
	}

}
