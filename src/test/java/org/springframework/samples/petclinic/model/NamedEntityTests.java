package org.springframework.samples.petclinic.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link NamedEntity}.
 */
class NamedEntityTests {

	private NamedEntity entity;
	
	@BeforeEach
	void setup() {
		entity = new NamedEntity() {};
	}
	
	@Test
	void shouldSetAndGetName() {
		// given
		String name = "Test Name";
		
		// when
		entity.setName(name);
		
		// then
		assertThat(entity.getName()).isEqualTo(name);
	}
	
	@Test
	void shouldHandleNullName() {
		// when
		entity.setName(null);
		
		// then
		assertThat(entity.getName()).isNull();
	}
	
	@Test
	void shouldHandleEmptyName() {
		// given
		String name = "";
		
		// when
		entity.setName(name);
		
		// then
		assertThat(entity.getName()).isEmpty();
	}
	
	@Test
	void shouldReturnNameInToString() {
		// given
		String name = "Test Entity";
		entity.setName(name);
		
		// when
		String result = entity.toString();
		
		// then
		assertThat(result).isEqualTo(name);
	}
}
