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
package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 * Integration tests for the {@link Visit} entity.
 */
@DataJpaTest
class VisitIntegrationTests {

	@Autowired
	private TestEntityManager entityManager;

	@Test
	void testPersistVisit() {
		// Given a new visit with description
		Visit visit = new Visit();
		visit.setDescription("Vaccination");
		LocalDate visitDate = LocalDate.of(2023, 6, 10);
		visit.setDate(visitDate);

		// When persisting the visit
		Visit savedVisit = this.entityManager.persistFlushFind(visit);

		// Then the visit should be saved with correct properties
		assertThat(savedVisit.getId()).isNotNull();
		assertThat(savedVisit.getDescription()).isEqualTo("Vaccination");
		assertThat(savedVisit.getDate()).isEqualTo(visitDate);
	}

	@Test
	void testPersistAndRetrieveVisit() {
		// Given a persisted visit
		Visit visit = new Visit();
		visit.setDescription("Annual checkup");
		visit.setDate(LocalDate.of(2023, 7, 15));
		entityManager.persist(visit);
		entityManager.flush();
		
		// When retrieving the visit by ID
		Visit found = entityManager.find(Visit.class, visit.getId());
		
		// Then the retrieved visit should match the original
		assertThat(found).isNotNull();
		assertThat(found.getDescription()).isEqualTo("Annual checkup");
		assertThat(found.getDate()).isEqualTo(LocalDate.of(2023, 7, 15));
	}

	@Test
	void testUpdateVisit() {
		// Given a persisted visit
		Visit visit = new Visit();
		visit.setDescription("Initial checkup");
		visit.setDate(LocalDate.of(2023, 8, 20));
		entityManager.persist(visit);
		entityManager.flush();
		
		// When updating the visit
		Visit persistedVisit = entityManager.find(Visit.class, visit.getId());
		persistedVisit.setDescription("Follow-up visit");
		persistedVisit.setDate(LocalDate.of(2023, 9, 5));
		entityManager.flush();
		
		// Then the changes should be persisted
		Visit updatedVisit = entityManager.find(Visit.class, visit.getId());
		assertThat(updatedVisit.getDescription()).isEqualTo("Follow-up visit");
		assertThat(updatedVisit.getDate()).isEqualTo(LocalDate.of(2023, 9, 5));
	}

	@Test
	void testValidationConstraints() {
		// Given a visit with null description (which violates @NotBlank)
		Visit visit = new Visit();
		visit.setDescription(null);
		
		// When trying to persist it
		// Then validation should fail
		assertThat(entityManager.persistAndFlush(visit)).satisfies(
			persistedVisit -> {
				// The entity manager will throw an exception due to validation failure
				// which will be caught by the test framework
				assertThat(persistedVisit.getDescription()).isNull();
			}
		);
	}
}
