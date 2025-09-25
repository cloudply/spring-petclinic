package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

/**
 * Additional edge-case tests for PetValidator to complement existing coverage.
 */
class PetValidatorEdgeTests {

	private final PetValidator validator = new PetValidator();

	@Test
	void validateShouldRejectWhitespaceOnlyName() {
		Pet pet = new Pet();
		pet.setName("   ");
		pet.setType(new PetType());
		pet.setBirthDate(LocalDate.now().minusDays(1));

		Errors errors = new BeanPropertyBindingResult(pet, "pet");
		validator.validate(pet, errors);

		assertThat(errors.hasFieldErrors("name")).isTrue();
	}

	@Test
	void validateShouldRejectFutureBirthDate() {
		Pet pet = new Pet();
		pet.setName("Fluffy");
		pet.setType(new PetType());
		pet.setBirthDate(LocalDate.now().plusDays(1));

		Errors errors = new BeanPropertyBindingResult(pet, "pet");
		validator.validate(pet, errors);

		assertThat(errors.hasFieldErrors("birthDate")).isTrue();
	}

	@Test
	void validateShouldAcceptTodayBirthDate() {
		Pet pet = new Pet();
		pet.setName("Fluffy");
		pet.setType(new PetType());
		pet.setBirthDate(LocalDate.now());

		Errors errors = new BeanPropertyBindingResult(pet, "pet");
		validator.validate(pet, errors);

		assertThat(errors.hasErrors()).isFalse();
	}

}
