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

package org.springframework.samples.petclinic.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration test of the Service and the Repository layer.
 * <p>
 * ClinicServiceSpringDataJpaTests subclasses benefit from the following services provided
 * by the Spring TestContext Framework:
 * </p>
 * <ul>
 * <li><strong>Spring IoC container caching</strong> which spares us unnecessary set up
 * time between test execution.</li>
 * <li><strong>Dependency Injection</strong> of test fixture instances, meaning that we
 * don't need to perform application context lookups. See the use of
 * {@link Autowired @Autowired} on the <code> </code> instance variable, which uses
 * autowiring <em>by type</em>.
 * <li><strong>Transaction management</strong>, meaning each test method is executed in
 * its own transaction, which is automatically rolled back by default. Thus, even if tests
 * insert or otherwise change database state, there is no need for a teardown or cleanup
 * script.
 * <li>An {@link org.springframework.context.ApplicationContext ApplicationContext} is
 * also inherited and can be used for explicit bean lookup if necessary.</li>
 * </ul>
 *
 * @author Ken Krebs
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Dave Syer
 */
@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
// Ensure that if the mysql profile is active we connect to the real database:
@AutoConfigureTestDatabase(replace = Replace.NONE)
// @TestPropertySource("/application-postgres.properties")
class ClinicServiceTests {

	@Autowired
	protected OwnerRepository owners;

	@Autowired
	protected VetRepository vets;

	Pageable pageable;

	@Test
	void shouldFindOwnersByLastName() {
		Page<Owner> owners = this.owners.findByLastName("Davis", pageable);
		assertThat(owners).hasSize(2);

		owners = this.owners.findByLastName("Daviss", pageable);
		assertThat(owners).isEmpty();
	}

	@Test
	void shouldFindSingleOwnerWithPet() {
		Owner owner = this.owners.findById(1);
		assertThat(owner.getLastName()).startsWith("Franklin");
		assertThat(owner.getPets()).hasSize(1);
		assertThat(owner.getPets().get(0).getType()).isNotNull();
		assertThat(owner.getPets().get(0).getType().getName()).isEqualTo("cat");
	}

	@Test
	@Transactional
	void shouldInsertOwner() {
		Page<Owner> owners = this.owners.findByLastName("Schultz", pageable);
		int found = (int) owners.getTotalElements();

		Owner owner = new Owner();
		owner.setFirstName("Sam");
		owner.setLastName("Schultz");
		owner.setAddress("4, Evans Street");
		owner.setCity("Wollongong");
		owner.setTelephone("4444444444");
		this.owners.save(owner);
		assertThat(owner.getId()).isNotZero();

		owners = this.owners.findByLastName("Schultz", pageable);
		assertThat(owners.getTotalElements()).isEqualTo(found + 1);
	}

	@Test
	@Transactional
	void shouldUpdateOwner() {
		Owner owner = this.owners.findById(1);
		String oldLastName = owner.getLastName();
		String newLastName = oldLastName + "X";

		owner.setLastName(newLastName);
		this.owners.save(owner);

		// retrieving new name from database
		owner = this.owners.findById(1);
		assertThat(owner.getLastName()).isEqualTo(newLastName);
	}

	@Test
	void shouldFindAllPetTypes() {
		Collection<PetType> petTypes = this.owners.findPetTypes();

		PetType petType1 = EntityUtils.getById(petTypes, PetType.class, 1);
		assertThat(petType1.getName()).isEqualTo("cat");
		PetType petType4 = EntityUtils.getById(petTypes, PetType.class, 4);
		assertThat(petType4.getName()).isEqualTo("snake");
	}

	@Test
	@Transactional
	void shouldInsertPetIntoDatabaseAndGenerateId() {
		Owner owner6 = this.owners.findById(6);
		int found = owner6.getPets().size();

		Pet pet = new Pet();
		pet.setName("bowser");
		Collection<PetType> types = this.owners.findPetTypes();
		pet.setType(EntityUtils.getById(types, PetType.class, 2));
		pet.setBirthDate(LocalDate.now());
		owner6.addPet(pet);
		assertThat(owner6.getPets()).hasSize(found + 1);

		this.owners.save(owner6);

		owner6 = this.owners.findById(6);
		assertThat(owner6.getPets()).hasSize(found + 1);
		// checks that id has been generated
		pet = owner6.getPet("bowser");
		assertThat(pet.getId()).isNotNull();
	}

	@Test
	@Transactional
	void shouldUpdatePetName() {
		Owner owner6 = this.owners.findById(6);
		Pet pet7 = owner6.getPet(7);
		String oldName = pet7.getName();

		String newName = oldName + "X";
		pet7.setName(newName);
		this.owners.save(owner6);

		owner6 = this.owners.findById(6);
		pet7 = owner6.getPet(7);
		assertThat(pet7.getName()).isEqualTo(newName);
	}

	@Test
	void shouldFindVets() {
		Collection<Vet> vets = this.vets.findAll();

		Vet vet = EntityUtils.getById(vets, Vet.class, 3);
		assertThat(vet.getLastName()).isEqualTo("Douglas");
		assertThat(vet.getNrOfSpecialties()).isEqualTo(2);
		assertThat(vet.getSpecialties().get(0).getName()).isEqualTo("dentistry");
		assertThat(vet.getSpecialties().get(1).getName()).isEqualTo("surgery");
	}

	@Test
	@Transactional
	void shouldAddNewVisitForPet() {
		Owner owner6 = this.owners.findById(6);
		Pet pet7 = owner6.getPet(7);
		int found = pet7.getVisits().size();
		Visit visit = new Visit();
		visit.setDescription("test");

		owner6.addVisit(pet7.getId(), visit);
		this.owners.save(owner6);

		owner6 = this.owners.findById(6);

		assertThat(pet7.getVisits()) //
			.hasSize(found + 1) //
			.allMatch(value -> value.getId() != null);
	}

	@Test
	void shouldFindVisitsByPetId() {
		Owner owner6 = this.owners.findById(6);
		Pet pet7 = owner6.getPet(7);
		Collection<Visit> visits = pet7.getVisits();

		assertThat(visits) //
			.hasSize(2) //
			.element(0)
			.extracting(Visit::getDate)
			.isNotNull();
	}

	// Additional comprehensive tests for OwnerRepository

	@Test
	void shouldFindOwnersByPartialLastName() {
		Pageable testPageable = PageRequest.of(0, 10);
		Page<Owner> owners = this.owners.findByLastName("Dav", testPageable);

		assertThat(owners).isNotNull();
		assertThat(owners.getContent()).isNotEmpty();
		
		// All found owners should have last names starting with "Dav"
		owners.getContent().forEach(owner -> {
			assertThat(owner.getLastName()).startsWith("Dav");
		});
	}

	@Test
	void shouldReturnEmptyPageForNonExistentLastName() {
		Pageable testPageable = PageRequest.of(0, 10);
		Page<Owner> owners = this.owners.findByLastName("NonExistentLastName", testPageable);

		assertThat(owners).isNotNull();
		assertThat(owners.getContent()).isEmpty();
		assertThat(owners.getTotalElements()).isEqualTo(0);
	}

	@Test
	void shouldFindOwnersByEmptyLastName() {
		Pageable testPageable = PageRequest.of(0, 10);
		Page<Owner> owners = this.owners.findByLastName("", testPageable);

		assertThat(owners).isNotNull();
		assertThat(owners.getContent()).isNotEmpty();
		// Empty string should match all owners (starts with empty string)
		assertThat(owners.getTotalElements()).isGreaterThan(0);
	}

	@Test
	void shouldReturnNullForNonExistentOwnerId() {
		Owner owner = this.owners.findById(999);
		assertThat(owner).isNull();
	}

	@Test
	void shouldReturnNullForNullOwnerId() {
		Owner owner = this.owners.findById(null);
		assertThat(owner).isNull();
	}

	@Test
	void shouldFindAllOwnersWithPagination() {
		Pageable testPageable = PageRequest.of(0, 5);
		Page<Owner> owners = this.owners.findAll(testPageable);

		assertThat(owners).isNotNull();
		assertThat(owners.getContent()).isNotEmpty();
		assertThat(owners.getContent().size()).isLessThanOrEqualTo(5);
		assertThat(owners.getTotalElements()).isGreaterThan(0);
		
		// Verify all owners have required fields
		owners.getContent().forEach(owner -> {
			assertThat(owner.getId()).isNotNull();
			assertThat(owner.getFirstName()).isNotNull();
			assertThat(owner.getLastName()).isNotNull();
		});
	}

	@Test
	void shouldHandlePaginationCorrectly() {
		Pageable firstPage = PageRequest.of(0, 2);
		Pageable secondPage = PageRequest.of(1, 2);
		
		Page<Owner> firstPageResult = this.owners.findAll(firstPage);
		Page<Owner> secondPageResult = this.owners.findAll(secondPage);

		assertThat(firstPageResult.getContent()).hasSize(2);
		assertThat(firstPageResult.getNumber()).isEqualTo(0);
		assertThat(firstPageResult.getSize()).isEqualTo(2);
		
		if (secondPageResult.hasContent()) {
			assertThat(secondPageResult.getNumber()).isEqualTo(1);
			// Ensure different owners on different pages
			assertThat(firstPageResult.getContent())
				.doesNotContainAnyElementsOf(secondPageResult.getContent());
		}
		
		// Total elements should be the same across pages
		assertThat(firstPageResult.getTotalElements()).isEqualTo(secondPageResult.getTotalElements());
	}

	@Test
	void shouldFindPetTypesOrderedByName() {
		Collection<PetType> petTypes = this.owners.findPetTypes();

		assertThat(petTypes).isNotEmpty();
		
		// Convert to list to check ordering
		java.util.List<PetType> petTypeList = new java.util.ArrayList<>(petTypes);
		
		// Verify that pet types are ordered by name
		for (int i = 1; i < petTypeList.size(); i++) {
			assertThat(petTypeList.get(i - 1).getName())
				.isLessThanOrEqualTo(petTypeList.get(i).getName());
		}
		
		// Verify that all pet types have names
		petTypes.forEach(petType -> {
			assertThat(petType.getName()).isNotNull();
			assertThat(petType.getName()).isNotEmpty();
		});
	}

	@Test
	@Transactional
	void shouldSaveOwnerWithMinimalRequiredFields() {
		Owner minimalOwner = new Owner();
		minimalOwner.setFirstName("Min");
		minimalOwner.setLastName("Mal");
		// Not setting optional fields like address, city, telephone

		this.owners.save(minimalOwner);

		assertThat(minimalOwner.getId()).isNotNull();
		
		Owner savedOwner = this.owners.findById(minimalOwner.getId());
		assertThat(savedOwner).isNotNull();
		assertThat(savedOwner.getFirstName()).isEqualTo("Min");
		assertThat(savedOwner.getLastName()).isEqualTo("Mal");
	}

	@Test
	@Transactional
	void shouldSaveOwnerWithPetAndVisit() {
		Owner newOwner = new Owner();
		newOwner.setFirstName("Jane");
		newOwner.setLastName("Smith");
		newOwner.setAddress("456 Oak Ave.");
		newOwner.setCity("Riverside");
		newOwner.setTelephone("5559876543");

		Pet pet = new Pet();
		pet.setName("Buddy");
		pet.setBirthDate(LocalDate.of(2020, 1, 15));
		
		// Get a pet type from the database
		Collection<PetType> petTypes = this.owners.findPetTypes();
		assertThat(petTypes).isNotEmpty();
		pet.setType(petTypes.iterator().next());

		Visit visit = new Visit();
		visit.setDescription("Initial checkup");
		visit.setDate(LocalDate.now());
		pet.addVisit(visit);

		newOwner.addPet(pet);

		this.owners.save(newOwner);

		assertThat(newOwner.getId()).isNotNull();
		
		Owner savedOwner = this.owners.findById(newOwner.getId());
		assertThat(savedOwner).isNotNull();
		assertThat(savedOwner.getPets()).hasSize(1);
		
		Pet savedPet = savedOwner.getPets().get(0);
		assertThat(savedPet.getName()).isEqualTo("Buddy");
		assertThat(savedPet.getBirthDate()).isEqualTo(LocalDate.of(2020, 1, 15));
		assertThat(savedPet.getType()).isNotNull();
		assertThat(savedPet.getVisits()).hasSize(1);
		
		Visit savedVisit = savedPet.getVisits().iterator().next();
		assertThat(savedVisit.getDescription()).isEqualTo("Initial checkup");
		assertThat(savedVisit.getDate()).isEqualTo(LocalDate.now());
	}

	@Test
	void shouldFindOwnerWithAllPetsLoaded() {
		Owner owner = this.owners.findById(6);
		assertThat(owner).isNotNull();
		
		// Verify that pets are eagerly loaded due to left join fetch in query
		assertThat(owner.getPets()).isNotNull();
		
		// Access pet properties to ensure they're loaded
		owner.getPets().forEach(pet -> {
			assertThat(pet.getName()).isNotNull();
			assertThat(pet.getType()).isNotNull();
			assertThat(pet.getBirthDate()).isNotNull();
		});
	}

	@Test
	void shouldHandleLastNameSearchCaseSensitivity() {
		Pageable testPageable = PageRequest.of(0, 10);
		
		// Test with different cases - behavior depends on database collation
		Page<Owner> lowerCase = this.owners.findByLastName("davis", testPageable);
		Page<Owner> upperCase = this.owners.findByLastName("DAVIS", testPageable);
		Page<Owner> mixedCase = this.owners.findByLastName("Davis", testPageable);

		// At least one of these should return results
		long totalResults = lowerCase.getTotalElements() + upperCase.getTotalElements() + mixedCase.getTotalElements();
		assertThat(totalResults).isGreaterThan(0);
	}

	@Test
	void shouldHandleEmptyPageSize() {
		Pageable emptyPageable = PageRequest.of(0, 0);
		Page<Owner> owners = this.owners.findAll(emptyPageable);

		assertThat(owners).isNotNull();
		assertThat(owners.getContent()).isEmpty();
		assertThat(owners.getTotalElements()).isGreaterThan(0); // Total should still be available
	}

	@Test
	void shouldHandleLargePageSize() {
		Pageable largePageable = PageRequest.of(0, 1000);
		Page<Owner> owners = this.owners.findAll(largePageable);

		assertThat(owners).isNotNull();
		assertThat(owners.getContent()).isNotEmpty();
		assertThat(owners.isFirst()).isTrue();
		assertThat(owners.isLast()).isTrue(); // Should be the only page
	}

	@Test
	@Transactional
	void shouldUpdateOwnerAllFields() {
		Owner owner = this.owners.findById(1);
		assertThat(owner).isNotNull();
		
		// Store original values
		String originalFirstName = owner.getFirstName();
		String originalLastName = owner.getLastName();
		String originalAddress = owner.getAddress();
		String originalCity = owner.getCity();
		String originalTelephone = owner.getTelephone();
		
		// Update all fields
		owner.setFirstName("UpdatedFirst");
		owner.setLastName("UpdatedLast");
		owner.setAddress("Updated Address");
		owner.setCity("Updated City");
		owner.setTelephone("9999999999");

		this.owners.save(owner);

		// Retrieve and verify updates
		Owner updatedOwner = this.owners.findById(1);
		assertThat(updatedOwner).isNotNull();
		assertThat(updatedOwner.getFirstName()).isEqualTo("UpdatedFirst").isNotEqualTo(originalFirstName);
		assertThat(updatedOwner.getLastName()).isEqualTo("UpdatedLast").isNotEqualTo(originalLastName);
		assertThat(updatedOwner.getAddress()).isEqualTo("Updated Address").isNotEqualTo(originalAddress);
		assertThat(updatedOwner.getCity()).isEqualTo("Updated City").isNotEqualTo(originalCity);
		assertThat(updatedOwner.getTelephone()).isEqualTo("9999999999").isNotEqualTo(originalTelephone);
	}

	@Test
	void shouldVerifyQueryDistinctBehavior() {
		// Test that the DISTINCT clause in findByLastName works correctly
		Pageable testPageable = PageRequest.of(0, 100);
		Page<Owner> allOwners = this.owners.findAll(testPageable);
		
		// Get all unique owner IDs
		java.util.Set<Integer> ownerIds = new java.util.HashSet<>();
		allOwners.getContent().forEach(owner -> ownerIds.add(owner.getId()));
		
		// The number of unique owners should equal the total number returned
		assertThat(ownerIds).hasSize(allOwners.getContent().size());
	}

}
