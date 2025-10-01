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
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

/**
 * Test class for {@link PetValidator}
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
	void shouldValidateWhenAllFieldsCorrect() {
		// Arrange
		pet.setName("Max");
		pet.setBirthDate(LocalDate.now().minusYears(1));
		PetType dog = new PetType();
		dog.setName("dog");
		pet.setType(dog);
		
		// Act
		petValidator.validate(pet, errors);
		
		// Assert
		assertThat(errors.hasErrors()).isFalse();
	}
	
	@Test
	void shouldNotValidateWhenNameEmpty() {
		// Arrange
		pet.setName("");  // Empty name
		pet.setBirthDate(LocalDate.now().minusYears(1));
		PetType dog = new PetType();
		dog.setName("dog");
		pet.setType(dog);
		
		// Act
		petValidator.validate(pet, errors);
		
		// Assert
		assertThat(errors.hasErrors()).isTrue();
		assertThat(errors.getFieldError("name").getCode()).isEqualTo("required");
	}
	
	@Test
	void shouldNotValidateWhenNameNull() {
		// Arrange
		pet.setName(null);  // Null name
		pet.setBirthDate(LocalDate.now().minusYears(1));
		PetType dog = new PetType();
		dog.setName("dog");
		pet.setType(dog);
		
		// Act
		petValidator.validate(pet, errors);
		
		// Assert
		assertThat(errors.hasErrors()).isTrue();
		assertThat(errors.getFieldError("name").getCode()).isEqualTo("required");
	}
	
	@Test
	void shouldNotValidateWhenTypeNullForNewPet() {
		// Arrange
		pet.setName("Max");
		pet.setBirthDate(LocalDate.now().minusYears(1));
		pet.setType(null);  // Null type
		// Ensure it's a new pet
		pet.setId(null);
		
		// Act
		petValidator.validate(pet, errors);
		
		// Assert
		assertThat(errors.hasErrors()).isTrue();
		assertThat(errors.getFieldError("type").getCode()).isEqualTo("required");
	}
	
	@Test
	void shouldValidateWhenTypeNullForExistingPet() {
		// Arrange
		pet.setName("Max");
		pet.setBirthDate(LocalDate.now().minusYears(1));
		pet.setType(null);  // Null type
		// Make it an existing pet
		pet.setId(1);
		
		// Act
		petValidator.validate(pet, errors);
		
		// Assert
		assertThat(errors.hasErrors()).isFalse();
	}
	
	@Test
	void shouldNotValidateWhenBirthDateNull() {
		// Arrange
		pet.setName("Max");
		pet.setBirthDate(null);  // Null birthDate
		PetType dog = new PetType();
		dog.setName("dog");
		pet.setType(dog);
		
		// Act
		petValidator.validate(pet, errors);
		
		// Assert
		assertThat(errors.hasErrors()).isTrue();
		assertThat(errors.getFieldError("birthDate").getCode()).isEqualTo("required");
	}
	
	@Test
	void shouldSupportPetClass() {
		// Act & Assert
		assertThat(petValidator.supports(Pet.class)).isTrue();
	}
	
	@Test
	void shouldNotSupportOtherClasses() {
		// Act & Assert
		assertThat(petValidator.supports(Object.class)).isFalse();
		assertThat(petValidator.supports(PetType.class)).isFalse();
	}
}
