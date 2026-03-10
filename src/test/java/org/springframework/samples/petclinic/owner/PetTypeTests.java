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
package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import org.springframework.util.SerializationUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link PetType}
 */
class PetTypeTests {

	@Test
	void testPetTypeCreation() {
		PetType petType = new PetType();
		assertThat(petType).isNotNull();
		assertThat(petType.getId()).isNull();
		assertThat(petType.getName()).isNull();
		assertThat(petType.isNew()).isTrue();
	}

	@Test
	void testPetTypeWithId() {
		PetType petType = new PetType();
		petType.setId(1);
		
		assertThat(petType.getId()).isEqualTo(1);
		assertThat(petType.isNew()).isFalse();
	}

	@Test
	void testPetTypeWithName() {
		PetType petType = new PetType();
		petType.setName("Dog");
		
		assertThat(petType.getName()).isEqualTo("Dog");
	}

	@Test
	void testPetTypeWithIdAndName() {
		PetType petType = new PetType();
		petType.setId(1);
		petType.setName("Cat");
		
		assertThat(petType.getId()).isEqualTo(1);
		assertThat(petType.getName()).isEqualTo("Cat");
		assertThat(petType.isNew()).isFalse();
	}

	@Test
	void testPetTypeToString() {
		PetType petType = new PetType();
		petType.setName("Hamster");
		
		String result = petType.toString();
		assertThat(result).contains("Hamster");
	}

	@Test
	void testPetTypeToStringWithNullName() {
		PetType petType = new PetType();
		
		String result = petType.toString();
		assertThat(result).isNotNull();
	}

	@Test
	void testPetTypeEquality() {
		PetType petType1 = new PetType();
		petType1.setId(1);
		petType1.setName("Bird");
		
		PetType petType2 = new PetType();
		petType2.setId(1);
		petType2.setName("Bird");
		
		// Note: BaseEntity doesn't override equals/hashCode, so this tests object identity
		assertThat(petType1).isNotEqualTo(petType2);
		assertThat(petType1).isEqualTo(petType1);
	}

	@Test
	void testPetTypeSerialization() {
		PetType petType = new PetType();
		petType.setId(123);
		petType.setName("Rabbit");
		
		@SuppressWarnings("deprecation")
		PetType deserializedPetType = (PetType) SerializationUtils.deserialize(SerializationUtils.serialize(petType));
		
		assertThat(deserializedPetType.getId()).isEqualTo(petType.getId());
		assertThat(deserializedPetType.getName()).isEqualTo(petType.getName());
		assertThat(deserializedPetType.isNew()).isEqualTo(petType.isNew());
	}

	@Test
	void testPetTypeWithEmptyName() {
		PetType petType = new PetType();
		petType.setName("");
		
		assertThat(petType.getName()).isEmpty();
	}

	@Test
	void testPetTypeWithWhitespaceName() {
		PetType petType = new PetType();
		petType.setName("   ");
		
		assertThat(petType.getName()).isEqualTo("   ");
	}

	@Test
	void testPetTypeWithLongName() {
		PetType petType = new PetType();
		String longName = "Very Long Pet Type Name That Exceeds Normal Length";
		petType.setName(longName);
		
		assertThat(petType.getName()).isEqualTo(longName);
	}

	@Test
	void testPetTypeWithSpecialCharactersInName() {
		PetType petType = new PetType();
		petType.setName("Dog & Cat Mix");
		
		assertThat(petType.getName()).isEqualTo("Dog & Cat Mix");
	}

	@Test
	void testPetTypeWithNumericName() {
		PetType petType = new PetType();
		petType.setName("123");
		
		assertThat(petType.getName()).isEqualTo("123");
	}

	@Test
	void testPetTypeIdBoundaryValues() {
		PetType petType = new PetType();
		
		// Test with zero
		petType.setId(0);
		assertThat(petType.getId()).isEqualTo(0);
		assertThat(petType.isNew()).isFalse();
		
		// Test with negative value
		petType.setId(-1);
		assertThat(petType.getId()).isEqualTo(-1);
		assertThat(petType.isNew()).isFalse();
		
		// Test with large value
		petType.setId(Integer.MAX_VALUE);
		assertThat(petType.getId()).isEqualTo(Integer.MAX_VALUE);
		assertThat(petType.isNew()).isFalse();
	}

	@Test
	void testPetTypeResetId() {
		PetType petType = new PetType();
		petType.setId(1);
		assertThat(petType.isNew()).isFalse();
		
		petType.setId(null);
		assertThat(petType.getId()).isNull();
		assertThat(petType.isNew()).isTrue();
	}

	@Test
	void testPetTypeNameModification() {
		PetType petType = new PetType();
		petType.setName("Dog");
		assertThat(petType.getName()).isEqualTo("Dog");
		
		petType.setName("Cat");
		assertThat(petType.getName()).isEqualTo("Cat");
		
		petType.setName(null);
		assertThat(petType.getName()).isNull();
	}
}
