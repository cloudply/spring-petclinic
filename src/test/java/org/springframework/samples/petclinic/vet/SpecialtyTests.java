package org.springframework.samples.petclinic.vet;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.SerializationUtils;

/**
 * Tests for the {@link Specialty} class.
 */
class SpecialtyTests {

	private Specialty specialty;

	@BeforeEach
	void setup() {
		specialty = new Specialty();
	}

	@Test
	void testSerialization() {
		specialty.setName("dentistry");
		specialty.setId(123);
		
		@SuppressWarnings("deprecation")
		Specialty other = (Specialty) SerializationUtils.deserialize(SerializationUtils.serialize(specialty));
		
		assertThat(other.getId()).isEqualTo(specialty.getId());
		assertThat(other.getName()).isEqualTo(specialty.getName());
	}

	@Test
	void testGetName() {
		String name = "surgery";
		specialty.setName(name);
		assertThat(specialty.getName()).isEqualTo(name);
	}

	@Test
	void testSetName() {
		String name = "radiology";
		specialty.setName(name);
		assertThat(specialty.getName()).isEqualTo(name);
	}

	@Test
	void testToString() {
		String name = "neurology";
		specialty.setName(name);
		assertThat(specialty.toString()).isEqualTo(name);
	}

	@Test
	void testInheritance() {
		assertThat(specialty).isInstanceOf(org.springframework.samples.petclinic.model.NamedEntity.class);
	}
}
