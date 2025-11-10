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

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests for the {@link VetRepository} interface.
 */
@DataJpaTest
class VetRepositoryTests {

	@Autowired
	private VetRepository vetRepository;

	@Test
	void testFindAll() {
		Collection<Vet> vets = this.vetRepository.findAll();
		
		// The sample data has 6 vets
		assertThat(vets).isNotNull();
		assertThat(vets.size()).isEqualTo(6);
		
		// Verify that we have both vets with and without specialties
		boolean foundVetWithSpecialty = false;
		boolean foundVetWithoutSpecialty = false;
		
		for (Vet vet : vets) {
			if (vet.getNrOfSpecialties() > 0) {
				foundVetWithSpecialty = true;
			}
			else {
				foundVetWithoutSpecialty = true;
			}
		}
		
		assertThat(foundVetWithSpecialty).isTrue();
		assertThat(foundVetWithoutSpecialty).isTrue();
	}
	
	@Test
	void testFindAllWithPagination() {
		// Test first page (2 elements per page)
		Pageable firstPageWithTwoElements = PageRequest.of(0, 2);
		Page<Vet> firstPage = this.vetRepository.findAll(firstPageWithTwoElements);
		
		assertThat(firstPage).isNotNull();
		assertThat(firstPage.getContent().size()).isEqualTo(2);
		assertThat(firstPage.getTotalElements()).isEqualTo(6); // Total of 6 vets in sample data
		assertThat(firstPage.getTotalPages()).isEqualTo(3);    // With 2 per page, we should have 3 pages
		assertThat(firstPage.getNumber()).isEqualTo(0);        // We're on the first page (index 0)
		
		// Test second page
		Pageable secondPageWithTwoElements = PageRequest.of(1, 2);
		Page<Vet> secondPage = this.vetRepository.findAll(secondPageWithTwoElements);
		
		assertThat(secondPage).isNotNull();
		assertThat(secondPage.getContent().size()).isEqualTo(2);
		assertThat(secondPage.getNumber()).isEqualTo(1);       // We're on the second page (index 1)
		
		// Test with different page size
		Pageable pageWithThreeElements = PageRequest.of(0, 3);
		Page<Vet> largerPage = this.vetRepository.findAll(pageWithThreeElements);
		
		assertThat(largerPage).isNotNull();
		assertThat(largerPage.getContent().size()).isEqualTo(3);
		assertThat(largerPage.getTotalPages()).isEqualTo(2);   // With 3 per page, we should have 2 pages
	}
	
	@Test
	void testFindAllWithEmptyRepository() {
		// This test is more of a conceptual test since we can't easily clear the repository
		// In a real scenario, we might use @Sql to set up specific database states
		
		// Even with sample data, we can verify that pagination works correctly with empty results
		Pageable pageOutOfBounds = PageRequest.of(10, 10); // This page should be beyond our data
		Page<Vet> emptyPage = this.vetRepository.findAll(pageOutOfBounds);
		
		assertThat(emptyPage).isNotNull();
		assertThat(emptyPage.getContent()).isEmpty();
		assertThat(emptyPage.getTotalElements()).isEqualTo(6); // Still reports correct total
	}
}
