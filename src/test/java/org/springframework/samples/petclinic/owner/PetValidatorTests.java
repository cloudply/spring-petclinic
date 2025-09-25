package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

class PetValidatorTests {

	@Test
	void supportsShouldReturnTrueForPet() {
		PetValidator validator = new PetValidator();
		assertThat(validator.supports(Pet.class)).isTrue();
	}

	@Test
	void supportsShouldReturnFalseForNonPet() {
		PetValidator validator = new PetValidator();
		assertThat(validator.supports(Owner.class)).isFalse();
	}

	@Test
	void validateShouldRejectMissingFields() {
		Pet pet = new Pet(); // name=null, type=null, birthDate=null
		PetValidator validator = new PetValidator();
		Errors errors = new BeanPropertyBindingResult(pet, "pet");

		validator.validate(pet, errors);

		assertThat(errors.hasFieldErrors("name")).isTrue();
		assertThat(errors.hasFieldErrors("type")).isTrue();
		assertThat(errors.hasFieldErrors("birthDate")).isTrue();
	}

	@Test
	void validateShouldRejectBlankName() {
		Pet pet = new Pet();
		pet.setName("   "); // blank
		pet.setBirthDate(LocalDate.now());
		PetType type = new PetType();
		type.setName("dog");
		pet.setType(type);

		PetValidator validator = new PetValidator();
		Errors errors = new BeanPropertyBindingResult(pet, "pet");

		validator.validate(pet, errors);

		assertThat(errors.hasFieldErrors("name")).isTrue();
		assertThat(errors.hasFieldErrors("type")).isFalse();
		assertThat(errors.hasFieldErrors("birthDate")).isFalse();
	}

	@Test
	void validateShouldPassWhenAllFieldsPresent() {
		Pet pet = new Pet();
		pet.setName("Max");
		pet.setBirthDate(LocalDate.now());
		PetType type = new PetType();
		type.setName("dog");
		pet.setType(type);

		PetValidator validator = new PetValidator();
		Errors errors = new BeanPropertyBindingResult(pet, "pet");

		validator.validate(pet, errors);

		assertThat(errors.hasErrors()).isFalse();
	}
}
