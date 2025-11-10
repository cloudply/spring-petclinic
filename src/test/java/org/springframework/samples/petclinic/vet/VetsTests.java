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

import org.junit.jupiter.api.Test;
import org.springframework.util.SerializationUtils;

/**
 * Tests for the {@link Vets} class.
 */
class VetsTests {

	@Test
	void testGetVetListReturnsEmptyListByDefault() {
		Vets vets = new Vets();
		List<Vet> vetList = vets.getVetList();
		
		assertThat(vetList).isNotNull();
		assertThat(vetList).isEmpty();
	}
	
	@Test
	void testGetVetListReturnsSameInstance() {
		Vets vets = new Vets();
		List<Vet> firstCall = vets.getVetList();
		List<Vet> secondCall = vets.getVetList();
		
		assertThat(firstCall).isSameAs(secondCall);
	}
	
	@Test
	void testGetVetListAllowsModification() {
		Vets vets = new Vets();
		List<Vet> vetList = vets.getVetList();
		
		Vet vet = new Vet();
		vet.setFirstName("James");
		vet.setLastName("Carter");
		
		vetList.add(vet);
		
		assertThat(vets.getVetList()).hasSize(1);
		assertThat(vets.getVetList().get(0)).isSameAs(vet);
	}
	
	@Test
	void testSerialization() {
		Vets vets = new Vets();
		List<Vet> vetList = vets.getVetList();
		
		Vet vet = new Vet();
		vet.setFirstName("James");
		vet.setLastName("Carter");
		vet.setId(1);
		
		vetList.add(vet);
		
		@SuppressWarnings("deprecation")
		Vets deserializedVets = (Vets) SerializationUtils.deserialize(SerializationUtils.serialize(vets));
		
		assertThat(deserializedVets.getVetList()).isNotNull();
		assertThat(deserializedVets.getVetList()).hasSize(1);
		assertThat(deserializedVets.getVetList().get(0).getFirstName()).isEqualTo("James");
		assertThat(deserializedVets.getVetList().get(0).getLastName()).isEqualTo("Carter");
		assertThat(deserializedVets.getVetList().get(0).getId()).isEqualTo(1);
	}
	
	@Test
	void testXmlAnnotations() {
		// Verify the class has proper XML annotations
		assertThat(Vets.class.isAnnotationPresent(jakarta.xml.bind.annotation.XmlRootElement.class)).isTrue();
		
		try {
			assertThat(Vets.class.getMethod("getVetList").isAnnotationPresent(jakarta.xml.bind.annotation.XmlElement.class)).isTrue();
		}
		catch (NoSuchMethodException e) {
			throw new AssertionError("getVetList method not found or not properly annotated", e);
		}
	}
}
