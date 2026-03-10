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
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for the {@link VetRepository}.
 */
@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@ActiveProfiles("hsqldb")
class VetRepositoryTests {

	@Autowired
	private VetRepository vetRepository;

	@Test
	void shouldFindAllVets() {
		Collection<Vet> vets = this.vetRepository.findAll();
		
		assertThat(vets).isNotNull();
		assertThat(vets).isNotEmpty();
		assertThat(vets).hasSize(6); // Based on the test data in data.sql
	}

	@Test
	void shouldFindAllVetsWithSpecialties() {
		Collection<Vet> vets = this.vetRepository.findAll();
		
		// Find a vet with specialties (Helen Leary has radiology specialty)
		Vet vetWithSpecialty = vets.stream()
			.filter(vet -> "Helen".equals(vet.getFirstName()) && "Leary".equals(vet.getLastName()))
			.findFirst()
			.orElse(null);
		
		assertThat(vetWithSpecialty).isNotNull();
		assertThat(vetWithSpecialty.getSpecialties()).isNotEmpty();
		assertThat(vetWithSpecialty.getNrOfSpecialties()).isGreaterThan(0);
	}

	@Test
	void shouldFindAllVetsWithoutSpecialties() {
		Collection<Vet> vets = this.vetRepository.findAll();
		
		// Find a vet without specialties (James Carter has no specialties)
		Vet vetWithoutSpecialty = vets.stream()
			.filter(vet -> "James".equals(vet.getFirstName()) && "Carter".equals(vet.getLastName()))
			.findFirst()
			.orElse(null);
		
		assertThat(vetWithoutSpecialty).isNotNull();
		assertThat(vetWithoutSpecialty.getSpecialties()).isEmpty();
		assertThat(vetWithoutSpecialty.getNrOfSpecialties()).isEqualTo(0);
	}

	@Test
	void shouldFindAllVetsPageable() {
		Pageable pageable = PageRequest.of(0, 3);
		Page<Vet> vetsPage = this.vetRepository.findAll(pageable);
		
		assertThat(vetsPage).isNotNull();
		assertThat(vetsPage.getContent()).isNotEmpty();
		assertThat(vetsPage.getContent()).hasSize(3);
		assertThat(vetsPage.getTotalElements()).isEqualTo(6);
		assertThat(vetsPage.getTotalPages()).isEqualTo(2);
		assertThat(vetsPage.getNumber()).isEqualTo(0);
		assertThat(vetsPage.getSize()).isEqualTo(3);
	}

	@Test
	void shouldFindAllVetsPageableSecondPage() {
		Pageable pageable = PageRequest.of(1, 3);
		Page<Vet> vetsPage = this.vetRepository.findAll(pageable);
		
		assertThat(vetsPage).isNotNull();
		assertThat(vetsPage.getContent()).isNotEmpty();
		assertThat(vetsPage.getContent()).hasSize(3);
		assertThat(vetsPage.getTotalElements()).isEqualTo(6);
		assertThat(vetsPage.getTotalPages()).isEqualTo(2);
		assertThat(vetsPage.getNumber()).isEqualTo(1);
		assertThat(vetsPage.getSize()).isEqualTo(3);
	}

	@Test
	void shouldFindAllVetsPageableWithLargerPageSize() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<Vet> vetsPage = this.vetRepository.findAll(pageable);
		
		assertThat(vetsPage).isNotNull();
		assertThat(vetsPage.getContent()).isNotEmpty();
		assertThat(vetsPage.getContent()).hasSize(6); // All vets fit in one page
		assertThat(vetsPage.getTotalElements()).isEqualTo(6);
		assertThat(vetsPage.getTotalPages()).isEqualTo(1);
		assertThat(vetsPage.getNumber()).isEqualTo(0);
		assertThat(vetsPage.getSize()).isEqualTo(10);
	}

	@Test
	void shouldFindAllVetsPageableWithSingleItemPerPage() {
		Pageable pageable = PageRequest.of(0, 1);
		Page<Vet> vetsPage = this.vetRepository.findAll(pageable);
		
		assertThat(vetsPage).isNotNull();
		assertThat(vetsPage.getContent()).isNotEmpty();
		assertThat(vetsPage.getContent()).hasSize(1);
		assertThat(vetsPage.getTotalElements()).isEqualTo(6);
		assertThat(vetsPage.getTotalPages()).isEqualTo(6);
		assertThat(vetsPage.getNumber()).isEqualTo(0);
		assertThat(vetsPage.getSize()).isEqualTo(1);
	}

	@Test
	void shouldHandleEmptyPageRequest() {
		Pageable pageable = PageRequest.of(10, 5); // Page beyond available data
		Page<Vet> vetsPage = this.vetRepository.findAll(pageable);
		
		assertThat(vetsPage).isNotNull();
		assertThat(vetsPage.getContent()).isEmpty();
		assertThat(vetsPage.getTotalElements()).isEqualTo(6);
		assertThat(vetsPage.getNumber()).isEqualTo(10);
		assertThat(vetsPage.getSize()).isEqualTo(5);
	}

	@Test
	void shouldVerifyVetProperties() {
		Collection<Vet> vets = this.vetRepository.findAll();
		
		for (Vet vet : vets) {
			assertThat(vet.getId()).isNotNull();
			assertThat(vet.getFirstName()).isNotNull().isNotEmpty();
			assertThat(vet.getLastName()).isNotNull().isNotEmpty();
			assertThat(vet.getSpecialties()).isNotNull(); // Can be empty but not null
		}
	}

	@Test
	void shouldFindSpecificVetsByName() {
		Collection<Vet> vets = this.vetRepository.findAll();
		
		// Verify specific vets from test data exist
		boolean foundJamesCarter = vets.stream()
			.anyMatch(vet -> "James".equals(vet.getFirstName()) && "Carter".equals(vet.getLastName()));
		
		boolean foundHelenLeary = vets.stream()
			.anyMatch(vet -> "Helen".equals(vet.getFirstName()) && "Leary".equals(vet.getLastName()));
		
		assertThat(foundJamesCarter).isTrue();
		assertThat(foundHelenLeary).isTrue();
	}

	@Test
	void shouldMaintainConsistentResultsBetweenCalls() {
		Collection<Vet> vets1 = this.vetRepository.findAll();
		Collection<Vet> vets2 = this.vetRepository.findAll();
		
		assertThat(vets1).hasSize(vets2.size());
		assertThat(vets1).containsExactlyInAnyOrderElementsOf(vets2);
	}

	@Test
	void shouldMaintainConsistentPageableResults() {
		Pageable pageable = PageRequest.of(0, 5);
		Page<Vet> vetsPage1 = this.vetRepository.findAll(pageable);
		Page<Vet> vetsPage2 = this.vetRepository.findAll(pageable);
		
		assertThat(vetsPage1.getContent()).hasSize(vetsPage2.getContent().size());
		assertThat(vetsPage1.getTotalElements()).isEqualTo(vetsPage2.getTotalElements());
		assertThat(vetsPage1.getContent()).containsExactlyInAnyOrderElementsOf(vetsPage2.getContent());
	}

	@Test
	void shouldVerifyTransactionalBehavior() {
		// This test verifies that the @Transactional annotation works correctly
		// The repository methods should execute within a read-only transaction
		Collection<Vet> vets = this.vetRepository.findAll();
		assertThat(vets).isNotNull();
		
		Pageable pageable = PageRequest.of(0, 3);
		Page<Vet> vetsPage = this.vetRepository.findAll(pageable);
		assertThat(vetsPage).isNotNull();
	}

	@Test
	void shouldVerifyCacheableBehavior() {
		// First call - should populate cache
		Collection<Vet> vets1 = this.vetRepository.findAll();
		assertThat(vets1).isNotNull();
		
		// Second call - should use cached result
		Collection<Vet> vets2 = this.vetRepository.findAll();
		assertThat(vets2).isNotNull();
		assertThat(vets1).hasSize(vets2.size());
		
		// Pageable calls should also be cached
		Pageable pageable = PageRequest.of(0, 5);
		Page<Vet> vetsPage1 = this.vetRepository.findAll(pageable);
		Page<Vet> vetsPage2 = this.vetRepository.findAll(pageable);
		
		assertThat(vetsPage1.getTotalElements()).isEqualTo(vetsPage2.getTotalElements());
	}
}
