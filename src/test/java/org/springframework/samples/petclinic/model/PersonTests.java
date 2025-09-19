package org.springframework.samples.petclinic.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Person}.
 */
class PersonTests {

	private Person person;
	
	@BeforeEach
	void setup() {
		person = new Person() {};
	}
	
	@Test
	void shouldSetAndGetFirstName() {
		// given
		String firstName = "John";
		
		// when
		person.setFirstName(firstName);
		
		// then
		assertThat(person.getFirstName()).isEqualTo(firstName);
	}
	
	@Test
	void shouldSetAndGetLastName() {
		// given
		String lastName = "Doe";
		
		// when
		person.setLastName(lastName);
		
		// then
		assertThat(person.getLastName()).isEqualTo(lastName);
	}
	
	@Test
	void shouldHandleNullFirstName() {
		// when
		person.setFirstName(null);
		
		// then
		assertThat(person.getFirstName()).isNull();
	}
	
	@Test
	void shouldHandleNullLastName() {
		// when
		person.setLastName(null);
		
		// then
		assertThat(person.getLastName()).isNull();
	}
	
	@Test
	void shouldHandleEmptyFirstName() {
		// given
		String firstName = "";
		
		// when
		person.setFirstName(firstName);
		
		// then
		assertThat(person.getFirstName()).isEmpty();
	}
	
	@Test
	void shouldHandleEmptyLastName() {
		// given
		String lastName = "";
		
		// when
		person.setLastName(lastName);
		
		// then
		assertThat(person.getLastName()).isEmpty();
	}
}
