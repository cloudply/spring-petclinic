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
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for {@link OwnerRepository}.
 */
@DataJpaTest
class OwnerRepositoryTests {

	@Autowired
	private OwnerRepository owners;

	@Test
	void shouldFindOwnersByLastName() {
		// given
		String lastName = "Davis";
		Pageable pageable = PageRequest.of(0, 10);
		
		// when
		Page<Owner> result = this.owners.findByLastName(lastName, pageable);
		
		// then
		assertThat(result).isNotEmpty();
		assertThat(result.getContent().get(0).getLastName()).isEqualTo(lastName);
	}

	@Test
	void shouldFindOwnersByLastNameWithWildcard() {
		// given
		String lastName = "D%";
		Pageable pageable = PageRequest.of(0, 10);
		
		// when
		Page<Owner> result = this.owners.findByLastName(lastName, pageable);
		
		// then
		assertThat(result).isNotEmpty();
		assertThat(result.getContent().get(0).getLastName()).startsWith("D");
	}

	@Test
	void shouldFindNoOwnerForNonExistingLastName() {
		// given
		String lastName = "NonExistingLastName";
		Pageable pageable = PageRequest.of(0, 10);
		
		// when
		Page<Owner> result = this.owners.findByLastName(lastName, pageable);
		
		// then
		assertThat(result).isEmpty();
	}

	@Test
	void shouldFindOwnerById() {
		// given
		Integer id = 1;
		
		// when
		Owner owner = this.owners.findById(id);
		
		// then
		assertThat(owner).isNotNull();
		assertThat(owner.getId()).isEqualTo(id);
	}

	@Test
	void shouldReturnNullForNonExistingOwnerId() {
		// given
		Integer id = 999;
		
		// when
		Owner owner = this.owners.findById(id);
		
		// then
		assertThat(owner).isNull();
	}

	@Test
	void shouldFindAllPetTypes() {
		// when
		List<PetType> petTypes = this.owners.findPetTypes();
		
		// then
		assertThat(petTypes).isNotEmpty();
		assertThat(petTypes.size()).isGreaterThanOrEqualTo(6);
	}

	@Test
	@Transactional
	void shouldSaveOwner() {
		// given
		int initialCount = ((Collection<Owner>) this.owners.findAll(PageRequest.of(0, 100)).getContent()).size();
		
		Owner owner = new Owner();
		owner.setFirstName("John");
		owner.setLastName("Doe");
		owner.setAddress("123 Main St");
		owner.setCity("New York");
		owner.setTelephone("1234567890");
		
		// when
		this.owners.save(owner);
		
		// then
		assertThat(owner.getId()).isNotNull();
		assertThat(((Collection<Owner>) this.owners.findAll(PageRequest.of(0, 100)).getContent()).size())
			.isEqualTo(initialCount + 1);
	}

	@Test
	@Transactional
	void shouldUpdateExistingOwner() {
		// given
		Owner owner = this.owners.findById(1);
		String newLastName = "UpdatedLastName";
		
		// when
		owner.setLastName(newLastName);
		this.owners.save(owner);
		
		// then
		Owner updatedOwner = this.owners.findById(1);
		assertThat(updatedOwner.getLastName()).isEqualTo(newLastName);
	}
}
