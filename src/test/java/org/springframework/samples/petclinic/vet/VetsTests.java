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
	void testGetVetListInitiallyEmpty() {
		// When
		List<Vet> vetList = vets.getVetList();

		// Then
		assertThat(vetList).isNotNull();
		assertThat(vetList).isEmpty();
	}

	@Test
	void testGetVetListReturnsSameInstance() {
		// When
		List<Vet> vetList1 = vets.getVetList();
		List<Vet> vetList2 = vets.getVetList();

		// Then
		assertThat(vetList1).isSameAs(vetList2);
	}

	@Test
	void testGetVetListCanAddVets() {
		// Given
		Vet vet1 = createVet("James", "Carter", 1);
		Vet vet2 = createVet("Helen", "Leary", 2);

		// When
		List<Vet> vetList = vets.getVetList();
		vetList.add(vet1);
		vetList.add(vet2);

		// Then
		assertThat(vetList).hasSize(2);
		assertThat(vetList).containsExactly(vet1, vet2);
	}

	@Test
	void testGetVetListMaintainsOrder() {
		// Given
		Vet vet1 = createVet("James", "Carter", 1);
		Vet vet2 = createVet("Helen", "Leary", 2);
		Vet vet3 = createVet("Linda", "Douglas", 3);

		// When
		List<Vet> vetList = vets.getVetList();
		vetList.add(vet1);
		vetList.add(vet2);
		vetList.add(vet3);

		// Then
		assertThat(vetList).hasSize(3);
		assertThat(vetList.get(0)).isEqualTo(vet1);
		assertThat(vetList.get(1)).isEqualTo(vet2);
		assertThat(vetList.get(2)).isEqualTo(vet3);
	}

	@Test
	void testGetVetListCanRemoveVets() {
		// Given
		Vet vet1 = createVet("James", "Carter", 1);
		Vet vet2 = createVet("Helen", "Leary", 2);
		List<Vet> vetList = vets.getVetList();
		vetList.add(vet1);
		vetList.add(vet2);

		// When
		vetList.remove(vet1);

		// Then
		assertThat(vetList).hasSize(1);
		assertThat(vetList).containsExactly(vet2);
	}

	@Test
	void testGetVetListCanClearVets() {
		// Given
		Vet vet1 = createVet("James", "Carter", 1);
		Vet vet2 = createVet("Helen", "Leary", 2);
		List<Vet> vetList = vets.getVetList();
		vetList.add(vet1);
		vetList.add(vet2);

		// When
		vetList.clear();

		// Then
		assertThat(vetList).isEmpty();
	}

	@Test
	void testGetVetListWithNullVet() {
		// When
		List<Vet> vetList = vets.getVetList();
		vetList.add(null);

		// Then
		assertThat(vetList).hasSize(1);
		assertThat(vetList.get(0)).isNull();
	}

	@Test
	void testGetVetListWithDuplicateVets() {
		// Given
		Vet vet = createVet("James", "Carter", 1);

		// When
		List<Vet> vetList = vets.getVetList();
		vetList.add(vet);
		vetList.add(vet);

		// Then
		assertThat(vetList).hasSize(2);
		assertThat(vetList.get(0)).isSameAs(vetList.get(1));
	}

	@Test
	void testMultipleCallsToGetVetListAfterModification() {
		// Given
		Vet vet = createVet("James", "Carter", 1);

		// When
		List<Vet> vetList1 = vets.getVetList();
		vetList1.add(vet);
		List<Vet> vetList2 = vets.getVetList();

		// Then
		assertThat(vetList1).isSameAs(vetList2);
		assertThat(vetList2).hasSize(1);
		assertThat(vetList2).containsExactly(vet);
	}

	@Test
	void testGetVetListIsModifiable() {
		// Given
		Vet vet1 = createVet("James", "Carter", 1);
		Vet vet2 = createVet("Helen", "Leary", 2);

		// When
		List<Vet> vetList = vets.getVetList();
		vetList.add(vet1);
		vetList.set(0, vet2);

		// Then
		assertThat(vetList).hasSize(1);
		assertThat(vetList.get(0)).isEqualTo(vet2);
	}

	private Vet createVet(String firstName, String lastName, int id) {
		Vet vet = new Vet();
		vet.setFirstName(firstName);
		vet.setLastName(lastName);
		vet.setId(id);
		return vet;
	}

}
