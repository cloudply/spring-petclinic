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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Dave Syer
 */
class VetTests {

	@Test
	void testSerialization() {
		Vet vet = new Vet();
		vet.setFirstName("Zaphod");
		vet.setLastName("Beeblebrox");
		vet.setId(123);
		@SuppressWarnings("deprecation")
		Vet other = (Vet) SerializationUtils.deserialize(SerializationUtils.serialize(vet));
		assertThat(other.getFirstName()).isEqualTo(vet.getFirstName());
		assertThat(other.getLastName()).isEqualTo(vet.getLastName());
		assertThat(other.getId()).isEqualTo(vet.getId());
	}

	@Test
	void testGetSpecialtiesInternal() {
		// Test when specialties is null
		Vet vet = new Vet();
		Set<Specialty> specialties = vet.getSpecialtiesInternal();
		assertThat(specialties).isNotNull();
		assertThat(specialties).isEmpty();

		// Test when specialties is already initialized
		Specialty specialty = new Specialty();
		specialty.setName("surgery");
		specialties.add(specialty);

		Set<Specialty> specialtiesAgain = vet.getSpecialtiesInternal();
		assertThat(specialtiesAgain).isEqualTo(specialties);
		assertThat(specialtiesAgain).hasSize(1);
	}

	@Test
	void testSetSpecialtiesInternal() {
		Vet vet = new Vet();
		Set<Specialty> specialties = new HashSet<>();
		Specialty specialty = new Specialty();
		specialty.setName("dentistry");
		specialties.add(specialty);

		vet.setSpecialtiesInternal(specialties);

		assertThat(vet.getSpecialtiesInternal()).isEqualTo(specialties);
	}

	@Test
	void testGetSpecialties() {
		Vet vet = new Vet();

		// Test with empty specialties
		List<Specialty> emptySpecialties = vet.getSpecialties();
		assertThat(emptySpecialties).isEmpty();

		// Test with multiple specialties
		Specialty surgery = new Specialty();
		surgery.setName("surgery");
		surgery.setId(1);

		Specialty radiology = new Specialty();
		radiology.setName("radiology");
		radiology.setId(2);

		Specialty dentistry = new Specialty();
		dentistry.setName("dentistry");
		dentistry.setId(3);

		vet.addSpecialty(surgery);
		vet.addSpecialty(radiology);
		vet.addSpecialty(dentistry);

		List<Specialty> specialties = vet.getSpecialties();
		assertThat(specialties).hasSize(3);

		// Verify sorting by name
		assertThat(specialties.get(0).getName()).isEqualTo("dentistry");
		assertThat(specialties.get(1).getName()).isEqualTo("radiology");
		assertThat(specialties.get(2).getName()).isEqualTo("surgery");

		// Verify unmodifiable
		assertThatExceptionOfType(UnsupportedOperationException.class)
			.isThrownBy(() -> specialties.add(new Specialty()));
	}

	@Test
	void testGetNrOfSpecialties() {
		Vet vet = new Vet();
		assertThat(vet.getNrOfSpecialties()).isEqualTo(0);

		Specialty specialty1 = new Specialty();
		specialty1.setName("surgery");
		vet.addSpecialty(specialty1);
		assertThat(vet.getNrOfSpecialties()).isEqualTo(1);

		Specialty specialty2 = new Specialty();
		specialty2.setName("radiology");
		vet.addSpecialty(specialty2);
		assertThat(vet.getNrOfSpecialties()).isEqualTo(2);
	}

	@Test
	void testAddSpecialty() {
		Vet vet = new Vet();
		assertThat(vet.getNrOfSpecialties()).isEqualTo(0);

		// Add first specialty
		Specialty surgery = new Specialty();
		surgery.setName("surgery");
		vet.addSpecialty(surgery);

		assertThat(vet.getNrOfSpecialties()).isEqualTo(1);
		assertThat(vet.getSpecialties()).contains(surgery);

		// Add second specialty
		Specialty radiology = new Specialty();
		radiology.setName("radiology");
		vet.addSpecialty(radiology);

		assertThat(vet.getNrOfSpecialties()).isEqualTo(2);
		assertThat(vet.getSpecialties()).contains(surgery, radiology);

		// Add duplicate specialty (should still be added as Set doesn't enforce business
		// equality)
		Specialty surgeryCopy = new Specialty();
		surgeryCopy.setName("surgery");
		vet.addSpecialty(surgeryCopy);

		assertThat(vet.getNrOfSpecialties()).isEqualTo(3);
	}
	
	@Test
	void testSpecialtiesWithSameNameButDifferentIds() {
		Vet vet = new Vet();
		
		// Create two specialties with same name but different IDs
		Specialty surgery1 = new Specialty();
		surgery1.setName("surgery");
		surgery1.setId(1);
		
		Specialty surgery2 = new Specialty();
		surgery2.setName("surgery");
		surgery2.setId(2);
		
		vet.addSpecialty(surgery1);
		vet.addSpecialty(surgery2);
		
		// Both should be added since they have different IDs
		assertThat(vet.getNrOfSpecialties()).isEqualTo(2);
	}
	
	@Test
	void testSpecialtiesWithSameIdButDifferentNames() {
		Vet vet = new Vet();
		
		// Create two specialties with different names but same ID
		Specialty specialty1 = new Specialty();
		specialty1.setName("surgery");
		specialty1.setId(1);
		
		Specialty specialty2 = new Specialty();
		specialty2.setName("radiology");
		specialty2.setId(1);
		
		vet.addSpecialty(specialty1);
		vet.addSpecialty(specialty2);
		
		// Both should be added since Set uses object equality by default
		assertThat(vet.getNrOfSpecialties()).isEqualTo(2);
	}
	
	@Test
	void testSpecialtiesWithNullName() {
		Vet vet = new Vet();
		
		Specialty specialty = new Specialty();
		specialty.setName(null);
		specialty.setId(1);
		
		vet.addSpecialty(specialty);
		
		// Should be added even with null name
		assertThat(vet.getNrOfSpecialties()).isEqualTo(1);
		assertThat(vet.getSpecialties().get(0).getName()).isNull();
	}
}
