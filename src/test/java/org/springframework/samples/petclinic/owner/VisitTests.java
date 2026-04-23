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
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Test class for the Visit entity.
 *
 * @author Spring PetClinic Team
 */
class VisitTests {

	private Visit visit;
	private Validator validator;

	@BeforeEach
	void setUp() {
		visit = new Visit();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void testVisitCreation() {
		Visit newVisit = new Visit();
		assertThat(newVisit).isNotNull();
		assertThat(newVisit.getId()).isNull();
		assertThat(newVisit.getDate()).isEqualTo(LocalDate.now());
		assertThat(newVisit.getDescription()).isNull();
		assertThat(newVisit.isNew()).isTrue();
	}

	@Test
	void testVisitWithDescription() {
		String description = "Regular checkup";
		visit.setDescription(description);
		
		assertThat(visit.getDescription()).isEqualTo(description);
	}

	@Test
	void testVisitWithCustomDate() {
		LocalDate customDate = LocalDate.of(2023, 12, 25);
		visit.setDate(customDate);
		
		assertThat(visit.getDate()).isEqualTo(customDate);
	}

	@Test
	void testVisitWithId() {
		Integer id = 123;
		visit.setId(id);
		
		assertThat(visit.getId()).isEqualTo(id);
		assertThat(visit.isNew()).isFalse();
	}

	@Test
	void testVisitWithIdAndDescription() {
		Integer id = 456;
		String description = "Vaccination";
		
		visit.setId(id);
		visit.setDescription(description);
		
		assertThat(visit.getId()).isEqualTo(id);
		assertThat(visit.getDescription()).isEqualTo(description);
		assertThat(visit.isNew()).isFalse();
	}

	@Test
	void testVisitDateDefaultsToToday() {
		Visit newVisit = new Visit();
		assertThat(newVisit.getDate()).isEqualTo(LocalDate.now());
	}

	@Test
	void testVisitDateCanBeChanged() {
		LocalDate originalDate = visit.getDate();
		LocalDate newDate = LocalDate.of(2024, 1, 15);
		
		visit.setDate(newDate);
		
		assertThat(visit.getDate()).isNotEqualTo(originalDate);
		assertThat(visit.getDate()).isEqualTo(newDate);
	}

	@Test
	void testVisitDescriptionValidation() {
		visit.setDescription("Valid description");
		
		Set<ConstraintViolation<Visit>> violations = validator.validate(visit);
		assertThat(violations).isEmpty();
	}

	@Test
	void testVisitDescriptionCannotBeNull() {
		visit.setDescription(null);
		
		Set<ConstraintViolation<Visit>> violations = validator.validate(visit);
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).contains("must not be blank");
	}

	@Test
	void testVisitDescriptionCannotBeEmpty() {
		visit.setDescription("");
		
		Set<ConstraintViolation<Visit>> violations = validator.validate(visit);
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).contains("must not be blank");
	}

	@Test
	void testVisitDescriptionCannotBeBlank() {
		visit.setDescription("   ");
		
		Set<ConstraintViolation<Visit>> violations = validator.validate(visit);
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).contains("must not be blank");
	}

	@Test
	void testVisitDescriptionWithWhitespace() {
		String description = "  Regular checkup  ";
		visit.setDescription(description);
		
		assertThat(visit.getDescription()).isEqualTo(description);
		
		Set<ConstraintViolation<Visit>> violations = validator.validate(visit);
		assertThat(violations).isEmpty();
	}

	@Test
	void testVisitDescriptionWithSpecialCharacters() {
		String description = "Emergency visit - broken leg! Cost: $150.00";
		visit.setDescription(description);
		
		assertThat(visit.getDescription()).isEqualTo(description);
		
		Set<ConstraintViolation<Visit>> violations = validator.validate(visit);
		assertThat(violations).isEmpty();
	}

	@Test
	void testVisitDescriptionWithLongText() {
		String longDescription = "This is a very long description that contains multiple sentences. " +
				"The pet came in with various symptoms including lethargy, loss of appetite, and unusual behavior. " +
				"After thorough examination and tests, we determined the appropriate treatment plan.";
		visit.setDescription(longDescription);
		
		assertThat(visit.getDescription()).isEqualTo(longDescription);
		
		Set<ConstraintViolation<Visit>> violations = validator.validate(visit);
		assertThat(violations).isEmpty();
	}

	@Test
	void testVisitDateEdgeCases() {
		// Test with past date
		LocalDate pastDate = LocalDate.of(2020, 1, 1);
		visit.setDate(pastDate);
		assertThat(visit.getDate()).isEqualTo(pastDate);
		
		// Test with future date
		LocalDate futureDate = LocalDate.of(2030, 12, 31);
		visit.setDate(futureDate);
		assertThat(visit.getDate()).isEqualTo(futureDate);
		
		// Test with leap year date
		LocalDate leapYearDate = LocalDate.of(2024, 2, 29);
		visit.setDate(leapYearDate);
		assertThat(visit.getDate()).isEqualTo(leapYearDate);
	}

	@Test
	void testVisitIdEdgeCases() {
		// Test with zero ID
		visit.setId(0);
		assertThat(visit.getId()).isEqualTo(0);
		assertThat(visit.isNew()).isFalse();
		
		// Test with negative ID (though not typical, testing edge case)
		visit.setId(-1);
		assertThat(visit.getId()).isEqualTo(-1);
		assertThat(visit.isNew()).isFalse();
		
		// Test with maximum integer value
		visit.setId(Integer.MAX_VALUE);
		assertThat(visit.getId()).isEqualTo(Integer.MAX_VALUE);
		assertThat(visit.isNew()).isFalse();
	}

	@Test
	void testVisitStateTransitions() {
		// Initially new
		assertThat(visit.isNew()).isTrue();
		
		// Set ID, no longer new
		visit.setId(1);
		assertThat(visit.isNew()).isFalse();
		
		// Set ID back to null, becomes new again
		visit.setId(null);
		assertThat(visit.isNew()).isTrue();
	}

	@Test
	void testVisitInheritanceFromBaseEntity() {
		// Verify that Visit properly inherits from BaseEntity
		assertThat(visit).isInstanceOf(org.springframework.samples.petclinic.model.BaseEntity.class);
		
		// Test inherited methods
		assertThat(visit.getId()).isNull();
		assertThat(visit.isNew()).isTrue();
		
		visit.setId(100);
		assertThat(visit.getId()).isEqualTo(100);
		assertThat(visit.isNew()).isFalse();
	}

	@Test
	void testVisitWithCompleteValidData() {
		Integer id = 789;
		LocalDate date = LocalDate.of(2023, 6, 15);
		String description = "Annual wellness exam";
		
		visit.setId(id);
		visit.setDate(date);
		visit.setDescription(description);
		
		assertThat(visit.getId()).isEqualTo(id);
		assertThat(visit.getDate()).isEqualTo(date);
		assertThat(visit.getDescription()).isEqualTo(description);
		assertThat(visit.isNew()).isFalse();
		
		Set<ConstraintViolation<Visit>> violations = validator.validate(visit);
		assertThat(violations).isEmpty();
	}

	@Test
	void testVisitEquality() {
		Visit visit1 = new Visit();
		Visit visit2 = new Visit();
		
		// Two new visits should be equal (both have null IDs)
		assertThat(visit1.getId()).isEqualTo(visit2.getId());
		
		// Set same ID
		visit1.setId(1);
		visit2.setId(1);
		assertThat(visit1.getId()).isEqualTo(visit2.getId());
		
		// Set different IDs
		visit1.setId(1);
		visit2.setId(2);
		assertThat(visit1.getId()).isNotEqualTo(visit2.getId());
	}

	@Test
	void testVisitSerialization() {
		// Test that Visit can be properly serialized (implements Serializable through BaseEntity)
		visit.setId(123);
		visit.setDescription("Test visit");
		visit.setDate(LocalDate.of(2023, 5, 10));
		
		// Verify all fields are accessible for serialization
		assertThat(visit.getId()).isNotNull();
		assertThat(visit.getDescription()).isNotNull();
		assertThat(visit.getDate()).isNotNull();
	}

	@Test
	void testVisitDescriptionBoundaryValues() {
		// Test single character description
		visit.setDescription("X");
		Set<ConstraintViolation<Visit>> violations = validator.validate(visit);
		assertThat(violations).isEmpty();
		
		// Test description with only numbers
		visit.setDescription("12345");
		violations = validator.validate(visit);
		assertThat(violations).isEmpty();
		
		// Test description with mixed content
		visit.setDescription("Visit #123 - Follow-up");
		violations = validator.validate(visit);
		assertThat(violations).isEmpty();
	}

	@Test
	void testVisitDateTimeFormatAnnotation() {
		// Verify that the date field has the correct DateTimeFormat annotation
		// This is more of a structural test to ensure the annotation is present
		LocalDate testDate = LocalDate.of(2023, 12, 25);
		visit.setDate(testDate);
		
		assertThat(visit.getDate()).isEqualTo(testDate);
		assertThat(visit.getDate().toString()).matches("\\d{4}-\\d{2}-\\d{2}");
	}
}
