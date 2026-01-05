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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 * Test class for the {@link Visit} class.
 */
class VisitTests {

	private Visit visit;

	@BeforeEach
	void setUp() {
		visit = new Visit();
	}

	@Test
	void testCreateNewVisit() {
		// When a new Visit is created
		// Then the date should be set to the current date
		assertThat(visit.getDate()).isEqualTo(LocalDate.now());
		assertThat(visit.isNew()).isTrue();
	}

	@Test
	void testGetAndSetDate() {
		// Given a visit
		// When setting a specific date
		LocalDate testDate = LocalDate.of(2023, 5, 15);
		visit.setDate(testDate);
		
		// Then the date should be correctly retrieved
		assertThat(visit.getDate()).isEqualTo(testDate);
	}

	@Test
	void testGetAndSetDescription() {
		// Given a visit
		// When setting a description
		String testDescription = "Annual checkup";
		visit.setDescription(testDescription);
		
		// Then the description should be correctly retrieved
		assertThat(visit.getDescription()).isEqualTo(testDescription);
	}

	@Test
	void testSetId() {
		// Given a visit
		// When setting an ID
		Integer testId = 1;
		visit.setId(testId);
		
		// Then the visit should no longer be considered new
		assertThat(visit.isNew()).isFalse();
		assertThat(visit.getId()).isEqualTo(testId);
	}
}
