package org.springframework.samples.petclinic.vet;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Integration tests for {@link VetRepository}.
 */
@DataJpaTest
class VetRepositoryTests {

	@Autowired
	private VetRepository vets;

	@Test
	void shouldFindAllVets() {
		// when
		Collection<Vet> vetCollection = this.vets.findAll();
		
		// then
		assertThat(vetCollection).isNotEmpty();
		assertThat(vetCollection.size()).isGreaterThanOrEqualTo(6);
	}
	
	@Test
	void shouldFindAllVetsWithPagination() {
		// given
		Pageable pageable = PageRequest.of(0, 3);
		
		// when
		Page<Vet> vetPage = this.vets.findAll(pageable);
		
		// then
		assertThat(vetPage).isNotEmpty();
		assertThat(vetPage.getContent().size()).isEqualTo(3);
		assertThat(vetPage.getTotalElements()).isGreaterThanOrEqualTo(6);
	}
	
	@Test
	void shouldFindVetsWithSpecialties() {
		// when
		Collection<Vet> vetCollection = this.vets.findAll();
		
		// then
		boolean foundVetWithSpecialty = false;
		for (Vet vet : vetCollection) {
			if (vet.getNrOfSpecialties() > 0) {
				foundVetWithSpecialty = true;
				break;
			}
		}
		assertThat(foundVetWithSpecialty).isTrue();
	}
}
