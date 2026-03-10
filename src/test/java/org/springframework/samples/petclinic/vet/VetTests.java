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
package org.springframework.samples.petclinic.vet;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.SerializationUtils;

/**
 * Test class for the {@link Vet} entity.
 *
 * @author Dave Syer
 */
class VetTests {

	private Vet vet;

	@BeforeEach
	void setUp() {
		vet = new Vet();
		vet.setFirstName("John");
		vet.setLastName("Doe");
		vet.setId(1);
	}

	@Test
	void testSerialization() {
		Vet vet = new Vet();
		vet.setFirstName("Zaphod");
		vet.setLastName("Beeblebrox");
		vet.setId(123);
		@SuppressWarnings("deprecation")
		Vet other = (Vet) SerializationUtils.deserialize(SerializationUtils.serialize(vet));
		assertThat(other.getFirstName()).isEqualTo(vet.getFirstName());
		assertThat(other.getLastName()).isEqualTo(vet.getLastName());
		assertThat(other.getId()).isEqualTo(vet.getId());
	}

	@Test
	void testGetSpecialtiesWhenEmpty() {
		List<Specialty> specialties = vet.getSpecialties();
		assertThat(specialties).isNotNull();
		assertThat(specialties).isEmpty();
	}

	@Test
	void testGetNrOfSpecialtiesWhenEmpty() {
		int count = vet.getNrOfSpecialties();
		assertThat(count).isEqualTo(0);
	}

	@Test
	void testAddSingleSpecialty() {
		Specialty radiology = createSpecialty(1, "radiology");
		
		vet.addSpecialty(radiology);
		
		assertThat(vet.getNrOfSpecialties()).isEqualTo(1);
		assertThat(vet.getSpecialties()).hasSize(1);
		assertThat(vet.getSpecialties()).contains(radiology);
	}

	@Test
	void testAddMultipleSpecialties() {
		Specialty radiology = createSpecialty(1, "radiology");
		Specialty surgery = createSpecialty(2, "surgery");
		Specialty dentistry = createSpecialty(3, "dentistry");
		
		vet.addSpecialty(radiology);
		vet.addSpecialty(surgery);
		vet.addSpecialty(dentistry);
		
		assertThat(vet.getNrOfSpecialties()).isEqualTo(3);
		assertThat(vet.getSpecialties()).hasSize(3);
		assertThat(vet.getSpecialties()).containsExactlyInAnyOrder(radiology, surgery, dentistry);
	}

	@Test
	void testAddDuplicateSpecialty() {
		Specialty radiology = createSpecialty(1, "radiology");
		
		vet.addSpecialty(radiology);
		vet.addSpecialty(radiology); // Adding the same specialty again
		
		// Set should prevent duplicates
		assertThat(vet.getNrOfSpecialties()).isEqualTo(1);
		assertThat(vet.getSpecialties()).hasSize(1);
	}

	@Test
	void testSpecialtiesAreSortedByName() {
		Specialty surgery = createSpecialty(2, "surgery");
		Specialty dentistry = createSpecialty(3, "dentistry");
		Specialty radiology = createSpecialty(1, "radiology");
		
		// Add in non-alphabetical order
		vet.addSpecialty(surgery);
		vet.addSpecialty(dentistry);
		vet.addSpecialty(radiology);
		
		List<Specialty> specialties = vet.getSpecialties();
		assertThat(specialties).hasSize(3);
		// Should be sorted alphabetically by name
		assertThat(specialties.get(0).getName()).isEqualTo("dentistry");
		assertThat(specialties.get(1).getName()).isEqualTo("radiology");
		assertThat(specialties.get(2).getName()).isEqualTo("surgery");
	}

	@Test
	void testGetSpecialtiesReturnsUnmodifiableList() {
		Specialty radiology = createSpecialty(1, "radiology");
		vet.addSpecialty(radiology);
		
		List<Specialty> specialties = vet.getSpecialties();
		
		// Verify the returned list is unmodifiable
		assertThat(specialties).isInstanceOf(java.util.Collections.UnmodifiableList.class);
	}

	@Test
	void testVetInheritanceFromPerson() {
		// Test that Vet properly inherits from Person
		vet.setFirstName("Jane");
		vet.setLastName("Smith");
		
		assertThat(vet.getFirstName()).isEqualTo("Jane");
		assertThat(vet.getLastName()).isEqualTo("Smith");
	}

	@Test
	void testVetWithNullSpecialtyHandling() {
		// Test adding null specialty (should not cause issues due to HashSet)
		vet.addSpecialty(null);
		
		assertThat(vet.getNrOfSpecialties()).isEqualTo(1); // HashSet allows one null
		assertThat(vet.getSpecialties()).hasSize(1);
		assertThat(vet.getSpecialties()).containsOnlyNulls();
	}

	@Test
	void testSpecialtiesCaseInsensitiveSorting() {
		Specialty upperCase = createSpecialty(1, "RADIOLOGY");
		Specialty lowerCase = createSpecialty(2, "surgery");
		Specialty mixedCase = createSpecialty(3, "Dentistry");
		
		vet.addSpecialty(upperCase);
		vet.addSpecialty(lowerCase);
		vet.addSpecialty(mixedCase);
		
		List<Specialty> specialties = vet.getSpecialties();
		// Should be sorted case-insensitively
		assertThat(specialties.get(0).getName()).isEqualTo("Dentistry");
		assertThat(specialties.get(1).getName()).isEqualTo("RADIOLOGY");
		assertThat(specialties.get(2).getName()).isEqualTo("surgery");
	}

	@Test
	void testVetSerializationWithSpecialties() {
		Specialty radiology = createSpecialty(1, "radiology");
		Specialty surgery = createSpecialty(2, "surgery");
		
		vet.addSpecialty(radiology);
		vet.addSpecialty(surgery);
		
		@SuppressWarnings("deprecation")
		Vet deserializedVet = (Vet) SerializationUtils.deserialize(SerializationUtils.serialize(vet));
		
		assertThat(deserializedVet.getFirstName()).isEqualTo(vet.getFirstName());
		assertThat(deserializedVet.getLastName()).isEqualTo(vet.getLastName());
		assertThat(deserializedVet.getId()).isEqualTo(vet.getId());
		assertThat(deserializedVet.getNrOfSpecialties()).isEqualTo(2);
		assertThat(deserializedVet.getSpecialties()).hasSize(2);
	}

	private Specialty createSpecialty(Integer id, String name) {
		Specialty specialty = new Specialty();
		specialty.setId(id);
		specialty.setName(name);
		return specialty;
	}

}
