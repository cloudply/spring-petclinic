package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Pet}.
 */
class PetTests {

	private Pet pet;
	private PetType petType;
	
	@BeforeEach
	void setup() {
		pet = new Pet();
		petType = new PetType();
		petType.setName("Dog");
	}
	
	@Test
	void shouldSetAndGetBirthDate() {
		// given
		LocalDate birthDate = LocalDate.of(2020, 1, 1);
		
		// when
		pet.setBirthDate(birthDate);
		
		// then
		assertThat(pet.getBirthDate()).isEqualTo(birthDate);
	}
	
	@Test
	void shouldSetAndGetType() {
		// when
		pet.setType(petType);
		
		// then
		assertThat(pet.getType()).isEqualTo(petType);
		assertThat(pet.getType().getName()).isEqualTo("Dog");
	}
	
	@Test
	void shouldInitializeVisitsCollection() {
		// when
		Collection<Visit> visits = pet.getVisits();
		
		// then
		assertThat(visits).isNotNull();
		assertThat(visits).isEmpty();
	}
	
	@Test
	void shouldAddVisit() {
		// given
		Visit visit = new Visit();
		visit.setDate(LocalDate.now());
		visit.setDescription("Annual checkup");
		
		// when
		pet.addVisit(visit);
		
		// then
		assertThat(pet.getVisits()).contains(visit);
		assertThat(pet.getVisits().size()).isEqualTo(1);
	}
	
	@Test
	void shouldAddMultipleVisits() {
		// given
		Visit visit1 = new Visit();
		visit1.setDate(LocalDate.now());
		visit1.setDescription("Annual checkup");
		
		Visit visit2 = new Visit();
		visit2.setDate(LocalDate.now().minusDays(30));
		visit2.setDescription("Vaccination");
		
		// when
		pet.addVisit(visit1);
		pet.addVisit(visit2);
		
		// then
		assertThat(pet.getVisits()).contains(visit1, visit2);
		assertThat(pet.getVisits().size()).isEqualTo(2);
	}
}
