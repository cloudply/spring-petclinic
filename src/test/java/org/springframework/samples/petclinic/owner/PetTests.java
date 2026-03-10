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

import java.time.LocalDate;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.SerializationUtils;

/**
 * Test class for {@link Pet}
 */
class PetTests {

	private Pet pet;
	private PetType petType;
	private Visit visit1;
	private Visit visit2;

	@BeforeEach
	void setUp() {
		pet = new Pet();
		petType = new PetType();
		petType.setId(1);
		petType.setName("Dog");
		
		visit1 = new Visit();
		visit1.setId(1);
		visit1.setDate(LocalDate.of(2023, 1, 15));
		visit1.setDescription("Regular checkup");
		
		visit2 = new Visit();
		visit2.setId(2);
		visit2.setDate(LocalDate.of(2023, 2, 20));
		visit2.setDescription("Vaccination");
	}

	@Test
	void testPetCreation() {
		assertThat(pet).isNotNull();
		assertThat(pet.getId()).isNull();
		assertThat(pet.getName()).isNull();
		assertThat(pet.getBirthDate()).isNull();
		assertThat(pet.getType()).isNull();
		assertThat(pet.getVisits()).isNotNull();
		assertThat(pet.getVisits()).isEmpty();
		assertThat(pet.isNew()).isTrue();
	}

	@Test
	void testSetAndGetBirthDate() {
		LocalDate birthDate = LocalDate.of(2020, 5, 15);
		pet.setBirthDate(birthDate);
		
		assertThat(pet.getBirthDate()).isEqualTo(birthDate);
	}

	@Test
	void testSetAndGetBirthDateWithNull() {
		pet.setBirthDate(null);
		
		assertThat(pet.getBirthDate()).isNull();
	}

	@Test
	void testSetAndGetType() {
		pet.setType(petType);
		
		assertThat(pet.getType()).isEqualTo(petType);
		assertThat(pet.getType().getName()).isEqualTo("Dog");
		assertThat(pet.getType().getId()).isEqualTo(1);
	}

	@Test
	void testSetAndGetTypeWithNull() {
		pet.setType(null);
		
		assertThat(pet.getType()).isNull();
	}

	@Test
	void testGetVisitsInitiallyEmpty() {
		Collection<Visit> visits = pet.getVisits();
		
		assertThat(visits).isNotNull();
		assertThat(visits).isEmpty();
	}

	@Test
	void testAddSingleVisit() {
		pet.addVisit(visit1);
		
		Collection<Visit> visits = pet.getVisits();
		assertThat(visits).hasSize(1);
		assertThat(visits).contains(visit1);
	}

	@Test
	void testAddMultipleVisits() {
		pet.addVisit(visit1);
		pet.addVisit(visit2);
		
		Collection<Visit> visits = pet.getVisits();
		assertThat(visits).hasSize(2);
		assertThat(visits).contains(visit1, visit2);
	}

	@Test
	void testAddDuplicateVisit() {
		pet.addVisit(visit1);
		pet.addVisit(visit1);
		
		Collection<Visit> visits = pet.getVisits();
		// Since it's a Set, duplicate should not be added
		assertThat(visits).hasSize(1);
		assertThat(visits).contains(visit1);
	}

	@Test
	void testVisitsOrderedByDate() {
		// Add visits in reverse chronological order
		pet.addVisit(visit2); // 2023-02-20
		pet.addVisit(visit1); // 2023-01-15
		
		Collection<Visit> visits = pet.getVisits();
		assertThat(visits).hasSize(2);
		
		// Convert to array to check order
		Visit[] visitArray = visits.toArray(new Visit[0]);
		assertThat(visitArray[0]).isEqualTo(visit1); // Earlier date should come first
		assertThat(visitArray[1]).isEqualTo(visit2);
	}

	@Test
	void testPetWithAllProperties() {
		LocalDate birthDate = LocalDate.of(2019, 3, 10);
		pet.setId(1);
		pet.setName("Max");
		pet.setBirthDate(birthDate);
		pet.setType(petType);
		pet.addVisit(visit1);
		
		assertThat(pet.getId()).isEqualTo(1);
		assertThat(pet.getName()).isEqualTo("Max");
		assertThat(pet.getBirthDate()).isEqualTo(birthDate);
		assertThat(pet.getType()).isEqualTo(petType);
		assertThat(pet.getVisits()).hasSize(1);
		assertThat(pet.isNew()).isFalse();
	}

	@Test
	void testPetInheritedMethods() {
		// Test inherited methods from NamedEntity
		pet.setName("Buddy");
		assertThat(pet.getName()).isEqualTo("Buddy");
		
		// Test inherited methods from BaseEntity
		pet.setId(5);
		assertThat(pet.getId()).isEqualTo(5);
		assertThat(pet.isNew()).isFalse();
	}

	@Test
	void testPetToString() {
		pet.setName("Charlie");
		String result = pet.toString();
		
		assertThat(result).contains("Charlie");
	}

	@Test
	void testPetWithFutureBirthDate() {
		LocalDate futureBirthDate = LocalDate.now().plusDays(30);
		pet.setBirthDate(futureBirthDate);
		
		assertThat(pet.getBirthDate()).isEqualTo(futureBirthDate);
	}

	@Test
	void testPetWithPastBirthDate() {
		LocalDate pastBirthDate = LocalDate.of(2010, 1, 1);
		pet.setBirthDate(pastBirthDate);
		
		assertThat(pet.getBirthDate()).isEqualTo(pastBirthDate);
	}

	@Test
	void testPetWithCurrentDateBirthDate() {
		LocalDate currentDate = LocalDate.now();
		pet.setBirthDate(currentDate);
		
		assertThat(pet.getBirthDate()).isEqualTo(currentDate);
	}

	@Test
	void testMultiplePetTypesAssignment() {
		PetType cat = new PetType();
		cat.setId(2);
		cat.setName("Cat");
		
		// Initially set to dog
		pet.setType(petType);
		assertThat(pet.getType().getName()).isEqualTo("Dog");
		
		// Change to cat
		pet.setType(cat);
		assertThat(pet.getType().getName()).isEqualTo("Cat");
		assertThat(pet.getType().getId()).isEqualTo(2);
	}

	@Test
	void testVisitCollectionModification() {
		pet.addVisit(visit1);
		Collection<Visit> visits = pet.getVisits();
		
		// Verify we can iterate over visits
		int count = 0;
		for (Visit visit : visits) {
			count++;
			assertThat(visit).isNotNull();
		}
		assertThat(count).isEqualTo(1);
	}

	@Test
	void testPetWithEmptyName() {
		pet.setName("");
		assertThat(pet.getName()).isEqualTo("");
	}

	@Test
	void testPetWithWhitespaceName() {
		pet.setName("   ");
		assertThat(pet.getName()).isEqualTo("   ");
	}

	@Test
	void testPetWithLongName() {
		String longName = "A".repeat(100);
		pet.setName(longName);
		assertThat(pet.getName()).isEqualTo(longName);
	}

	@Test
	void testPetWithSpecialCharactersInName() {
		String specialName = "Max-O'Connor Jr.";
		pet.setName(specialName);
		assertThat(pet.getName()).isEqualTo(specialName);
	}

	@Test
	void testPetIdBoundaryValues() {
		// Test with zero
		pet.setId(0);
		assertThat(pet.getId()).isEqualTo(0);
		assertThat(pet.isNew()).isFalse();
		
		// Test with negative value
		pet.setId(-1);
		assertThat(pet.getId()).isEqualTo(-1);
		assertThat(pet.isNew()).isFalse();
		
		// Test with large value
		pet.setId(Integer.MAX_VALUE);
		assertThat(pet.getId()).isEqualTo(Integer.MAX_VALUE);
		assertThat(pet.isNew()).isFalse();
	}

	@Test
	void testPetResetId() {
		pet.setId(10);
		assertThat(pet.isNew()).isFalse();
		
		pet.setId(null);
		assertThat(pet.isNew()).isTrue();
	}

	@Test
	void testPetSerialization() {
		pet.setId(123);
		pet.setName("Fluffy");
		pet.setBirthDate(LocalDate.of(2020, 6, 15));
		pet.setType(petType);
		pet.addVisit(visit1);
		
		@SuppressWarnings("deprecation")
		Pet deserializedPet = (Pet) SerializationUtils.deserialize(SerializationUtils.serialize(pet));
		
		assertThat(deserializedPet.getId()).isEqualTo(pet.getId());
		assertThat(deserializedPet.getName()).isEqualTo(pet.getName());
		assertThat(deserializedPet.getBirthDate()).isEqualTo(pet.getBirthDate());
		assertThat(deserializedPet.getType().getName()).isEqualTo(pet.getType().getName());
		assertThat(deserializedPet.getVisits()).hasSize(1);
	}

	@Test
	void testPetEquality() {
		Pet pet1 = new Pet();
		pet1.setId(1);
		pet1.setName("Max");
		
		Pet pet2 = new Pet();
		pet2.setId(1);
		pet2.setName("Max");
		
		// Note: BaseEntity doesn't override equals/hashCode, so this tests object identity
		assertThat(pet1).isNotEqualTo(pet2);
		assertThat(pet1).isEqualTo(pet1);
	}

	@Test
	void testPetWithNullVisit() {
		// Test edge case - what happens if we try to add null visit
		// This should be handled gracefully by the implementation
		Collection<Visit> visitsBefore = pet.getVisits();
		int sizeBefore = visitsBefore.size();
		
		try {
			pet.addVisit(null);
			// If no exception is thrown, verify the collection state
			assertThat(pet.getVisits()).hasSize(sizeBefore);
		} catch (Exception e) {
			// If an exception is thrown, that's also acceptable behavior
			assertThat(e).isNotNull();
		}
	}

	@Test
	void testPetBirthDateEdgeCases() {
		// Test with leap year date
		LocalDate leapYearDate = LocalDate.of(2020, 2, 29);
		pet.setBirthDate(leapYearDate);
		assertThat(pet.getBirthDate()).isEqualTo(leapYearDate);
		
		// Test with end of year date
		LocalDate endOfYear = LocalDate.of(2023, 12, 31);
		pet.setBirthDate(endOfYear);
		assertThat(pet.getBirthDate()).isEqualTo(endOfYear);
		
		// Test with beginning of year date
		LocalDate beginningOfYear = LocalDate.of(2023, 1, 1);
		pet.setBirthDate(beginningOfYear);
		assertThat(pet.getBirthDate()).isEqualTo(beginningOfYear);
	}
}
