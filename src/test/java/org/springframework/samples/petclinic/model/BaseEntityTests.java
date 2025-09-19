package org.springframework.samples.petclinic.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link BaseEntity}.
 */
class BaseEntityTests {

	private BaseEntity entity;
	
	@BeforeEach
	void setup() {
		entity = new BaseEntity() {};
	}
	
	@Test
	void shouldBeNewWhenIdIsNull() {
		// given
		entity.setId(null);
		
		// then
		assertThat(entity.isNew()).isTrue();
	}
	
	@Test
	void shouldNotBeNewWhenIdIsNotNull() {
		// given
		entity.setId(1);
		
		// then
		assertThat(entity.isNew()).isFalse();
	}
	
	@Test
	void shouldSetAndGetId() {
		// given
		Integer id = 42;
		
		// when
		entity.setId(id);
		
		// then
		assertThat(entity.getId()).isEqualTo(id);
	}
}
