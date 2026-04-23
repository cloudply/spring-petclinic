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
 * @author Dave Syer
 */
class VetTests {

	private Vet vet;

	@BeforeEach
	void setUp() {
		vet = new Vet();
		vet.setFirstName("Zaphod");
		vet.setLastName("Beeblebrox");
		vet.setId(123);
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
	void testVetCreation() {
		Vet newVet = new Vet();
		assertThat(newVet).isNotNull();
		assertThat(newVet.getId()).isNull();
		assertThat(newVet.getFirstName()).isNull();
		assertThat(newVet.getLastName()).isNull();
		assertThat(newVet.isNew()).isTrue();
	}

	@Test
	void testVetWithBasicInfo() {
		assertThat(vet.getFirstName()).isEqualTo("Zaphod");
		assertThat(vet.getLastName()).isEqualTo("Beeblebrox");
		assertThat(vet.getId()).isEqualTo(123);
		assertThat(vet.isNew()).isFalse();
	}

	@Test
	void testGetSpecialtiesWhenEmpty() {
		List<Specialty> specialties = vet.getSpecialties();
		assertThat(specialties).isNotNull();
		assertThat(specialties).isEmpty();
		assertThat(vet.getNrOfSpecialties()).isZero();
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
	void testAddDuplicateSpecialty() {
		Specialty radiology = createSpecialty(1, "radiology");
		
		vet.addSpecialty(radiology);
		vet.addSpecialty(radiology); // Add same specialty twice
		
		// Set should prevent duplicates
		assertThat(vet.getNrOfSpecialties()).isEqualTo(1);
		assertThat(vet.getSpecialties()).hasSize(1);
	}

	@Test
	void testGetSpecialtiesReturnsUnmodifiableList() {
		Specialty radiology = createSpecialty(1, "radiology");
		vet.addSpecialty(radiology);
		
		List<Specialty> specialties = vet.getSpecialties();
		
		// Verify it's unmodifiable by attempting to modify it
		try {
			specialties.add(createSpecialty(2, "surgery"));
			// If we reach here, the list is modifiable (which it shouldn't be)
			assertThat(false).as("Expected UnsupportedOperationException").isTrue();
		} catch (UnsupportedOperationException e) {
			// This is expected behavior
			assertThat(true).isTrue();
		}
	}

	@Test
	void testSpecialtiesInternalManagement() {
		// Test that internal specialties set is properly initialized
		assertThat(vet.getNrOfSpecialties()).isZero();
		
		Specialty specialty = createSpecialty(1, "radiology");
		vet.addSpecialty(specialty);
		
		// Verify internal state is consistent
		assertThat(vet.getNrOfSpecialties()).isEqualTo(1);
	}

	@Test
	void testVetInheritanceFromPerson() {
		// Verify that Vet properly inherits from Person
		assertThat(vet.getFirstName()).isEqualTo("Zaphod");
		assertThat(vet.getLastName()).isEqualTo("Beeblebrox");
		
		// Test setting new values
		vet.setFirstName("John");
		vet.setLastName("Doe");
		
		assertThat(vet.getFirstName()).isEqualTo("John");
		assertThat(vet.getLastName()).isEqualTo("Doe");
	}

	@Test
	void testVetWithNullSpecialties() {
		// Create a vet and verify it handles null specialties gracefully
		Vet newVet = new Vet();
		assertThat(newVet.getSpecialties()).isNotNull();
		assertThat(newVet.getSpecialties()).isEmpty();
		assertThat(newVet.getNrOfSpecialties()).isZero();
	}

	@Test
	void testSpecialtyNameSorting() {
		// Test sorting with various name cases
		Specialty zSpecialty = createSpecialty(1, "Zoology");
		Specialty aSpecialty = createSpecialty(2, "Anesthesiology");
		Specialty mSpecialty = createSpecialty(3, "medicine");
		
		vet.addSpecialty(zSpecialty);
		vet.addSpecialty(aSpecialty);
		vet.addSpecialty(mSpecialty);
		
		List<Specialty> specialties = vet.getSpecialties();
		// Should be case-insensitive sorting
		assertThat(specialties.get(0).getName()).isEqualTo("Anesthesiology");
		assertThat(specialties.get(1).getName()).isEqualTo("medicine");
		assertThat(specialties.get(2).getName()).isEqualTo("Zoology");
	}

	@Test
	void testSerializationWithSpecialties() {
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
	}

	@Test
	void testVetEquality() {
		Vet vet1 = new Vet();
		vet1.setId(1);
		vet1.setFirstName("John");
		vet1.setLastName("Doe");
		
		Vet vet2 = new Vet();
		vet2.setId(1);
		vet2.setFirstName("John");
		vet2.setLastName("Doe");
		
		// Test that vets with same ID are considered equal (inherited from BaseEntity)
		assertThat(vet1).isEqualTo(vet2);
		assertThat(vet1.hashCode()).isEqualTo(vet2.hashCode());
	}

	@Test
	void testVetToString() {
		String vetString = vet.toString();
		assertThat(vetString).isNotNull();
		assertThat(vetString).contains("Zaphod");
		assertThat(vetString).contains("Beeblebrox");
	}

	@Test
	void testAddNullSpecialty() {
		// Test adding null specialty - should handle gracefully
		int initialCount = vet.getNrOfSpecialties();
		vet.addSpecialty(null);
		
		// The Set should handle null values according to HashSet behavior
		assertThat(vet.getNrOfSpecialties()).isEqualTo(initialCount + 1);
	}

	@Test
	void testSpecialtiesListConsistency() {
		Specialty surgery = createSpecialty(1, "surgery");
		Specialty dentistry = createSpecialty(2, "dentistry");
		
		vet.addSpecialty(surgery);
		vet.addSpecialty(dentistry);
		
		List<Specialty> specialties1 = vet.getSpecialties();
		List<Specialty> specialties2 = vet.getSpecialties();
		
		// Should return consistent results each time
		assertThat(specialties1).hasSize(specialties2.size());
		assertThat(specialties1).containsExactlyElementsOf(specialties2);
	}

	@Test
	void testSpecialtiesWithSameName() {
		// Test adding specialties with same name but different IDs
		Specialty surgery1 = createSpecialty(1, "surgery");
		Specialty surgery2 = createSpecialty(2, "surgery");
		
		vet.addSpecialty(surgery1);
		vet.addSpecialty(surgery2);
		
		// Both should be added since they have different IDs
		assertThat(vet.getNrOfSpecialties()).isEqualTo(2);
		assertThat(vet.getSpecialties()).hasSize(2);
	}

	@Test
	void testSpecialtiesWithEmptyName() {
		// Test specialty with empty name
		Specialty emptyNameSpecialty = createSpecialty(1, "");
		vet.addSpecialty(emptyNameSpecialty);
		
		assertThat(vet.getNrOfSpecialties()).isEqualTo(1);
		List<Specialty> specialties = vet.getSpecialties();
		assertThat(specialties.get(0).getName()).isEmpty();
	}

	@Test
	void testSpecialtiesWithNullName() {
		// Test specialty with null name
		Specialty nullNameSpecialty = new Specialty();
		nullNameSpecialty.setId(1);
		nullNameSpecialty.setName(null);
		
		vet.addSpecialty(nullNameSpecialty);
		
		assertThat(vet.getNrOfSpecialties()).isEqualTo(1);
		// The sorting should handle null names gracefully
		List<Specialty> specialties = vet.getSpecialties();
		assertThat(specialties).hasSize(1);
	}

	@Test
	void testLargeNumberOfSpecialties() {
		// Test performance with many specialties
		for (int i = 0; i < 50; i++) {
			Specialty specialty = createSpecialty(i, "specialty" + String.format("%02d", i));
			vet.addSpecialty(specialty);
		}
		
		assertThat(vet.getNrOfSpecialties()).isEqualTo(50);
		List<Specialty> specialties = vet.getSpecialties();
		assertThat(specialties).hasSize(50);
		
		// Verify they are sorted
		for (int i = 1; i < specialties.size(); i++) {
			String current = specialties.get(i).getName();
			String previous = specialties.get(i - 1).getName();
			assertThat(current.compareToIgnoreCase(previous)).isGreaterThanOrEqualTo(0);
		}
	}

	@Test
	void testSpecialtiesOrderingWithSpecialCharacters() {
		// Test sorting with special characters
		Specialty specialty1 = createSpecialty(1, "A-specialty");
		Specialty specialty2 = createSpecialty(2, "B_specialty");
		Specialty specialty3 = createSpecialty(3, "C specialty");
		
		vet.addSpecialty(specialty3);
		vet.addSpecialty(specialty1);
		vet.addSpecialty(specialty2);
		
		List<Specialty> specialties = vet.getSpecialties();
		assertThat(specialties).hasSize(3);
		// Verify alphabetical ordering
		assertThat(specialties.get(0).getName()).isEqualTo("A-specialty");
		assertThat(specialties.get(1).getName()).isEqualTo("B_specialty");
		assertThat(specialties.get(2).getName()).isEqualTo("C specialty");
	}

	@Test
	void testVetWithOnlyId() {
		// Test vet with only ID set
		Vet vetWithIdOnly = new Vet();
		vetWithIdOnly.setId(999);
		
		assertThat(vetWithIdOnly.getId()).isEqualTo(999);
		assertThat(vetWithIdOnly.getFirstName()).isNull();
		assertThat(vetWithIdOnly.getLastName()).isNull();
		assertThat(vetWithIdOnly.isNew()).isFalse();
		assertThat(vetWithIdOnly.getNrOfSpecialties()).isZero();
	}

	@Test
	void testVetWithOnlyNames() {
		// Test vet with only names set
		Vet vetWithNamesOnly = new Vet();
		vetWithNamesOnly.setFirstName("Jane");
		vetWithNamesOnly.setLastName("Smith");
		
		assertThat(vetWithNamesOnly.getId()).isNull();
		assertThat(vetWithNamesOnly.getFirstName()).isEqualTo("Jane");
		assertThat(vetWithNamesOnly.getLastName()).isEqualTo("Smith");
		assertThat(vetWithNamesOnly.isNew()).isTrue();
		assertThat(vetWithNamesOnly.getNrOfSpecialties()).isZero();
	}

	@Test
	void testSpecialtiesImmutabilityAfterRetrieval() {
		Specialty surgery = createSpecialty(1, "surgery");
		vet.addSpecialty(surgery);
		
		List<Specialty> specialties = vet.getSpecialties();
		int originalSize = specialties.size();
		
		// Try to modify the returned list
		try {
			specialties.clear();
			assertThat(false).as("Expected UnsupportedOperationException").isTrue();
		} catch (UnsupportedOperationException e) {
			// Expected behavior
		}
		
		// Verify original data is unchanged
		assertThat(vet.getNrOfSpecialties()).isEqualTo(originalSize);
	}

	private Specialty createSpecialty(int id, String name) {
		Specialty specialty = new Specialty();
		specialty.setId(id);
		specialty.setName(name);
		return specialty;
	}
}
