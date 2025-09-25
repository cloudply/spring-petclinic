package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PetValidatorTests {

	private final PetValidator validator = new PetValidator();

	@Test
	void shouldRejectWhenNameIsEmpty() {
		Pet pet = new Pet();
		pet.setType(new PetType());
		pet.setBirthDate(LocalDate.now());

		Errors errors = new BeanPropertyBindingResult(pet, "pet");
		validator.validate(pet, errors);

		assertThat(errors.hasFieldErrors("name")).isTrue();
	}

	@Test
	void shouldRejectWhenTypeIsNull() {
		Pet pet = new Pet();
		pet.setName("Luna");
		pet.setBirthDate(LocalDate.now());

		Errors errors = new BeanPropertyBindingResult(pet, "pet");
		validator.validate(pet, errors);

		assertThat(errors.hasFieldErrors("type")).isTrue();
	}

	@Test
	void shouldRejectWhenBirthDateIsNull() {
		Pet pet = new Pet();
		pet.setName("Charlie");
		pet.setType(new PetType());

		Errors errors = new BeanPropertyBindingResult(pet, "pet");
		validator.validate(pet, errors);

		assertThat(errors.hasFieldErrors("birthDate")).isTrue();
	}

	@Test
	void shouldAcceptValidPet() {
		Pet pet = new Pet();
		pet.setName("Milo");
		pet.setType(new PetType());
		pet.setBirthDate(LocalDate.now());

		Errors errors = new BeanPropertyBindingResult(pet, "pet");
		validator.validate(pet, errors);

		assertThat(errors.hasErrors()).isFalse();
	}
}
