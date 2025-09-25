package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

class PetValidatorTests {

	@Test
	void supportsShouldReturnTrueForPetAndFalseForOthers() {
		PetValidator validator = new PetValidator();

		assertThat(validator.supports(Pet.class)).isTrue();
		assertThat(validator.supports(Owner.class)).isFalse();
	}

	@Test
	void validateShouldRejectEmptyName() {
		Pet pet = new Pet();
		// leave name empty
		pet.setBirthDate(LocalDate.now());
		pet.setType(new PetType());

		Errors errors = new BeanPropertyBindingResult(pet, "pet");

		new PetValidator().validate(pet, errors);

		assertThat(errors.hasFieldErrors("name")).isTrue();
		assertThat(errors.getFieldError("name").getCode()).isEqualTo("required");
	}

	@Test
	void validateShouldRejectNullType() {
		Pet pet = new Pet();
		pet.setName("Buddy");
		pet.setBirthDate(LocalDate.now());
		// type is null

		Errors errors = new BeanPropertyBindingResult(pet, "pet");

		new PetValidator().validate(pet, errors);

		assertThat(errors.hasFieldErrors("type")).isTrue();
		assertThat(errors.getFieldError("type").getCode()).isEqualTo("required");
	}

	@Test
	void validateShouldRejectNullBirthDate() {
		Pet pet = new Pet();
		pet.setName("Buddy");
		pet.setType(new PetType());
		// birthDate is null

		Errors errors = new BeanPropertyBindingResult(pet, "pet");

		new PetValidator().validate(pet, errors);

		assertThat(errors.hasFieldErrors("birthDate")).isTrue();
		assertThat(errors.getFieldError("birthDate").getCode()).isEqualTo("required");
	}

	@Test
	void validateShouldPassForValidPet() {
		Pet pet = new Pet();
		pet.setName("Buddy");
		pet.setBirthDate(LocalDate.now());
		pet.setType(new PetType());

		Errors errors = new BeanPropertyBindingResult(pet, "pet");

		new PetValidator().validate(pet, errors);

		assertThat(errors.hasErrors()).isFalse();
	}
}
