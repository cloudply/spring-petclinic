package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test class for {@link OwnerRepository}
 */
@DataJpaTest
class OwnerRepositoryTests {

	@Autowired
	private OwnerRepository owners;

	@Test
	void shouldFindOwnersByLastName() {
		Page<Owner> owners = this.owners.findByLastName("Davis", PageRequest.of(0, 10));
		assertThat(owners.getTotalElements()).isEqualTo(2);

		owners = this.owners.findByLastName("Daviss", PageRequest.of(0, 10));
		assertThat(owners.getTotalElements()).isEqualTo(0);
	}

	@Test
	void shouldFindSingleOwnerWithPet() {
		Owner owner = this.owners.findById(1);
		assertThat(owner.getLastName()).startsWith("Franklin");
		assertThat(owner.getPets().size()).isEqualTo(1);
		assertThat(owner.getPets().get(0).getType()).isNotNull();
		assertThat(owner.getPets().get(0).getType().getName()).isEqualTo("cat");
	}

	@Test
	@Transactional
	void shouldInsertOwner() {
		Page<Owner> owners = this.owners.findByLastName("Schultz", PageRequest.of(0, 10));
		int found = (int) owners.getTotalElements();

		Owner owner = new Owner();
		owner.setFirstName("Sam");
		owner.setLastName("Schultz");
		owner.setAddress("4 Evans Street");
		owner.setCity("Wollongong");
		owner.setTelephone("4444444444");
		this.owners.save(owner);
		assertThat(owner.getId()).isNotNull();

		owners = this.owners.findByLastName("Schultz", PageRequest.of(0, 10));
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
		List<PetType> petTypes = this.owners.findPetTypes();
		assertThat(petTypes.size()).isGreaterThan(0);
		PetType petType = petTypes.get(0);
		assertThat(petType.getName()).isNotEmpty();
	}

	@Test
	void shouldFindAllOwners() {
		Page<Owner> owners = this.owners.findAll(PageRequest.of(0, 10));
		assertThat(owners.getTotalElements()).isGreaterThan(0);
	}
}
