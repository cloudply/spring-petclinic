package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PetValidatorTest {

    @Test
    void testSupports() {
        PetValidator validator = new PetValidator();
        assertThat(validator.supports(Pet.class)).isTrue();
        assertThat(validator.supports(String.class)).isFalse();
    }

    @Test
    void testValidateAllFieldsValid() {
        Pet pet = new Pet();
        pet.setName("Fluffy");
        pet.setType(new PetType());
        pet.setBirthDate(LocalDate.of(2020, 1, 1));
        Errors errors = new BeanPropertyBindingResult(pet, "pet");
        new PetValidator().validate(pet, errors);
        assertThat(errors.hasErrors()).isFalse();
    }

    @Test
    void testValidateMissingName() {
        Pet pet = new Pet();
        pet.setType(new PetType());
        pet.setBirthDate(LocalDate.of(2020, 1, 1));
        Errors errors = new BeanPropertyBindingResult(pet, "pet");
        new PetValidator().validate(pet, errors);
        assertThat(errors.hasFieldErrors("name")).isTrue();
    }

    @Test
    void testValidateMissingType() {
        Pet pet = new Pet();
        pet.setName("Fluffy");
        pet.setBirthDate(LocalDate.of(2020, 1, 1));
        Errors errors = new BeanPropertyBindingResult(pet, "pet");
        new PetValidator().validate(pet, errors);
        assertThat(errors.hasFieldErrors("type")).isTrue();
    }

    @Test
    void testValidateMissingBirthDate() {
        Pet pet = new Pet();
        pet.setName("Fluffy");
        pet.setType(new PetType());
        Errors errors = new BeanPropertyBindingResult(pet, "pet");
        new PetValidator().validate(pet, errors);
        assertThat(errors.hasFieldErrors("birthDate")).isTrue();
    }
}
