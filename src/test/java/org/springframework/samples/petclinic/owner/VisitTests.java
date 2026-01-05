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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.SerializationUtils;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

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
	void testCreationDefaultDate() {
		// When a visit is created
		// Then the date should be set to the current date
		assertThat(visit.getDate()).isEqualTo(LocalDate.now());
	}

	@Test
	void testSetAndGetDate() {
		// Given a visit
		// When setting a specific date
		LocalDate testDate = LocalDate.of(2023, 5, 15);
		visit.setDate(testDate);
		
		// Then the date should be retrievable
		assertThat(visit.getDate()).isEqualTo(testDate);
	}

	@Test
	void testSetAndGetDescription() {
		// Given a visit
		// When setting a description
		String testDescription = "Annual checkup";
		visit.setDescription(testDescription);
		
		// Then the description should be retrievable
		assertThat(visit.getDescription()).isEqualTo(testDescription);
	}

	@Test
	void testSerialization() {
		// Given a visit with data
		visit.setId(123);
		visit.setDescription("Vaccination");
		LocalDate testDate = LocalDate.of(2023, 6, 20);
		visit.setDate(testDate);
		
		// When serializing and deserializing
		@SuppressWarnings("deprecation")
		Visit other = (Visit) SerializationUtils.deserialize(SerializationUtils.serialize(visit));
		
		// Then the deserialized object should have the same properties
		assertThat(other.getId()).isEqualTo(visit.getId());
		assertThat(other.getDescription()).isEqualTo(visit.getDescription());
		assertThat(other.getDate()).isEqualTo(visit.getDate());
	}

	@Test
	void testNewVisitIsNew() {
		// When a new visit is created
		// Then it should be considered "new" (id is null)
		assertThat(visit.isNew()).isTrue();
	}

	@Test
	void testVisitWithIdIsNotNew() {
		// Given a visit with an ID
		visit.setId(1);
		
		// Then it should not be considered "new"
		assertThat(visit.isNew()).isFalse();
	}
}
