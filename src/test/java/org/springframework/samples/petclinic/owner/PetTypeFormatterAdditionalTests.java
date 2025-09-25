package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Additional unit tests for PetTypeFormatter focusing on edge cases
 * to complement existing coverage without duplication.
 */
@ExtendWith(MockitoExtension.class)
class PetTypeFormatterAdditionalTests {

	@Mock
	private OwnerRepository owners;

	private PetTypeFormatter formatter;

	@BeforeEach
	void setup() {
		this.formatter = new PetTypeFormatter(this.owners);
		when(this.owners.findPetTypes()).thenReturn(makePetTypes());
	}

	@Test
	void parseShouldBeCaseSensitive() {
		// 'dog' (lowercase) should not match "Dog" as comparison is case-sensitive
		assertThatThrownBy(() -> this.formatter.parse("dog", Locale.ENGLISH))
			.isInstanceOf(ParseException.class);
	}

	@Test
	void parseShouldRejectNamesWithSurroundingWhitespace() {
		// Whitespace around a valid name should not parse successfully
		assertThatThrownBy(() -> this.formatter.parse(" Dog ", Locale.ENGLISH))
			.isInstanceOf(ParseException.class);
	}

	private List<PetType> makePetTypes() {
		List<PetType> petTypes = new ArrayList<>();
		PetType dog = new PetType();
		dog.setName("Dog");
		petTypes.add(dog);

		PetType bird = new PetType();
		bird.setName("Bird");
		petTypes.add(bird);

		return petTypes;
	}

}
