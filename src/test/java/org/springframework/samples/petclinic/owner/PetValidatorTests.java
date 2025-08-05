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
    void shouldValidateWhenAllFieldsAreSet() {
        // Arrange
        pet.setName("Max");
        PetType dogType = new PetType();
        dogType.setName("dog");
        pet.setType(dogType);
        pet.setBirthDate(LocalDate.now().minusYears(1));
        
        // Act
        petValidator.validate(pet, errors);
        
        // Assert
        assertThat(errors.hasErrors()).isFalse();
    }

    @Test
    void shouldNotValidateWhenNameIsEmpty() {
        // Arrange
        pet.setName("");
        PetType dogType = new PetType();
        dogType.setName("dog");
        pet.setType(dogType);
        pet.setBirthDate(LocalDate.now().minusYears(1));
        
        // Act
        petValidator.validate(pet, errors);
        
        // Assert
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getFieldError("name").getCode()).isEqualTo("required");
    }

    @Test
    void shouldNotValidateWhenNameIsNull() {
        // Arrange
        pet.setName(null);
        PetType dogType = new PetType();
        dogType.setName("dog");
        pet.setType(dogType);
        pet.setBirthDate(LocalDate.now().minusYears(1));
        
        // Act
        petValidator.validate(pet, errors);
        
        // Assert
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getFieldError("name").getCode()).isEqualTo("required");
    }

    @Test
    void shouldNotValidateWhenTypeIsNull() {
        // Arrange
        pet.setName("Max");
        pet.setType(null);
        pet.setBirthDate(LocalDate.now().minusYears(1));
        
        // Act
        petValidator.validate(pet, errors);
        
        // Assert
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getFieldError("type").getCode()).isEqualTo("required");
    }

    @Test
    void shouldNotValidateWhenBirthDateIsNull() {
        // Arrange
        pet.setName("Max");
        PetType dogType = new PetType();
        dogType.setName("dog");
        pet.setType(dogType);
        pet.setBirthDate(null);
        
        // Act
        petValidator.validate(pet, errors);
        
        // Assert
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getFieldError("birthDate").getCode()).isEqualTo("required");
    }

    @Test
    void shouldValidateCorrectClass() {
        // Assert
        assertThat(petValidator.supports(Pet.class)).isTrue();
        assertThat(petValidator.supports(Object.class)).isFalse();
    }

    @Test
    void shouldNotValidateWhenMultipleFieldsAreMissing() {
        // Arrange - all fields missing
        
        // Act
        petValidator.validate(pet, errors);
        
        // Assert
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getErrorCount()).isEqualTo(3);
        assertThat(errors.hasFieldErrors("name")).isTrue();
        assertThat(errors.hasFieldErrors("type")).isTrue();
        assertThat(errors.hasFieldErrors("birthDate")).isTrue();
    }

    @Test
    void shouldTrimNameBeforeValidation() {
        // Arrange
        pet.setName("  ");
        PetType dogType = new PetType();
        dogType.setName("dog");
        pet.setType(dogType);
        pet.setBirthDate(LocalDate.now().minusYears(1));
        
        // Act
        petValidator.validate(pet, errors);
        
        // Assert
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getFieldError("name").getCode()).isEqualTo("required");
    }
}
