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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for {@link PetTypeFormatter}
 *
 * @author Colin But
 */
@ExtendWith(MockitoExtension.class)
@DisabledInNativeImage
class PetTypeFormatterTests {

	@Mock
	private OwnerRepository pets;

	private PetTypeFormatter petTypeFormatter;

	@BeforeEach
	void setup() {
		this.petTypeFormatter = new PetTypeFormatter(pets);
	}

	@Test
	void testPrint() {
		PetType petType = new PetType();
		petType.setName("Hamster");
		String petTypeName = this.petTypeFormatter.print(petType, Locale.ENGLISH);
		assertThat(petTypeName).isEqualTo("Hamster");
	}

	@Test
	void shouldParse() throws ParseException {
		given(this.pets.findPetTypes()).willReturn(makePetTypes());
		PetType petType = petTypeFormatter.parse("Bird", Locale.ENGLISH);
		assertThat(petType.getName()).isEqualTo("Bird");
	}

	@Test
	void shouldThrowParseException() throws ParseException {
		given(this.pets.findPetTypes()).willReturn(makePetTypes());
		Assertions.assertThrows(ParseException.class, () -> {
			petTypeFormatter.parse("Fish", Locale.ENGLISH);
		});
	}

	@Test
	void testPrintWithNullPetType() {
		assertThatThrownBy(() -> petTypeFormatter.print(null, Locale.ENGLISH))
			.isInstanceOf(NullPointerException.class);
	}

	@Test
	void testPrintWithNullPetTypeName() {
		PetType petType = new PetType();
		petType.setName(null);
		String result = petTypeFormatter.print(petType, Locale.ENGLISH);
		assertThat(result).isNull();
	}

	@Test
	void testPrintWithEmptyPetTypeName() {
		PetType petType = new PetType();
		petType.setName("");
		String result = petTypeFormatter.print(petType, Locale.ENGLISH);
		assertThat(result).isEmpty();
	}

	@Test
	void testPrintWithWhitespacePetTypeName() {
		PetType petType = new PetType();
		petType.setName("   ");
		String result = petTypeFormatter.print(petType, Locale.ENGLISH);
		assertThat(result).isEqualTo("   ");
	}

	@Test
	void testPrintWithDifferentLocales() {
		PetType petType = new PetType();
		petType.setName("Cat");
		
		String resultEnglish = petTypeFormatter.print(petType, Locale.ENGLISH);
		String resultFrench = petTypeFormatter.print(petType, Locale.FRENCH);
		String resultGerman = petTypeFormatter.print(petType, Locale.GERMAN);
		
		assertThat(resultEnglish).isEqualTo("Cat");
		assertThat(resultFrench).isEqualTo("Cat");
		assertThat(resultGerman).isEqualTo("Cat");
	}

	@Test
	void testParseWithNullText() {
		given(this.pets.findPetTypes()).willReturn(makePetTypes());
		
		assertThatThrownBy(() -> petTypeFormatter.parse(null, Locale.ENGLISH))
			.isInstanceOf(ParseException.class)
			.hasMessageContaining("type not found: null");
	}

	@Test
	void testParseWithEmptyText() {
		given(this.pets.findPetTypes()).willReturn(makePetTypes());
		
		assertThatThrownBy(() -> petTypeFormatter.parse("", Locale.ENGLISH))
			.isInstanceOf(ParseException.class)
			.hasMessageContaining("type not found: ");
	}

	@Test
	void testParseWithWhitespaceText() {
		given(this.pets.findPetTypes()).willReturn(makePetTypes());
		
		assertThatThrownBy(() -> petTypeFormatter.parse("   ", Locale.ENGLISH))
			.isInstanceOf(ParseException.class)
			.hasMessageContaining("type not found:    ");
	}

	@Test
	void testParseCaseSensitive() {
		given(this.pets.findPetTypes()).willReturn(makePetTypes());
		
		// Should not find "dog" (lowercase) when "Dog" (uppercase) exists
		assertThatThrownBy(() -> petTypeFormatter.parse("dog", Locale.ENGLISH))
			.isInstanceOf(ParseException.class)
			.hasMessageContaining("type not found: dog");
		
		// Should not find "BIRD" (uppercase) when "Bird" (mixed case) exists
		assertThatThrownBy(() -> petTypeFormatter.parse("BIRD", Locale.ENGLISH))
			.isInstanceOf(ParseException.class)
			.hasMessageContaining("type not found: BIRD");
	}

	@Test
	void testParseWithEmptyPetTypesList() {
		given(this.pets.findPetTypes()).willReturn(new ArrayList<>());
		
		assertThatThrownBy(() -> petTypeFormatter.parse("Dog", Locale.ENGLISH))
			.isInstanceOf(ParseException.class)
			.hasMessageContaining("type not found: Dog");
	}

	@Test
	void testParseWithNullPetTypesList() {
		given(this.pets.findPetTypes()).willReturn(null);
		
		assertThatThrownBy(() -> petTypeFormatter.parse("Dog", Locale.ENGLISH))
			.isInstanceOf(NullPointerException.class);
	}

	@Test
	void testParseWithPetTypeHavingNullName() throws ParseException {
		List<PetType> petTypesWithNull = new ArrayList<>();
		PetType petTypeWithNullName = new PetType();
		petTypeWithNullName.setName(null);
		petTypesWithNull.add(petTypeWithNullName);
		
		PetType validPetType = new PetType();
		validPetType.setName("Cat");
		petTypesWithNull.add(validPetType);
		
		given(this.pets.findPetTypes()).willReturn(petTypesWithNull);
		
		// Should find the valid pet type
		PetType result = petTypeFormatter.parse("Cat", Locale.ENGLISH);
		assertThat(result.getName()).isEqualTo("Cat");
		
		// Should not find a match for non-existent type
		assertThatThrownBy(() -> petTypeFormatter.parse("Dog", Locale.ENGLISH))
			.isInstanceOf(ParseException.class);
	}

	@Test
	void testParseWithDifferentLocales() throws ParseException {
		given(this.pets.findPetTypes()).willReturn(makePetTypes());
		
		PetType resultEnglish = petTypeFormatter.parse("Dog", Locale.ENGLISH);
		PetType resultFrench = petTypeFormatter.parse("Dog", Locale.FRENCH);
		PetType resultGerman = petTypeFormatter.parse("Dog", Locale.GERMAN);
		
		assertThat(resultEnglish.getName()).isEqualTo("Dog");
		assertThat(resultFrench.getName()).isEqualTo("Dog");
		assertThat(resultGerman.getName()).isEqualTo("Dog");
	}

	@Test
	void testParseWithSpecialCharacters() {
		List<PetType> specialPetTypes = new ArrayList<>();
		PetType specialType = new PetType();
		specialType.setName("Exotic-Bird");
		specialPetTypes.add(specialType);
		
		given(this.pets.findPetTypes()).willReturn(specialPetTypes);
		
		assertThatThrownBy(() -> petTypeFormatter.parse("Exotic Bird", Locale.ENGLISH))
			.isInstanceOf(ParseException.class);
	}

	@Test
	void testParseExactMatch() throws ParseException {
		List<PetType> petTypes = new ArrayList<>();
		
		PetType dog = new PetType();
		dog.setName("Dog");
		petTypes.add(dog);
		
		PetType doggy = new PetType();
		doggy.setName("Doggy");
		petTypes.add(doggy);
		
		given(this.pets.findPetTypes()).willReturn(petTypes);
		
		PetType result = petTypeFormatter.parse("Dog", Locale.ENGLISH);
		assertThat(result.getName()).isEqualTo("Dog");
		
		PetType result2 = petTypeFormatter.parse("Doggy", Locale.ENGLISH);
		assertThat(result2.getName()).isEqualTo("Doggy");
	}

	@Test
	void testParseReturnsFirstMatchingPetType() throws ParseException {
		List<PetType> duplicatePetTypes = new ArrayList<>();
		
		PetType firstDog = new PetType();
		firstDog.setName("Dog");
		firstDog.setId(1);
		duplicatePetTypes.add(firstDog);
		
		PetType secondDog = new PetType();
		secondDog.setName("Dog");
		secondDog.setId(2);
		duplicatePetTypes.add(secondDog);
		
		given(this.pets.findPetTypes()).willReturn(duplicatePetTypes);
		
		PetType result = petTypeFormatter.parse("Dog", Locale.ENGLISH);
		assertThat(result.getName()).isEqualTo("Dog");
		assertThat(result.getId()).isEqualTo(1); // Should return the first match
	}

	/**
	 * Helper method to produce some sample pet types just for test purpose
	 * @return {@link Collection} of {@link PetType}
	 */
	private List<PetType> makePetTypes() {
		List<PetType> petTypes = new ArrayList<>();
		petTypes.add(new PetType() {
			{
				setName("Dog");
			}
		});
		petTypes.add(new PetType() {
			{
				setName("Bird");
			}
		});
		return petTypes;
	}

}
