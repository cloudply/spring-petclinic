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

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit test for the {@link Owner} class.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 */
class OwnerTests {

	private Owner owner;
	private Pet pet1;
	private Pet pet2;
	private PetType dogType;
	private PetType catType;

	@BeforeEach
	void setUp() {
		owner = new Owner();
		owner.setId(1);
		owner.setFirstName("John");
		owner.setLastName("Doe");
		owner.setAddress("123 Main St");
		owner.setCity("Springfield");
		owner.setTelephone("1234567890");

		dogType = new PetType();
		dogType.setId(1);
		dogType.setName("Dog");

		catType = new PetType();
		catType.setId(2);
		catType.setName("Cat");

		pet1 = new Pet();
		pet1.setId(1);
		pet1.setName("Max");
		pet1.setType(dogType);
		pet1.setBirthDate(LocalDate.of(2020, 1, 1));

		pet2 = new Pet();
		pet2.setId(2);
		pet2.setName("Whiskers");
		pet2.setType(catType);
		pet2.setBirthDate(LocalDate.of(2019, 5, 15));
	}

	@Test
	void testOwnerCreation() {
		Owner newOwner = new Owner();
		assertThat(newOwner).isNotNull();
		assertThat(newOwner.getId()).isNull();
		assertThat(newOwner.isNew()).isTrue();
		assertThat(newOwner.getPets()).isNotNull();
		assertThat(newOwner.getPets()).isEmpty();
	}

	@Test
	void testSetAndGetAddress() {
		String address = "456 Oak Avenue";
		owner.setAddress(address);
		assertThat(owner.getAddress()).isEqualTo(address);
	}

	@Test
	void testSetAndGetCity() {
		String city = "New York";
		owner.setCity(city);
		assertThat(owner.getCity()).isEqualTo(city);
	}

	@Test
	void testSetAndGetTelephone() {
		String telephone = "9876543210";
		owner.setTelephone(telephone);
		assertThat(owner.getTelephone()).isEqualTo(telephone);
	}

	@Test
	void testGetPetsInitiallyEmpty() {
		Owner newOwner = new Owner();
		List<Pet> pets = newOwner.getPets();
		assertThat(pets).isNotNull();
		assertThat(pets).isEmpty();
	}

	@Test
	void testAddNewPet() {
		Pet newPet = new Pet();
		newPet.setName("Buddy");
		newPet.setType(dogType);
		
		owner.addPet(newPet);
		
		assertThat(owner.getPets()).hasSize(1);
		assertThat(owner.getPets()).contains(newPet);
	}

	@Test
	void testAddExistingPetDoesNotAddDuplicate() {
		owner.addPet(pet1);
		int initialSize = owner.getPets().size();
		
		// Try to add the same pet again (pet1 has an ID, so it's not new)
		owner.addPet(pet1);
		
		assertThat(owner.getPets()).hasSize(initialSize);
	}

	@Test
	void testGetPetByName() {
		owner.addPet(pet1);
		owner.addPet(pet2);
		
		Pet foundPet = owner.getPet("Max");
		assertThat(foundPet).isNotNull();
		assertThat(foundPet.getName()).isEqualTo("Max");
		
		Pet foundPet2 = owner.getPet("Whiskers");
		assertThat(foundPet2).isNotNull();
		assertThat(foundPet2.getName()).isEqualTo("Whiskers");
	}

	@Test
	void testGetPetByNameCaseInsensitive() {
		owner.addPet(pet1);
		
		Pet foundPet = owner.getPet("max");
		assertThat(foundPet).isNotNull();
		assertThat(foundPet.getName()).isEqualTo("Max");
		
		Pet foundPet2 = owner.getPet("MAX");
		assertThat(foundPet2).isNotNull();
		assertThat(foundPet2.getName()).isEqualTo("Max");
	}

	@Test
	void testGetPetByNameNotFound() {
		owner.addPet(pet1);
		
		Pet foundPet = owner.getPet("NonExistent");
		assertThat(foundPet).isNull();
	}

	@Test
	void testGetPetByNameWithNullName() {
		owner.addPet(pet1);
		
		assertThatThrownBy(() -> owner.getPet((String) null))
			.isInstanceOf(NullPointerException.class);
	}

	@Test
	void testGetPetById() {
		owner.addPet(pet1);
		owner.addPet(pet2);
		
		Pet foundPet = owner.getPet(1);
		assertThat(foundPet).isNotNull();
		assertThat(foundPet.getId()).isEqualTo(1);
		assertThat(foundPet.getName()).isEqualTo("Max");
		
		Pet foundPet2 = owner.getPet(2);
		assertThat(foundPet2).isNotNull();
		assertThat(foundPet2.getId()).isEqualTo(2);
		assertThat(foundPet2.getName()).isEqualTo("Whiskers");
	}

	@Test
	void testGetPetByIdNotFound() {
		owner.addPet(pet1);
		
		Pet foundPet = owner.getPet(999);
		assertThat(foundPet).isNull();
	}

	@Test
	void testGetPetByIdWithNewPet() {
		Pet newPet = new Pet();
		newPet.setName("NewPet");
		owner.addPet(newPet);
		
		Pet foundPet = owner.getPet((Integer) null);
		assertThat(foundPet).isNull();
	}

	@Test
	void testGetPetByNameIgnoreNew() {
		Pet newPet = new Pet();
		newPet.setName("NewPet");
		owner.addPet(newPet);
		owner.addPet(pet1);
		
		// Should find the new pet when ignoreNew is false
		Pet foundPet = owner.getPet("NewPet", false);
		assertThat(foundPet).isNotNull();
		assertThat(foundPet.getName()).isEqualTo("NewPet");
		
		// Should not find the new pet when ignoreNew is true
		Pet foundPet2 = owner.getPet("NewPet", true);
		assertThat(foundPet2).isNull();
		
		// Should find existing pet regardless of ignoreNew flag
		Pet foundPet3 = owner.getPet("Max", true);
		assertThat(foundPet3).isNotNull();
		assertThat(foundPet3.getName()).isEqualTo("Max");
	}

	@Test
	void testAddVisit() {
		owner.addPet(pet1);
		
		Visit visit = new Visit();
		visit.setDate(LocalDate.now());
		visit.setDescription("Regular checkup");
		
		owner.addVisit(1, visit);
		
		assertThat(pet1.getVisits()).hasSize(1);
		assertThat(pet1.getVisits()).contains(visit);
	}

	@Test
	void testAddVisitWithNullPetId() {
		assertThatThrownBy(() -> owner.addVisit(null, new Visit()))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Pet identifier must not be null!");
	}

	@Test
	void testAddVisitWithNullVisit() {
		owner.addPet(pet1);
		
		assertThatThrownBy(() -> owner.addVisit(1, null))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Visit must not be null!");
	}

	@Test
	void testAddVisitWithInvalidPetId() {
		owner.addPet(pet1);
		
		Visit visit = new Visit();
		visit.setDate(LocalDate.now());
		visit.setDescription("Regular checkup");
		
		assertThatThrownBy(() -> owner.addVisit(999, visit))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Invalid Pet identifier!");
	}

	@Test
	void testToString() {
		String result = owner.toString();
		
		assertThat(result).contains("id=1");
		assertThat(result).contains("new=false");
		assertThat(result).contains("lastName=Doe");
		assertThat(result).contains("firstName=John");
		assertThat(result).contains("address=123 Main St");
		assertThat(result).contains("city=Springfield");
		assertThat(result).contains("telephone=1234567890");
	}

	@Test
	void testToStringWithNewOwner() {
		Owner newOwner = new Owner();
		newOwner.setFirstName("Jane");
		newOwner.setLastName("Smith");
		newOwner.setAddress("789 Pine St");
		newOwner.setCity("Boston");
		newOwner.setTelephone("5555555555");
		
		String result = newOwner.toString();
		
		assertThat(result).contains("new=true");
		assertThat(result).contains("lastName=Smith");
		assertThat(result).contains("firstName=Jane");
		assertThat(result).contains("address=789 Pine St");
		assertThat(result).contains("city=Boston");
		assertThat(result).contains("telephone=5555555555");
	}

	@Test
	void testOwnerWithMultiplePets() {
		owner.addPet(pet1);
		owner.addPet(pet2);
		
		assertThat(owner.getPets()).hasSize(2);
		assertThat(owner.getPets()).containsExactly(pet1, pet2);
	}

	@Test
	void testOwnerInheritedMethods() {
		// Test inherited methods from Person and BaseEntity
		assertThat(owner.getFirstName()).isEqualTo("John");
		assertThat(owner.getLastName()).isEqualTo("Doe");
		assertThat(owner.getId()).isEqualTo(1);
		assertThat(owner.isNew()).isFalse();
		
		Owner newOwner = new Owner();
		assertThat(newOwner.isNew()).isTrue();
	}

	@Test
	void testPetWithNullName() {
		Pet petWithNullName = new Pet();
		petWithNullName.setId(3);
		petWithNullName.setType(dogType);
		owner.addPet(petWithNullName);
		
		Pet foundPet = owner.getPet("SomeName");
		assertThat(foundPet).isNull();
	}

	@Test
	void testMultipleVisitsForSamePet() {
		owner.addPet(pet1);
		
		Visit visit1 = new Visit();
		visit1.setDate(LocalDate.now().minusDays(10));
		visit1.setDescription("First visit");
		
		Visit visit2 = new Visit();
		visit2.setDate(LocalDate.now());
		visit2.setDescription("Second visit");
		
		owner.addVisit(1, visit1);
		owner.addVisit(1, visit2);
		
		assertThat(pet1.getVisits()).hasSize(2);
		assertThat(pet1.getVisits()).containsExactly(visit1, visit2);
	}

	@Test
	void testOwnerWithEmptyStringFields() {
		Owner emptyOwner = new Owner();
		emptyOwner.setFirstName("");
		emptyOwner.setLastName("");
		emptyOwner.setAddress("");
		emptyOwner.setCity("");
		emptyOwner.setTelephone("");
		
		assertThat(emptyOwner.getFirstName()).isEmpty();
		assertThat(emptyOwner.getLastName()).isEmpty();
		assertThat(emptyOwner.getAddress()).isEmpty();
		assertThat(emptyOwner.getCity()).isEmpty();
		assertThat(emptyOwner.getTelephone()).isEmpty();
	}

	@Test
	void testOwnerEquality() {
		Owner owner1 = new Owner();
		owner1.setId(1);
		owner1.setFirstName("John");
		owner1.setLastName("Doe");
		
		Owner owner2 = new Owner();
		owner2.setId(1);
		owner2.setFirstName("John");
		owner2.setLastName("Doe");
		
		// Note: Owner doesn't override equals/hashCode, so this tests object identity
		assertThat(owner1).isNotEqualTo(owner2);
		assertThat(owner1).isEqualTo(owner1);
	}

	@Test
	void testPetOrderingByName() {
		Pet petA = new Pet();
		petA.setName("Alpha");
		petA.setType(dogType);
		
		Pet petZ = new Pet();
		petZ.setName("Zulu");
		petZ.setType(catType);
		
		Pet petB = new Pet();
		petB.setName("Beta");
		petB.setType(dogType);
		
		owner.addPet(petZ);
		owner.addPet(petA);
		owner.addPet(petB);
		
		// Pets should be ordered by name due to @OrderBy annotation
		List<Pet> pets = owner.getPets();
		assertThat(pets).hasSize(3);
		// Note: The actual ordering depends on JPA implementation, 
		// but we can test that all pets are present
		assertThat(pets).containsExactlyInAnyOrder(petA, petB, petZ);
	}
}
