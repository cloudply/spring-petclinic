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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link Vets}.
 *
 * @author Test Author
 */
class VetsTests {

	private Vets vets;

	@BeforeEach
	void setUp() {
		vets = new Vets();
	}

	@Test
	void testGetVetListWhenEmpty() {
		List<Vet> vetList = vets.getVetList();
		
		assertThat(vetList).isNotNull();
		assertThat(vetList).isEmpty();
		assertThat(vetList.size()).isEqualTo(0);
	}

	@Test
	void testGetVetListInitializesListOnFirstCall() {
		// First call should initialize the list
		List<Vet> vetList1 = vets.getVetList();
		
		assertThat(vetList1).isNotNull();
		assertThat(vetList1).isEmpty();
		
		// Second call should return the same list instance
		List<Vet> vetList2 = vets.getVetList();
		
		assertThat(vetList2).isSameAs(vetList1);
	}

	@Test
	void testGetVetListReturnsSameInstanceOnMultipleCalls() {
		List<Vet> vetList1 = vets.getVetList();
		List<Vet> vetList2 = vets.getVetList();
		List<Vet> vetList3 = vets.getVetList();
		
		assertThat(vetList1).isSameAs(vetList2);
		assertThat(vetList2).isSameAs(vetList3);
	}

	@Test
	void testGetVetListIsModifiable() {
		List<Vet> vetList = vets.getVetList();
		
		// Create a test vet
		Vet testVet = new Vet();
		testVet.setFirstName("John");
		testVet.setLastName("Doe");
		testVet.setId(1);
		
		// Add vet to the list
		vetList.add(testVet);
		
		assertThat(vetList.size()).isEqualTo(1);
		assertThat(vetList.get(0)).isEqualTo(testVet);
		
		// Verify the same list is returned on subsequent calls
		List<Vet> sameList = vets.getVetList();
		assertThat(sameList.size()).isEqualTo(1);
		assertThat(sameList.get(0)).isEqualTo(testVet);
	}

	@Test
	void testGetVetListWithMultipleVets() {
		List<Vet> vetList = vets.getVetList();
		
		// Create multiple test vets
		Vet vet1 = new Vet();
		vet1.setFirstName("John");
		vet1.setLastName("Doe");
		vet1.setId(1);
		
		Vet vet2 = new Vet();
		vet2.setFirstName("Jane");
		vet2.setLastName("Smith");
		vet2.setId(2);
		
		Vet vet3 = new Vet();
		vet3.setFirstName("Bob");
		vet3.setLastName("Johnson");
		vet3.setId(3);
		
		// Add vets to the list
		vetList.add(vet1);
		vetList.add(vet2);
		vetList.add(vet3);
		
		assertThat(vetList.size()).isEqualTo(3);
		assertThat(vetList).containsExactly(vet1, vet2, vet3);
	}

	@Test
	void testGetVetListWithVetsWithSpecialties() {
		List<Vet> vetList = vets.getVetList();
		
		// Create a vet with specialties
		Vet vet = new Vet();
		vet.setFirstName("Helen");
		vet.setLastName("Leary");
		vet.setId(1);
		
		Specialty radiology = new Specialty();
		radiology.setId(1);
		radiology.setName("radiology");
		vet.addSpecialty(radiology);
		
		Specialty surgery = new Specialty();
		surgery.setId(2);
		surgery.setName("surgery");
		vet.addSpecialty(surgery);
		
		vetList.add(vet);
		
		assertThat(vetList.size()).isEqualTo(1);
		assertThat(vetList.get(0)).isEqualTo(vet);
		assertThat(vetList.get(0).getNrOfSpecialties()).isEqualTo(2);
	}

	@Test
	void testGetVetListClearAndReAdd() {
		List<Vet> vetList = vets.getVetList();
		
		// Add a vet
		Vet vet = new Vet();
		vet.setFirstName("Test");
		vet.setLastName("Vet");
		vet.setId(1);
		vetList.add(vet);
		
		assertThat(vetList.size()).isEqualTo(1);
		
		// Clear the list
		vetList.clear();
		assertThat(vetList.size()).isEqualTo(0);
		assertThat(vetList).isEmpty();
		
		// Add another vet
		Vet newVet = new Vet();
		newVet.setFirstName("New");
		newVet.setLastName("Vet");
		newVet.setId(2);
		vetList.add(newVet);
		
		assertThat(vetList.size()).isEqualTo(1);
		assertThat(vetList.get(0)).isEqualTo(newVet);
	}

	@Test
	void testGetVetListRemoveVet() {
		List<Vet> vetList = vets.getVetList();
		
		// Add multiple vets
		Vet vet1 = new Vet();
		vet1.setFirstName("First");
		vet1.setLastName("Vet");
		vet1.setId(1);
		
		Vet vet2 = new Vet();
		vet2.setFirstName("Second");
		vet2.setLastName("Vet");
		vet2.setId(2);
		
		vetList.add(vet1);
		vetList.add(vet2);
		
		assertThat(vetList.size()).isEqualTo(2);
		
		// Remove one vet
		vetList.remove(vet1);
		
		assertThat(vetList.size()).isEqualTo(1);
		assertThat(vetList).containsExactly(vet2);
		assertThat(vetList).doesNotContain(vet1);
	}

	@Test
	void testVetsObjectCreation() {
		Vets newVets = new Vets();
		
		assertThat(newVets).isNotNull();
		
		List<Vet> vetList = newVets.getVetList();
		assertThat(vetList).isNotNull();
		assertThat(vetList).isEmpty();
	}

	@Test
	void testMultipleVetsObjectsAreIndependent() {
		Vets vets1 = new Vets();
		Vets vets2 = new Vets();
		
		List<Vet> vetList1 = vets1.getVetList();
		List<Vet> vetList2 = vets2.getVetList();
		
		// Lists should be different instances
		assertThat(vetList1).isNotSameAs(vetList2);
		
		// Add vet to first list
		Vet vet = new Vet();
		vet.setFirstName("Test");
		vet.setLastName("Vet");
		vet.setId(1);
		vetList1.add(vet);
		
		// First list should have one vet, second should be empty
		assertThat(vetList1.size()).isEqualTo(1);
		assertThat(vetList2.size()).isEqualTo(0);
	}
}
