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
import org.springframework.util.SerializationUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link Specialty}
 *
 * @author Juergen Hoeller
 */
class SpecialtyTests {

	@Test
	void testSpecialtyCreation() {
		Specialty specialty = new Specialty();
		assertThat(specialty).isNotNull();
		assertThat(specialty.getId()).isNull();
		assertThat(specialty.getName()).isNull();
		assertThat(specialty.isNew()).isTrue();
	}

	@Test
	void testSpecialtyWithName() {
		Specialty specialty = new Specialty();
		specialty.setName("Dentistry");
		
		assertThat(specialty.getName()).isEqualTo("Dentistry");
		assertThat(specialty.isNew()).isTrue();
	}

	@Test
	void testSpecialtyWithId() {
		Specialty specialty = new Specialty();
		specialty.setId(1);
		
		assertThat(specialty.getId()).isEqualTo(1);
		assertThat(specialty.isNew()).isFalse();
	}

	@Test
	void testSpecialtyWithIdAndName() {
		Specialty specialty = new Specialty();
		specialty.setId(1);
		specialty.setName("Surgery");
		
		assertThat(specialty.getId()).isEqualTo(1);
		assertThat(specialty.getName()).isEqualTo("Surgery");
		assertThat(specialty.isNew()).isFalse();
	}

	@Test
	void testSpecialtyToString() {
		Specialty specialty = new Specialty();
		specialty.setName("Radiology");
		
		String result = specialty.toString();
		assertThat(result).contains("Radiology");
	}

	@Test
	void testSpecialtyToStringWithNullName() {
		Specialty specialty = new Specialty();
		
		String result = specialty.toString();
		assertThat(result).isNotNull();
	}

	@Test
	void testSpecialtyEquality() {
		Specialty specialty1 = new Specialty();
		specialty1.setId(1);
		specialty1.setName("Dentistry");
		
		Specialty specialty2 = new Specialty();
		specialty2.setId(1);
		specialty2.setName("Dentistry");
		
		// Note: BaseEntity doesn't override equals/hashCode, so this tests object identity
		assertThat(specialty1).isNotEqualTo(specialty2);
		assertThat(specialty1).isEqualTo(specialty1);
	}

	@Test
	void testSpecialtyHashCode() {
		Specialty specialty = new Specialty();
		specialty.setId(1);
		specialty.setName("Surgery");
		
		int hashCode1 = specialty.hashCode();
		int hashCode2 = specialty.hashCode();
		
		assertThat(hashCode1).isEqualTo(hashCode2);
	}

	@Test
	void testSerialization() {
		Specialty specialty = new Specialty();
		specialty.setId(123);
		specialty.setName("Cardiology");
		
		@SuppressWarnings("deprecation")
		Specialty other = (Specialty) SerializationUtils.deserialize(SerializationUtils.serialize(specialty));
		
		assertThat(other.getId()).isEqualTo(specialty.getId());
		assertThat(other.getName()).isEqualTo(specialty.getName());
		assertThat(other.isNew()).isEqualTo(specialty.isNew());
	}

	@Test
	void testSpecialtyNameEdgeCases() {
		Specialty specialty = new Specialty();
		
		// Test empty string
		specialty.setName("");
		assertThat(specialty.getName()).isEqualTo("");
		
		// Test whitespace
		specialty.setName("   ");
		assertThat(specialty.getName()).isEqualTo("   ");
		
		// Test long name
		String longName = "Very Long Specialty Name That Might Be Used In Some Cases";
		specialty.setName(longName);
		assertThat(specialty.getName()).isEqualTo(longName);
	}

	@Test
	void testSpecialtyIdEdgeCases() {
		Specialty specialty = new Specialty();
		
		// Test zero ID
		specialty.setId(0);
		assertThat(specialty.getId()).isEqualTo(0);
		assertThat(specialty.isNew()).isFalse();
		
		// Test negative ID (though not typical in real scenarios)
		specialty.setId(-1);
		assertThat(specialty.getId()).isEqualTo(-1);
		assertThat(specialty.isNew()).isFalse();
		
		// Test large ID
		specialty.setId(Integer.MAX_VALUE);
		assertThat(specialty.getId()).isEqualTo(Integer.MAX_VALUE);
		assertThat(specialty.isNew()).isFalse();
	}

	@Test
	void testSpecialtyStateTransitions() {
		Specialty specialty = new Specialty();
		
		// Initially new
		assertThat(specialty.isNew()).isTrue();
		
		// Set name, still new
		specialty.setName("Ophthalmology");
		assertThat(specialty.isNew()).isTrue();
		
		// Set ID, no longer new
		specialty.setId(1);
		assertThat(specialty.isNew()).isFalse();
		
		// Change name, still not new
		specialty.setName("Neurology");
		assertThat(specialty.isNew()).isFalse();
		
		// Reset ID to null, becomes new again
		specialty.setId(null);
		assertThat(specialty.isNew()).isTrue();
	}

	@Test
	void testSpecialtyInheritanceFromNamedEntity() {
		Specialty specialty = new Specialty();
		
		// Verify it's an instance of NamedEntity
		assertThat(specialty).isInstanceOf(org.springframework.samples.petclinic.model.NamedEntity.class);
		
		// Verify it's also an instance of BaseEntity (through inheritance)
		assertThat(specialty).isInstanceOf(org.springframework.samples.petclinic.model.BaseEntity.class);
	}
}
