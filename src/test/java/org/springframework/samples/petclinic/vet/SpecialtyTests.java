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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.model.NamedEntity;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link Specialty}.
 *
 * @author Spring PetClinic Team
 */
class SpecialtyTests {

	private Specialty specialty;

	@BeforeEach
	void setUp() {
		specialty = new Specialty();
	}

	@Test
	void testSpecialtyCreation() {
		assertThat(specialty).isNotNull();
		assertThat(specialty.getId()).isNull();
		assertThat(specialty.getName()).isNull();
	}

	@Test
	void testSpecialtyInheritanceFromNamedEntity() {
		assertThat(specialty).isInstanceOf(NamedEntity.class);
		assertThat(specialty).isInstanceOf(BaseEntity.class);
	}

	@Test
	void testSetAndGetName() {
		String specialtyName = "Dentistry";
		specialty.setName(specialtyName);
		
		assertThat(specialty.getName()).isEqualTo(specialtyName);
	}

	@Test
	void testSetAndGetId() {
		Integer specialtyId = 1;
		specialty.setId(specialtyId);
		
		assertThat(specialty.getId()).isEqualTo(specialtyId);
		assertThat(specialty.isNew()).isFalse();
	}

	@Test
	void testIsNewWhenIdIsNull() {
		specialty.setId(null);
		assertThat(specialty.isNew()).isTrue();
	}

	@Test
	void testIsNewWhenIdIsSet() {
		specialty.setId(1);
		assertThat(specialty.isNew()).isFalse();
	}

	@Test
	void testToString() {
		specialty.setName("Surgery");
		String toString = specialty.toString();
		
		assertThat(toString).isNotNull();
		assertThat(toString).contains("Surgery");
	}

	@Test
	void testToStringWithNullName() {
		specialty.setName(null);
		String toString = specialty.toString();
		
		assertThat(toString).isNotNull();
	}

	@Test
	void testSerialization() throws IOException, ClassNotFoundException {
		specialty.setId(1);
		specialty.setName("Radiology");

		// Serialize
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(specialty);
		oos.close();

		// Deserialize
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bais);
		Specialty deserializedSpecialty = (Specialty) ois.readObject();
		ois.close();

		assertThat(deserializedSpecialty.getId()).isEqualTo(specialty.getId());
		assertThat(deserializedSpecialty.getName()).isEqualTo(specialty.getName());
	}

	@Test
	void testSpecialtyWithEmptyName() {
		specialty.setName("");
		assertThat(specialty.getName()).isEmpty();
	}

	@Test
	void testSpecialtyWithWhitespaceName() {
		String nameWithWhitespace = "  Cardiology  ";
		specialty.setName(nameWithWhitespace);
		assertThat(specialty.getName()).isEqualTo(nameWithWhitespace);
	}

	@Test
	void testSpecialtyWithLongName() {
		String longName = "Very Long Specialty Name That Might Be Used In Some Veterinary Practices";
		specialty.setName(longName);
		assertThat(specialty.getName()).isEqualTo(longName);
	}

	@Test
	void testSpecialtyWithSpecialCharacters() {
		String nameWithSpecialChars = "Orthopedics & Surgery";
		specialty.setName(nameWithSpecialChars);
		assertThat(specialty.getName()).isEqualTo(nameWithSpecialChars);
	}

	@Test
	void testSpecialtyWithUnicodeCharacters() {
		String unicodeName = "Médecine Vétérinaire";
		specialty.setName(unicodeName);
		assertThat(specialty.getName()).isEqualTo(unicodeName);
	}

	@Test
	void testMultipleSpecialtiesWithSameProperties() {
		Specialty specialty1 = new Specialty();
		specialty1.setId(1);
		specialty1.setName("Dentistry");

		Specialty specialty2 = new Specialty();
		specialty2.setId(1);
		specialty2.setName("Dentistry");

		// Note: Since the class doesn't override equals/hashCode, 
		// these will not be equal by default (using Object's implementation)
		assertThat(specialty1).isNotEqualTo(specialty2);
		assertThat(specialty1.getId()).isEqualTo(specialty2.getId());
		assertThat(specialty1.getName()).isEqualTo(specialty2.getName());
	}

	@Test
	void testSpecialtyStateChanges() {
		// Test initial state
		assertThat(specialty.isNew()).isTrue();
		
		// Set name only
		specialty.setName("Ophthalmology");
		assertThat(specialty.isNew()).isTrue();
		assertThat(specialty.getName()).isEqualTo("Ophthalmology");
		
		// Set ID
		specialty.setId(5);
		assertThat(specialty.isNew()).isFalse();
		assertThat(specialty.getId()).isEqualTo(5);
		
		// Change name after setting ID
		specialty.setName("Updated Ophthalmology");
		assertThat(specialty.getName()).isEqualTo("Updated Ophthalmology");
		assertThat(specialty.getId()).isEqualTo(5);
		assertThat(specialty.isNew()).isFalse();
	}

	@Test
	void testSpecialtyWithNegativeId() {
		specialty.setId(-1);
		assertThat(specialty.getId()).isEqualTo(-1);
		assertThat(specialty.isNew()).isFalse();
	}

	@Test
	void testSpecialtyWithZeroId() {
		specialty.setId(0);
		assertThat(specialty.getId()).isEqualTo(0);
		assertThat(specialty.isNew()).isFalse();
	}

	@Test
	void testSpecialtyWithMaxIntegerId() {
		specialty.setId(Integer.MAX_VALUE);
		assertThat(specialty.getId()).isEqualTo(Integer.MAX_VALUE);
		assertThat(specialty.isNew()).isFalse();
	}

	@Test
	void testSpecialtyResetId() {
		specialty.setId(10);
		assertThat(specialty.isNew()).isFalse();
		
		specialty.setId(null);
		assertThat(specialty.isNew()).isTrue();
	}

	@Test
	void testCommonSpecialtyNames() {
		String[] commonSpecialties = {
			"Surgery", "Dentistry", "Radiology", "Cardiology", 
			"Dermatology", "Ophthalmology", "Oncology", "Neurology"
		};
		
		for (String specialtyName : commonSpecialties) {
			Specialty testSpecialty = new Specialty();
			testSpecialty.setName(specialtyName);
			assertThat(testSpecialty.getName()).isEqualTo(specialtyName);
		}
	}

	@Test
	void testSpecialtyNameCaseSensitivity() {
		specialty.setName("surgery");
		assertThat(specialty.getName()).isEqualTo("surgery");
		assertThat(specialty.getName()).isNotEqualTo("Surgery");
		assertThat(specialty.getName()).isNotEqualTo("SURGERY");
	}

	@Test
	void testSpecialtyBuilderPattern() {
		// Test method chaining if applicable (though not implemented in this simple entity)
		specialty.setId(1);
		specialty.setName("Pathology");
		
		assertThat(specialty.getId()).isEqualTo(1);
		assertThat(specialty.getName()).isEqualTo("Pathology");
	}
}
