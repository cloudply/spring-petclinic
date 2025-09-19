package org.springframework.samples.petclinic.vet;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Specialty}.
 */
class SpecialtyTests {

	private Specialty specialty;
	
	@BeforeEach
	void setup() {
		specialty = new Specialty();
	}
	
	@Test
	void shouldInheritFromNamedEntity() {
		// given
		String name = "Surgery";
		
		// when
		specialty.setName(name);
		
		// then
		assertThat(specialty.getName()).isEqualTo(name);
	}
	
	@Test
	void shouldHaveProperToString() {
		// given
		String name = "Dentistry";
		specialty.setName(name);
		
		// when
		String result = specialty.toString();
		
		// then
		assertThat(result).isEqualTo(name);
	}
}
