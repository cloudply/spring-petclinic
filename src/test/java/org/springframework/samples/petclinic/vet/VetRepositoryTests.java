package org.springframework.samples.petclinic.vet;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link VetRepository} interface.
 */
@DataJpaTest
class VetRepositoryTests {

	@Autowired
	private VetRepository vetRepository;

	private Vet vet;

	@BeforeEach
	void setup() {
		// The repository is pre-populated by data.sql script
	}

	@Test
	void testFindAll() {
		Collection<Vet> vets = this.vetRepository.findAll();
		
		// Verify we have vets from the data.sql script
		assertThat(vets).isNotEmpty();
		
		// Verify we have at least one vet with specialties
		boolean hasSpecialties = vets.stream()
			.anyMatch(v -> v.getNrOfSpecialties() > 0);
		assertThat(hasSpecialties).isTrue();
		
		// Verify we have the expected vets from data.sql
		boolean hasJamesCarter = vets.stream()
			.anyMatch(v -> "Carter".equals(v.getLastName()) && "James".equals(v.getFirstName()));
		assertThat(hasJamesCarter).isTrue();
		
		boolean hasHelenLeary = vets.stream()
			.anyMatch(v -> "Leary".equals(v.getLastName()) && "Helen".equals(v.getFirstName()));
		assertThat(hasHelenLeary).isTrue();
	}

	@Test
	void testFindAllWithPagination() {
		// Test first page (page 0) with 2 elements
		Pageable pageable = PageRequest.of(0, 2);
		Page<Vet> vetPage = this.vetRepository.findAll(pageable);
		
		assertThat(vetPage).isNotNull();
		assertThat(vetPage.getContent()).isNotEmpty();
		assertThat(vetPage.getContent().size()).isEqualTo(2);
		assertThat(vetPage.getTotalElements()).isGreaterThanOrEqualTo(6); // At least 6 vets in data.sql
		
		// Test second page
		pageable = PageRequest.of(1, 2);
		vetPage = this.vetRepository.findAll(pageable);
		
		assertThat(vetPage).isNotNull();
		assertThat(vetPage.getContent()).isNotEmpty();
		assertThat(vetPage.getContent().size()).isEqualTo(2);
		
		// Test with different page size
		pageable = PageRequest.of(0, 4);
		vetPage = this.vetRepository.findAll(pageable);
		
		assertThat(vetPage).isNotNull();
		assertThat(vetPage.getContent()).isNotEmpty();
		assertThat(vetPage.getContent().size()).isEqualTo(4);
	}
	
	@Test
	void testCacheableAnnotation() {
		// This is a more structural test to verify the annotation is present
		// The actual caching behavior would be tested in an integration test
		
		try {
			java.lang.reflect.Method findAllMethod = VetRepository.class.getMethod("findAll");
			Cacheable cacheable = findAllMethod.getAnnotation(Cacheable.class);
			
			assertThat(cacheable).isNotNull();
			assertThat(cacheable.value()[0]).isEqualTo("vets");
			
			java.lang.reflect.Method findAllPageableMethod = 
				VetRepository.class.getMethod("findAll", Pageable.class);
			Cacheable cacheablePaged = findAllPageableMethod.getAnnotation(Cacheable.class);
			
			assertThat(cacheablePaged).isNotNull();
			assertThat(cacheablePaged.value()[0]).isEqualTo("vets");
			
		} catch (NoSuchMethodException e) {
			// If this happens, the test should fail
			assertThat(false).isTrue();
		}
	}
}
