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
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link PetValidator}.
 *
 * @author Test Author
 */
class PetValidatorTests {

	private PetValidator petValidator;
	private Pet pet;
	private Errors errors;

	@BeforeEach
	void setUp() {
		petValidator = new PetValidator();
		pet = new Pet();
		errors = new BeanPropertyBindingResult(pet, "pet");
	}

	@Test
	void testSupportsReturnsTrueForPetClass() {
		assertThat(petValidator.supports(Pet.class)).isTrue();
	}

	@Test
	void testSupportsReturnsFalseForNonPetClass() {
		assertThat(petValidator.supports(String.class)).isFalse();
		assertThat(petValidator.supports(Object.class)).isFalse();
		assertThat(petValidator.supports(Owner.class)).isFalse();
	}

	@Test
	void testSupportsReturnsTrueForPetSubclass() {
		// Create a subclass of Pet for testing
		class TestPet extends Pet {
		}
		assertThat(petValidator.supports(TestPet.class)).isTrue();
	}

	@Test
	void testValidateWithValidPet() {
		// Setup a valid pet
		pet.setName("Fluffy");
		pet.setType(createPetType("Cat"));
		pet.setBirthDate(LocalDate.now().minusYears(1));
		pet.setId(1); // Make it not new

		petValidator.validate(pet, errors);

		assertThat(errors.hasErrors()).isFalse();
		assertThat(errors.getErrorCount()).isEqualTo(0);
	}

	@Test
	void testValidateWithEmptyName() {
		pet.setName("");
		pet.setType(createPetType("Dog"));
		pet.setBirthDate(LocalDate.now().minusYears(1));

		petValidator.validate(pet, errors);

		assertThat(errors.hasErrors()).isTrue();
		assertThat(errors.hasFieldErrors("name")).isTrue();
		assertThat(errors.getFieldError("name").getCode()).isEqualTo("required");
	}

	@Test
	void testValidateWithNullName() {
		pet.setName(null);
		pet.setType(createPetType("Dog"));
		pet.setBirthDate(LocalDate.now().minusYears(1));

		petValidator.validate(pet, errors);

		assertThat(errors.hasErrors()).isTrue();
		assertThat(errors.hasFieldErrors("name")).isTrue();
		assertThat(errors.getFieldError("name").getCode()).isEqualTo("required");
	}

	@Test
	void testValidateWithWhitespaceOnlyName() {
		pet.setName("   ");
		pet.setType(createPetType("Dog"));
		pet.setBirthDate(LocalDate.now().minusYears(1));

		petValidator.validate(pet, errors);

		assertThat(errors.hasErrors()).isTrue();
		assertThat(errors.hasFieldErrors("name")).isTrue();
		assertThat(errors.getFieldError("name").getCode()).isEqualTo("required");
	}

	@Test
	void testValidateWithNullTypeForNewPet() {
		pet.setName("Buddy");
		pet.setType(null);
		pet.setBirthDate(LocalDate.now().minusYears(1));
		// Pet is new by default (id is null)

		petValidator.validate(pet, errors);

		assertThat(errors.hasErrors()).isTrue();
		assertThat(errors.hasFieldErrors("type")).isTrue();
		assertThat(errors.getFieldError("type").getCode()).isEqualTo("required");
	}

	@Test
	void testValidateWithNullTypeForExistingPet() {
		pet.setName("Buddy");
		pet.setType(null);
		pet.setBirthDate(LocalDate.now().minusYears(1));
		pet.setId(1); // Make it not new

		petValidator.validate(pet, errors);

		// Should not have type error for existing pets
		assertThat(errors.hasFieldErrors("type")).isFalse();
		// But should still have other validation if any
	}

	@Test
	void testValidateWithNullBirthDate() {
		pet.setName("Charlie");
		pet.setType(createPetType("Bird"));
		pet.setBirthDate(null);

		petValidator.validate(pet, errors);

		assertThat(errors.hasErrors()).isTrue();
		assertThat(errors.hasFieldErrors("birthDate")).isTrue();
		assertThat(errors.getFieldError("birthDate").getCode()).isEqualTo("required");
	}

	@Test
	void testValidateWithAllFieldsEmpty() {
		pet.setName(null);
		pet.setType(null);
		pet.setBirthDate(null);

		petValidator.validate(pet, errors);

		assertThat(errors.hasErrors()).isTrue();
		assertThat(errors.getErrorCount()).isEqualTo(3);
		assertThat(errors.hasFieldErrors("name")).isTrue();
		assertThat(errors.hasFieldErrors("type")).isTrue();
		assertThat(errors.hasFieldErrors("birthDate")).isTrue();
	}

	@Test
	void testValidateWithValidNameAndBirthDateButNullTypeForNewPet() {
		pet.setName("Max");
		pet.setType(null);
		pet.setBirthDate(LocalDate.now().minusMonths(6));

		petValidator.validate(pet, errors);

		assertThat(errors.hasErrors()).isTrue();
		assertThat(errors.getErrorCount()).isEqualTo(1);
		assertThat(errors.hasFieldErrors("name")).isFalse();
		assertThat(errors.hasFieldErrors("type")).isTrue();
		assertThat(errors.hasFieldErrors("birthDate")).isFalse();
	}

	@Test
	void testValidateWithValidNameAndTypeButNullBirthDate() {
		pet.setName("Luna");
		pet.setType(createPetType("Cat"));
		pet.setBirthDate(null);

		petValidator.validate(pet, errors);

		assertThat(errors.hasErrors()).isTrue();
		assertThat(errors.getErrorCount()).isEqualTo(1);
		assertThat(errors.hasFieldErrors("name")).isFalse();
		assertThat(errors.hasFieldErrors("type")).isFalse();
		assertThat(errors.hasFieldErrors("birthDate")).isTrue();
	}

	@Test
	void testValidateWithValidTypeAndBirthDateButEmptyName() {
		pet.setName("");
		pet.setType(createPetType("Dog"));
		pet.setBirthDate(LocalDate.now().minusYears(2));

		petValidator.validate(pet, errors);

		assertThat(errors.hasErrors()).isTrue();
		assertThat(errors.getErrorCount()).isEqualTo(1);
		assertThat(errors.hasFieldErrors("name")).isTrue();
		assertThat(errors.hasFieldErrors("type")).isFalse();
		assertThat(errors.hasFieldErrors("birthDate")).isFalse();
	}

	@Test
	void testValidateWithFutureBirthDate() {
		// Note: The validator doesn't check for future dates, but this tests current behavior
		pet.setName("Future Pet");
		pet.setType(createPetType("Dog"));
		pet.setBirthDate(LocalDate.now().plusDays(1));

		petValidator.validate(pet, errors);

		// Current implementation doesn't validate future dates
		assertThat(errors.hasFieldErrors("birthDate")).isFalse();
	}

	@Test
	void testValidateWithVeryOldBirthDate() {
		pet.setName("Ancient Pet");
		pet.setType(createPetType("Turtle"));
		pet.setBirthDate(LocalDate.of(1900, 1, 1));

		petValidator.validate(pet, errors);

		// Current implementation doesn't validate age limits
		assertThat(errors.hasFieldErrors("birthDate")).isFalse();
	}

	@Test
	void testValidateWithSpecialCharactersInName() {
		pet.setName("Mr. Whiskers-Jr!");
		pet.setType(createPetType("Cat"));
		pet.setBirthDate(LocalDate.now().minusYears(1));

		petValidator.validate(pet, errors);

		assertThat(errors.hasFieldErrors("name")).isFalse();
	}

	@Test
	void testValidateWithNumericName() {
		pet.setName("123");
		pet.setType(createPetType("Robot"));
		pet.setBirthDate(LocalDate.now().minusYears(1));

		petValidator.validate(pet, errors);

		assertThat(errors.hasFieldErrors("name")).isFalse();
	}

	@Test
	void testValidateWithVeryLongName() {
		pet.setName("A".repeat(1000));
		pet.setType(createPetType("Dog"));
		pet.setBirthDate(LocalDate.now().minusYears(1));

		petValidator.validate(pet, errors);

		// Current implementation doesn't validate name length
		assertThat(errors.hasFieldErrors("name")).isFalse();
	}

	@Test
	void testValidateWithMinimalValidName() {
		pet.setName("A");
		pet.setType(createPetType("Dog"));
		pet.setBirthDate(LocalDate.now().minusYears(1));

		petValidator.validate(pet, errors);

		assertThat(errors.hasFieldErrors("name")).isFalse();
	}

	@Test
	void testValidateMultipleTimesWithSameValidator() {
		// First validation
		pet.setName("Pet1");
		pet.setType(createPetType("Dog"));
		pet.setBirthDate(LocalDate.now().minusYears(1));
		petValidator.validate(pet, errors);
		assertThat(errors.hasErrors()).isFalse();

		// Second validation with different pet
		Pet pet2 = new Pet();
		Errors errors2 = new BeanPropertyBindingResult(pet2, "pet2");
		pet2.setName("");
		petValidator.validate(pet2, errors2);
		assertThat(errors2.hasErrors()).isTrue();
	}

	@Test
	void testValidateWithTabsAndNewlinesInName() {
		pet.setName("\t\n");
		pet.setType(createPetType("Dog"));
		pet.setBirthDate(LocalDate.now().minusYears(1));

		petValidator.validate(pet, errors);

		assertThat(errors.hasFieldErrors("name")).isTrue();
		assertThat(errors.getFieldError("name").getCode()).isEqualTo("required");
	}

	@Test
	void testValidateWithMixedWhitespaceInName() {
		pet.setName(" \t \n ");
		pet.setType(createPetType("Dog"));
		pet.setBirthDate(LocalDate.now().minusYears(1));

		petValidator.validate(pet, errors);

		assertThat(errors.hasFieldErrors("name")).isTrue();
		assertThat(errors.getFieldError("name").getCode()).isEqualTo("required");
	}

	/**
	 * Helper method to create a PetType for testing
	 */
	private PetType createPetType(String name) {
		PetType petType = new PetType();
		petType.setName(name);
		petType.setId(1);
		return petType;
	}
}
