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
	void testSpecialtyManagement() {
		Vet vet = new Vet();
		
		// Initially no specialties
		assertThat(vet.getNrOfSpecialties()).isEqualTo(0);
		assertThat(vet.getSpecialties()).isEmpty();
		
		// Add one specialty
		Specialty radiology = new Specialty();
		radiology.setName("radiology");
		vet.addSpecialty(radiology);
		
		assertThat(vet.getNrOfSpecialties()).isEqualTo(1);
		assertThat(vet.getSpecialties()).hasSize(1);
		assertThat(vet.getSpecialties().get(0)).isEqualTo(radiology);
		
		// Add another specialty
		Specialty surgery = new Specialty();
		surgery.setName("surgery");
		vet.addSpecialty(surgery);
		
		assertThat(vet.getNrOfSpecialties()).isEqualTo(2);
		assertThat(vet.getSpecialties()).hasSize(2);
	}
	
	@Test
	void testSpecialtySorting() {
		Vet vet = new Vet();
		
		// Add specialties in non-alphabetical order
		Specialty surgery = new Specialty();
		surgery.setName("surgery");
		vet.addSpecialty(surgery);
		
		Specialty radiology = new Specialty();
		radiology.setName("radiology");
		vet.addSpecialty(radiology);
		
		Specialty dentistry = new Specialty();
		dentistry.setName("dentistry");
		vet.addSpecialty(dentistry);
		
		// Verify they're returned in alphabetical order
		List<Specialty> specialties = vet.getSpecialties();
		assertThat(specialties).hasSize(3);
		assertThat(specialties.get(0).getName()).isEqualTo("dentistry");
		assertThat(specialties.get(1).getName()).isEqualTo("radiology");
		assertThat(specialties.get(2).getName()).isEqualTo("surgery");
	}
	
	@Test
	void testSpecialtiesInternalManagement() {
		Vet vet = new Vet();
		
		// Test initial null handling
		assertThat(vet.getSpecialties()).isEmpty();
		
		// Test setting specialties internally
		Set<Specialty> specialties = new HashSet<>();
		Specialty cardiology = new Specialty();
		cardiology.setName("cardiology");
		specialties.add(cardiology);
		
		vet.setSpecialtiesInternal(specialties);
		assertThat(vet.getNrOfSpecialties()).isEqualTo(1);
		assertThat(vet.getSpecialties().get(0).getName()).isEqualTo("cardiology");
	}
	
	@Test
	void testUnmodifiableSpecialties() {
		Vet vet = new Vet();
		Specialty radiology = new Specialty();
		radiology.setName("radiology");
		vet.addSpecialty(radiology);
		
		List<Specialty> specialties = vet.getSpecialties();
		
		// Verify the returned list is unmodifiable
		assertThatExceptionOfType(UnsupportedOperationException.class)
			.isThrownBy(() -> specialties.add(new Specialty()));
	}
}
