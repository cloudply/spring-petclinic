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

/**
 * JUnit test for the {@link Visit} class.
 *
 * @author Ken Krebs
 * @author Dave Syer
 */
class VisitTests {

	private Visit visit;

	@BeforeEach
	void setUp() {
		visit = new Visit();
	}

	@Test
	void testVisitCreation() {
		assertThat(visit).isNotNull();
		assertThat(visit.getId()).isNull();
		assertThat(visit.getDate()).isEqualTo(LocalDate.now());
		assertThat(visit.getDescription()).isNull();
		assertThat(visit.isNew()).isTrue();
	}

	@Test
	void testVisitWithId() {
		visit.setId(1);
		assertThat(visit.getId()).isEqualTo(1);
		assertThat(visit.isNew()).isFalse();
	}

	@Test
	void testSetAndGetDate() {
		LocalDate testDate = LocalDate.of(2023, 12, 25);
		visit.setDate(testDate);
		assertThat(visit.getDate()).isEqualTo(testDate);
	}

	@Test
	void testSetAndGetDateWithNull() {
		visit.setDate(null);
		assertThat(visit.getDate()).isNull();
	}

	@Test
	void testSetAndGetDescription() {
		String description = "Regular checkup";
		visit.setDescription(description);
		assertThat(visit.getDescription()).isEqualTo(description);
	}

	@Test
	void testSetAndGetDescriptionWithNull() {
		visit.setDescription(null);
		assertThat(visit.getDescription()).isNull();
	}

	@Test
	void testVisitWithAllProperties() {
		Integer id = 1;
		LocalDate date = LocalDate.of(2023, 6, 15);
		String description = "Annual vaccination";

		visit.setId(id);
		visit.setDate(date);
		visit.setDescription(description);

		assertThat(visit.getId()).isEqualTo(id);
		assertThat(visit.getDate()).isEqualTo(date);
		assertThat(visit.getDescription()).isEqualTo(description);
		assertThat(visit.isNew()).isFalse();
	}

	@Test
	void testVisitInheritedMethods() {
		// Test inherited methods from BaseEntity
		assertThat(visit.isNew()).isTrue();
		
		visit.setId(42);
		assertThat(visit.isNew()).isFalse();
		assertThat(visit.getId()).isEqualTo(42);
	}

	@Test
	void testVisitWithEmptyDescription() {
		visit.setDescription("");
		assertThat(visit.getDescription()).isEmpty();
	}

	@Test
	void testVisitWithWhitespaceDescription() {
		String whitespaceDescription = "   ";
		visit.setDescription(whitespaceDescription);
		assertThat(visit.getDescription()).isEqualTo(whitespaceDescription);
	}

	@Test
	void testVisitWithLongDescription() {
		String longDescription = "This is a very long description that contains many details about the visit including symptoms, treatments, medications prescribed, and follow-up instructions for the pet owner to ensure proper care and recovery.";
		visit.setDescription(longDescription);
		assertThat(visit.getDescription()).isEqualTo(longDescription);
	}

	@Test
	void testVisitWithSpecialCharactersInDescription() {
		String specialDescription = "Pet had @#$%^&*() symptoms! Treatment: $100. Follow-up: 2 weeks.";
		visit.setDescription(specialDescription);
		assertThat(visit.getDescription()).isEqualTo(specialDescription);
	}

	@Test
	void testVisitWithFutureDate() {
		LocalDate futureDate = LocalDate.now().plusDays(30);
		visit.setDate(futureDate);
		assertThat(visit.getDate()).isEqualTo(futureDate);
	}

	@Test
	void testVisitWithPastDate() {
		LocalDate pastDate = LocalDate.now().minusDays(30);
		visit.setDate(pastDate);
		assertThat(visit.getDate()).isEqualTo(pastDate);
	}

	@Test
	void testVisitWithCurrentDate() {
		LocalDate currentDate = LocalDate.now();
		visit.setDate(currentDate);
		assertThat(visit.getDate()).isEqualTo(currentDate);
	}

	@Test
	void testVisitIdBoundaryValues() {
		// Test with minimum integer value
		visit.setId(Integer.MIN_VALUE);
		assertThat(visit.getId()).isEqualTo(Integer.MIN_VALUE);
		assertThat(visit.isNew()).isFalse();

		// Test with maximum integer value
		visit.setId(Integer.MAX_VALUE);
		assertThat(visit.getId()).isEqualTo(Integer.MAX_VALUE);
		assertThat(visit.isNew()).isFalse();

		// Test with zero
		visit.setId(0);
		assertThat(visit.getId()).isEqualTo(0);
		assertThat(visit.isNew()).isFalse();
	}

	@Test
	void testVisitResetId() {
		visit.setId(100);
		assertThat(visit.isNew()).isFalse();
		
		visit.setId(null);
		assertThat(visit.isNew()).isTrue();
		assertThat(visit.getId()).isNull();
	}

	@Test
	void testVisitDateEdgeCases() {
		// Test with leap year date
		LocalDate leapYearDate = LocalDate.of(2024, 2, 29);
		visit.setDate(leapYearDate);
		assertThat(visit.getDate()).isEqualTo(leapYearDate);

		// Test with year boundary
		LocalDate yearBoundary = LocalDate.of(2023, 12, 31);
		visit.setDate(yearBoundary);
		assertThat(visit.getDate()).isEqualTo(yearBoundary);

		// Test with month boundary
		LocalDate monthBoundary = LocalDate.of(2023, 1, 31);
		visit.setDate(monthBoundary);
		assertThat(visit.getDate()).isEqualTo(monthBoundary);
	}

	@Test
	void testVisitEquality() {
		Visit visit1 = new Visit();
		Visit visit2 = new Visit();
		
		// Two new visits should not be equal (different objects)
		assertThat(visit1).isNotEqualTo(visit2);
		
		// Same object should be equal to itself
		assertThat(visit1).isEqualTo(visit1);
		
		// Test with same ID
		visit1.setId(1);
		visit2.setId(1);
		// Note: BaseEntity doesn't override equals, so they're still different objects
		assertThat(visit1).isNotEqualTo(visit2);
	}

	@Test
	void testVisitDefaultConstructorSetsCurrentDate() {
		LocalDate beforeCreation = LocalDate.now();
		Visit newVisit = new Visit();
		LocalDate afterCreation = LocalDate.now();
		
		assertThat(newVisit.getDate()).isNotNull();
		assertThat(newVisit.getDate()).isBetween(beforeCreation, afterCreation);
	}

	@Test
	void testVisitDescriptionModification() {
		String originalDescription = "Initial checkup";
		visit.setDescription(originalDescription);
		assertThat(visit.getDescription()).isEqualTo(originalDescription);
		
		String modifiedDescription = "Follow-up examination";
		visit.setDescription(modifiedDescription);
		assertThat(visit.getDescription()).isEqualTo(modifiedDescription);
		assertThat(visit.getDescription()).isNotEqualTo(originalDescription);
	}

	@Test
	void testVisitDateModification() {
		LocalDate originalDate = LocalDate.of(2023, 1, 1);
		visit.setDate(originalDate);
		assertThat(visit.getDate()).isEqualTo(originalDate);
		
		LocalDate modifiedDate = LocalDate.of(2023, 12, 31);
		visit.setDate(modifiedDate);
		assertThat(visit.getDate()).isEqualTo(modifiedDate);
		assertThat(visit.getDate()).isNotEqualTo(originalDate);
	}

	@Test
	void testVisitWithMinimalValidData() {
		visit.setDescription("Checkup");
		assertThat(visit.getDescription()).isEqualTo("Checkup");
		assertThat(visit.getDate()).isNotNull(); // Should have default current date
		assertThat(visit.getId()).isNull(); // Should be new
	}

	@Test
	void testVisitWithCompleteValidData() {
		Integer id = 123;
		LocalDate date = LocalDate.of(2023, 8, 15);
		String description = "Comprehensive health examination with blood work and vaccinations";
		
		visit.setId(id);
		visit.setDate(date);
		visit.setDescription(description);
		
		assertThat(visit.getId()).isEqualTo(id);
		assertThat(visit.getDate()).isEqualTo(date);
		assertThat(visit.getDescription()).isEqualTo(description);
		assertThat(visit.isNew()).isFalse();
	}
}
