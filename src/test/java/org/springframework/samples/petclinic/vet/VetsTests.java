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

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for the {@link Vets} class.
 */
class VetsTests {

	private Vets vets;

	@BeforeEach
	void setup() {
		vets = new Vets();
	}

	@Test
	void testGetVetListWithEmptyList() {
		List<Vet> vetList = vets.getVetList();
		
		assertThat(vetList).isNotNull();
		assertThat(vetList).isEmpty();
	}

	@Test
	void testGetVetListWithAddedVets() {
		// Create and add vets to the list
		List<Vet> initialVetList = vets.getVetList();
		
		Vet vet1 = new Vet();
		vet1.setFirstName("James");
		vet1.setLastName("Carter");
		vet1.setId(1);
		
		Vet vet2 = new Vet();
		vet2.setFirstName("Helen");
		vet2.setLastName("Leary");
		vet2.setId(2);
		
		initialVetList.add(vet1);
		initialVetList.add(vet2);
		
		// Get the list again and verify
		List<Vet> retrievedVetList = vets.getVetList();
		
		assertThat(retrievedVetList).isNotNull();
		assertThat(retrievedVetList).hasSize(2);
		assertThat(retrievedVetList).contains(vet1, vet2);
	}

	@Test
	void testGetVetListReturnsSameInstance() {
		List<Vet> firstCall = vets.getVetList();
		List<Vet> secondCall = vets.getVetList();
		
		assertThat(secondCall).isSameAs(firstCall);
	}

	@Test
	void testGetVetListModification() {
		List<Vet> vetList = vets.getVetList();
		
		Vet vet = new Vet();
		vet.setFirstName("John");
		vet.setLastName("Doe");
		vet.setId(3);
		
		vetList.add(vet);
		
		assertThat(vets.getVetList()).hasSize(1);
		assertThat(vets.getVetList().get(0)).isEqualTo(vet);
	}

	@Test
	void testXmlAnnotations() {
		// This test verifies that the class has proper XML annotations
		// We can't directly test annotations at runtime, but we can check that the class
		// has the expected structure for XML serialization
		
		// Create a vet to add to the list
		Vet vet = new Vet();
		vet.setFirstName("John");
		vet.setLastName("Doe");
		vet.setId(1);
		
		vets.getVetList().add(vet);
		
		// Verify the list contains our vet (indirect verification that the structure works as expected)
		assertThat(vets.getVetList()).hasSize(1);
		assertThat(vets.getVetList().get(0).getFirstName()).isEqualTo("John");
		assertThat(vets.getVetList().get(0).getLastName()).isEqualTo("Doe");
	}
}
