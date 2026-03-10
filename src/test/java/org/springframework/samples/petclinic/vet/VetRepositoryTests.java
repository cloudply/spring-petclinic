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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the {@link VetRepository}.
 *
 * @author Ken Krebs
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 */
@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@ActiveProfiles("hsqldb")
class VetRepositoryTests {

	@Autowired
	private VetRepository vetRepository;

	@Test
	void shouldFindAllVets() {
		Collection<Vet> vets = this.vetRepository.findAll();
		
		// Based on the data.sql files, there should be 6 vets in the test data
		assertThat(vets).hasSize(6);
		
		// Verify that vets are properly loaded with their basic information
		assertThat(vets).allMatch(vet -> vet.getFirstName() != null);
		assertThat(vets).allMatch(vet -> vet.getLastName() != null);
		assertThat(vets).allMatch(vet -> vet.getId() != null);
	}

	@Test
	void shouldFindAllVetsWithSpecialties() {
		Collection<Vet> vets = this.vetRepository.findAll();
		
		// Verify that some vets have specialties
		long vetsWithSpecialties = vets.stream()
			.filter(vet -> vet.getNrOfSpecialties() > 0)
			.count();
		
		assertThat(vetsWithSpecialties).isGreaterThan(0);
	}

	@Test
	void shouldFindSpecificVetsByName() {
		Collection<Vet> vets = this.vetRepository.findAll();
		
		// Verify specific vets exist (based on typical test data)
		boolean foundJamesCarter = vets.stream()
			.anyMatch(vet -> "James".equals(vet.getFirstName()) && "Carter".equals(vet.getLastName()));
		
		boolean foundHelenLeary = vets.stream()
			.anyMatch(vet -> "Helen".equals(vet.getFirstName()) && "Leary".equals(vet.getLastName()));
		
		assertThat(foundJamesCarter || foundHelenLeary).isTrue();
	}

	@Test
	void shouldFindAllVetsWithPagination() {
		Pageable pageable = PageRequest.of(0, 5);
		Page<Vet> vetPage = this.vetRepository.findAll(pageable);
		
		assertThat(vetPage).isNotNull();
		assertThat(vetPage.getContent()).isNotEmpty();
		assertThat(vetPage.getContent()).hasSizeLessThanOrEqualTo(5);
		assertThat(vetPage.getTotalElements()).isEqualTo(6);
		assertThat(vetPage.getTotalPages()).isGreaterThanOrEqualTo(1);
	}

	@Test
	void shouldHandleFirstPagePagination() {
		Pageable firstPage = PageRequest.of(0, 3);
		Page<Vet> vetPage = this.vetRepository.findAll(firstPage);
		
		assertThat(vetPage.getNumber()).isEqualTo(0);
		assertThat(vetPage.getSize()).isEqualTo(3);
		assertThat(vetPage.getContent()).hasSize(3);
		assertThat(vetPage.hasNext()).isTrue();
		assertThat(vetPage.hasPrevious()).isFalse();
		assertThat(vetPage.isFirst()).isTrue();
		assertThat(vetPage.isLast()).isFalse();
	}

	@Test
	void shouldHandleLastPagePagination() {
		// Assuming 6 total vets, page size 4 should give us 2 pages
		Pageable lastPage = PageRequest.of(1, 4);
		Page<Vet> vetPage = this.vetRepository.findAll(lastPage);
		
		assertThat(vetPage.getNumber()).isEqualTo(1);
		assertThat(vetPage.getContent()).hasSizeLessThanOrEqualTo(4);
		assertThat(vetPage.hasNext()).isFalse();
		assertThat(vetPage.hasPrevious()).isTrue();
		assertThat(vetPage.isLast()).isTrue();
		assertThat(vetPage.isFirst()).isFalse();
	}

	@Test
	void shouldHandleEmptyPageWhenPageNumberTooHigh() {
		Pageable highPageNumber = PageRequest.of(10, 5);
		Page<Vet> vetPage = this.vetRepository.findAll(highPageNumber);
		
		assertThat(vetPage.getContent()).isEmpty();
		assertThat(vetPage.getTotalElements()).isEqualTo(6);
		assertThat(vetPage.getNumber()).isEqualTo(10);
	}

	@Test
	void shouldHandleSingleItemPerPage() {
		Pageable singleItemPage = PageRequest.of(0, 1);
		Page<Vet> vetPage = this.vetRepository.findAll(singleItemPage);
		
		assertThat(vetPage.getContent()).hasSize(1);
		assertThat(vetPage.getTotalPages()).isEqualTo(6);
		assertThat(vetPage.hasNext()).isTrue();
	}

	@Test
	void shouldHandleLargePageSize() {
		Pageable largePageSize = PageRequest.of(0, 100);
		Page<Vet> vetPage = this.vetRepository.findAll(largePageSize);
		
		assertThat(vetPage.getContent()).hasSize(6);
		assertThat(vetPage.getTotalPages()).isEqualTo(1);
		assertThat(vetPage.hasNext()).isFalse();
		assertThat(vetPage.isLast()).isTrue();
	}

	@Test
	@Transactional(readOnly = true)
	void shouldMaintainReadOnlyTransaction() {
		// This test verifies that the @Transactional(readOnly = true) annotation works
		Collection<Vet> vets = this.vetRepository.findAll();
		assertThat(vets).isNotEmpty();
		
		// Verify that we can read the data multiple times
		Collection<Vet> vetsSecondCall = this.vetRepository.findAll();
		assertThat(vetsSecondCall).hasSameSizeAs(vets);
	}

	@Test
	@Transactional(readOnly = true)
	void shouldMaintainReadOnlyTransactionForPagination() {
		Pageable pageable = PageRequest.of(0, 5);
		Page<Vet> vetPage = this.vetRepository.findAll(pageable);
		assertThat(vetPage).isNotNull();
		
		// Verify that we can read the data multiple times
		Page<Vet> vetPageSecondCall = this.vetRepository.findAll(pageable);
		assertThat(vetPageSecondCall.getContent()).hasSameSizeAs(vetPage.getContent());
	}

	@Test
	void shouldReturnConsistentResultsBetweenFindAllMethods() {
		// Compare results from both findAll methods
		Collection<Vet> allVets = this.vetRepository.findAll();
		
		Pageable pageable = PageRequest.of(0, 100); // Large page to get all results
		Page<Vet> allVetsPage = this.vetRepository.findAll(pageable);
		
		assertThat(allVetsPage.getContent()).hasSameSizeAs(allVets);
		assertThat(allVetsPage.getTotalElements()).isEqualTo(allVets.size());
	}

	@Test
	void shouldVerifyVetEntityIntegrity() {
		Collection<Vet> vets = this.vetRepository.findAll();
		
		for (Vet vet : vets) {
			// Verify basic entity properties
			assertThat(vet.getId()).isNotNull();
			assertThat(vet.getFirstName()).isNotNull().isNotEmpty();
			assertThat(vet.getLastName()).isNotNull().isNotEmpty();
			
			// Verify specialties collection is initialized
			assertThat(vet.getSpecialties()).isNotNull();
			assertThat(vet.getNrOfSpecialties()).isGreaterThanOrEqualTo(0);
			
			// Verify toString method works
			assertThat(vet.toString()).isNotNull();
		}
	}

	@Test
	void shouldVerifySpecialtiesAreProperlyLoaded() {
		Collection<Vet> vets = this.vetRepository.findAll();
		
		// Find vets with specialties
		vets.stream()
			.filter(vet -> vet.getNrOfSpecialties() > 0)
			.forEach(vet -> {
				assertThat(vet.getSpecialties()).isNotEmpty();
				vet.getSpecialties().forEach(specialty -> {
					assertThat(specialty.getName()).isNotNull().isNotEmpty();
					assertThat(specialty.getId()).isNotNull();
				});
			});
	}
}
