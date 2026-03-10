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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
		vet.setFirstName("James");
		vet.setLastName("Carter");
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
	void testGetSpecialtiesInitiallyEmpty() {
		List<Specialty> specialties = vet.getSpecialties();
		assertThat(specialties).isNotNull();
		assertThat(specialties).isEmpty();
	}

	@Test
	void testGetNrOfSpecialtiesInitiallyZero() {
		assertThat(vet.getNrOfSpecialties()).isEqualTo(0);
	}

	@Test
	void testAddSpecialty() {
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
		vet.addSpecialty(radiology); // Adding same specialty again
		
		// Set should not contain duplicates
		assertThat(vet.getNrOfSpecialties()).isEqualTo(1);
		assertThat(vet.getSpecialties()).hasSize(1);
	}

	@Test
	void testGetSpecialtiesReturnsSortedList() {
		Specialty surgery = createSpecialty(2, "surgery");
		Specialty dentistry = createSpecialty(3, "dentistry");
		Specialty radiology = createSpecialty(1, "radiology");
		
		// Add in non-alphabetical order
		vet.addSpecialty(surgery);
		vet.addSpecialty(dentistry);
		vet.addSpecialty(radiology);
		
		List<Specialty> specialties = vet.getSpecialties();
		
		// Should be sorted alphabetically by name
		assertThat(specialties).hasSize(3);
		assertThat(specialties.get(0).getName()).isEqualTo("dentistry");
		assertThat(specialties.get(1).getName()).isEqualTo("radiology");
		assertThat(specialties.get(2).getName()).isEqualTo("surgery");
	}

	@Test
	void testGetSpecialtiesReturnsUnmodifiableList() {
		Specialty radiology = createSpecialty(1, "radiology");
		vet.addSpecialty(radiology);
		
		List<Specialty> specialties = vet.getSpecialties();
		
		// Should throw exception when trying to modify the returned list
		assertThatThrownBy(() -> specialties.add(createSpecialty(2, "surgery")))
			.isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	void testGetSpecialtiesConsistentResults() {
		Specialty radiology = createSpecialty(1, "radiology");
		Specialty surgery = createSpecialty(2, "surgery");
		
		vet.addSpecialty(radiology);
		vet.addSpecialty(surgery);
		
		List<Specialty> specialties1 = vet.getSpecialties();
		List<Specialty> specialties2 = vet.getSpecialties();
		
		// Multiple calls should return equivalent lists
		assertThat(specialties1).isEqualTo(specialties2);
		assertThat(specialties1).isNotSameAs(specialties2); // But different instances
	}

	@Test
	void testAddNullSpecialty() {
		vet.addSpecialty(null);
		
		// Adding null should still work (Set allows null)
		assertThat(vet.getNrOfSpecialties()).isEqualTo(1);
		assertThat(vet.getSpecialties()).hasSize(1);
		assertThat(vet.getSpecialties()).contains(null);
	}

	@Test
	void testSetSpecialtiesInternal() {
		Set<Specialty> specialties = new HashSet<>();
		Specialty radiology = createSpecialty(1, "radiology");
		Specialty surgery = createSpecialty(2, "surgery");
		specialties.add(radiology);
		specialties.add(surgery);
		
		// Use reflection to access protected method or create a test subclass
		TestableVet testVet = new TestableVet();
		testVet.setSpecialtiesInternal(specialties);
		
		assertThat(testVet.getNrOfSpecialties()).isEqualTo(2);
		assertThat(testVet.getSpecialties()).containsExactlyInAnyOrder(radiology, surgery);
	}

	@Test
	void testGetSpecialtiesInternalInitialization() {
		TestableVet testVet = new TestableVet();
		
		// First call should initialize the set
		Set<Specialty> specialties = testVet.getSpecialtiesInternal();
		
		assertThat(specialties).isNotNull();
		assertThat(specialties).isEmpty();
		
		// Second call should return the same instance
		Set<Specialty> specialties2 = testVet.getSpecialtiesInternal();
		assertThat(specialties2).isSameAs(specialties);
	}

	@Test
	void testSpecialtiesSortingWithNullNames() {
		TestableVet testVet = new TestableVet();
		
		Specialty specialtyWithNullName = new Specialty();
		specialtyWithNullName.setId(1);
		// name is null
		
		Specialty normalSpecialty = createSpecialty(2, "surgery");
		
		testVet.addSpecialty(specialtyWithNullName);
		testVet.addSpecialty(normalSpecialty);
		
		// Should handle null names gracefully
		List<Specialty> specialties = testVet.getSpecialties();
		assertThat(specialties).hasSize(2);
		// The one with null name should come first in sorting
		assertThat(specialties.get(0)).isEqualTo(specialtyWithNullName);
		assertThat(specialties.get(1)).isEqualTo(normalSpecialty);
	}

	@Test
	void testSpecialtiesSortingCaseInsensitive() {
		Specialty upperCase = createSpecialty(1, "SURGERY");
		Specialty lowerCase = createSpecialty(2, "radiology");
		Specialty mixedCase = createSpecialty(3, "Dentistry");
		
		vet.addSpecialty(upperCase);
		vet.addSpecialty(lowerCase);
		vet.addSpecialty(mixedCase);
		
		List<Specialty> specialties = vet.getSpecialties();
		
		// Should be sorted case-insensitively
		assertThat(specialties.get(0).getName()).isEqualTo("Dentistry");
		assertThat(specialties.get(1).getName()).isEqualTo("radiology");
		assertThat(specialties.get(2).getName()).isEqualTo("SURGERY");
	}

	@Test
	void testVetInheritanceFromPerson() {
		// Test that Vet properly inherits from Person
		assertThat(vet.getFirstName()).isEqualTo("James");
		assertThat(vet.getLastName()).isEqualTo("Carter");
		assertThat(vet.getId()).isEqualTo(1);
		
		// Test setting new values
		vet.setFirstName("Helen");
		vet.setLastName("Leary");
		vet.setId(2);
		
		assertThat(vet.getFirstName()).isEqualTo("Helen");
		assertThat(vet.getLastName()).isEqualTo("Leary");
		assertThat(vet.getId()).isEqualTo(2);
	}

	@Test
	void testSerializationWithSpecialties() {
		Specialty radiology = createSpecialty(1, "radiology");
		Specialty surgery = createSpecialty(2, "surgery");
		
		vet.addSpecialty(radiology);
		vet.addSpecialty(surgery);
		
		@SuppressWarnings("deprecation")
		Vet other = (Vet) SerializationUtils.deserialize(SerializationUtils.serialize(vet));
		
		assertThat(other.getFirstName()).isEqualTo(vet.getFirstName());
		assertThat(other.getLastName()).isEqualTo(vet.getLastName());
		assertThat(other.getId()).isEqualTo(vet.getId());
		assertThat(other.getNrOfSpecialties()).isEqualTo(vet.getNrOfSpecialties());
		assertThat(other.getSpecialties()).containsExactlyInAnyOrderElementsOf(vet.getSpecialties());
	}

	@Test
	void testEmptySpecialtiesAfterAddingAndRemoving() {
		TestableVet testVet = new TestableVet();
		Specialty radiology = createSpecialty(1, "radiology");
		
		testVet.addSpecialty(radiology);
		assertThat(testVet.getNrOfSpecialties()).isEqualTo(1);
		
		// Remove specialty by setting empty set
		testVet.setSpecialtiesInternal(new HashSet<>());
		assertThat(testVet.getNrOfSpecialties()).isEqualTo(0);
		assertThat(testVet.getSpecialties()).isEmpty();
	}

	private Specialty createSpecialty(int id, String name) {
		Specialty specialty = new Specialty();
		specialty.setId(id);
		specialty.setName(name);
		return specialty;
	}

	// Test subclass to access protected methods
	private static class TestableVet extends Vet {
		@Override
		public Set<Specialty> getSpecialtiesInternal() {
			return super.getSpecialtiesInternal();
		}

		@Override
		public void setSpecialtiesInternal(Set<Specialty> specialties) {
			super.setSpecialtiesInternal(specialties);
		}
	}
}
