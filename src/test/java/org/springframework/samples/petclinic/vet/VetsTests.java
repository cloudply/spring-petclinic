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

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit test for the {@link Vets} class.
 *
 * @author Petclinic Team
 */
class VetsTests {

	private Vets vets;

	@BeforeEach
	void setUp() {
		vets = new Vets();
	}

	@Test
	void testVetsCreation() {
		Vets newVets = new Vets();
		assertThat(newVets).isNotNull();
	}

	@Test
	void testGetVetListWhenNull() {
		// When vets list is null, getVetList should return an empty list
		List<Vet> vetList = vets.getVetList();
		
		assertThat(vetList).isNotNull();
		assertThat(vetList).isEmpty();
		assertThat(vetList).hasSize(0);
	}

	@Test
	void testGetVetListInitializesOnlyOnce() {
		// First call should initialize the list
		List<Vet> firstCall = vets.getVetList();
		
		// Second call should return the same list instance
		List<Vet> secondCall = vets.getVetList();
		
		assertThat(firstCall).isSameAs(secondCall);
		assertThat(firstCall).isEmpty();
	}

	@Test
	void testGetVetListIsModifiable() {
		List<Vet> vetList = vets.getVetList();
		
		// Create a test vet
		Vet testVet = new Vet();
		testVet.setFirstName("Test");
		testVet.setLastName("Vet");
		testVet.setId(1);
		
		// Should be able to add to the list
		vetList.add(testVet);
		
		assertThat(vetList).hasSize(1);
		assertThat(vetList.get(0)).isEqualTo(testVet);
		
		// Subsequent calls should return the modified list
		List<Vet> subsequentCall = vets.getVetList();
		assertThat(subsequentCall).hasSize(1);
		assertThat(subsequentCall.get(0)).isEqualTo(testVet);
	}

	@Test
	void testGetVetListWithMultipleVets() {
		List<Vet> vetList = vets.getVetList();
		
		// Create multiple test vets
		Vet vet1 = createTestVet(1, "James", "Carter");
		Vet vet2 = createTestVet(2, "Helen", "Leary");
		Vet vet3 = createTestVet(3, "Linda", "Douglas");
		
		vetList.add(vet1);
		vetList.add(vet2);
		vetList.add(vet3);
		
		assertThat(vetList).hasSize(3);
		assertThat(vetList).containsExactly(vet1, vet2, vet3);
	}

	@Test
	void testXmlMarshalling() throws JAXBException {
		// Prepare test data
		List<Vet> vetList = vets.getVetList();
		Vet testVet = createTestVet(1, "Test", "Vet");
		vetList.add(testVet);
		
		// Marshal to XML
		JAXBContext context = JAXBContext.newInstance(Vets.class, Vet.class, Specialty.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		StringWriter writer = new StringWriter();
		marshaller.marshal(vets, writer);
		String xml = writer.toString();
		
		// Verify XML contains expected elements
		assertThat(xml).contains("<vets>");
		assertThat(xml).contains("</vets>");
		assertThat(xml).contains("<vetList>");
		assertThat(xml).contains("</vetList>");
	}

	@Test
	void testXmlUnmarshalling() throws JAXBException {
		// Create XML string
		String xml = """
			<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
			<vets>
				<vetList>
					<vet>
						<id>1</id>
						<firstName>Test</firstName>
						<lastName>Vet</lastName>
					</vet>
				</vetList>
			</vets>
			""";
		
		// Unmarshal from XML
		JAXBContext context = JAXBContext.newInstance(Vets.class, Vet.class, Specialty.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		
		StringReader reader = new StringReader(xml);
		Vets unmarshalledVets = (Vets) unmarshaller.unmarshal(reader);
		
		// Verify unmarshalled object
		assertThat(unmarshalledVets).isNotNull();
		List<Vet> vetList = unmarshalledVets.getVetList();
		assertThat(vetList).hasSize(1);
		
		Vet vet = vetList.get(0);
		assertThat(vet.getId()).isEqualTo(1);
		assertThat(vet.getFirstName()).isEqualTo("Test");
		assertThat(vet.getLastName()).isEqualTo("Vet");
	}

	@Test
	void testXmlMarshallingWithEmptyList() throws JAXBException {
		// Marshal empty vets list
		JAXBContext context = JAXBContext.newInstance(Vets.class, Vet.class, Specialty.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		StringWriter writer = new StringWriter();
		marshaller.marshal(vets, writer);
		String xml = writer.toString();
		
		// Verify XML structure for empty list
		assertThat(xml).contains("<vets>");
		assertThat(xml).contains("</vets>");
		assertThat(xml).contains("<vetList/>");
	}

	@Test
	void testXmlMarshallingWithMultipleVets() throws JAXBException {
		// Prepare test data with multiple vets
		List<Vet> vetList = vets.getVetList();
		vetList.add(createTestVet(1, "James", "Carter"));
		vetList.add(createTestVet(2, "Helen", "Leary"));
		
		// Marshal to XML
		JAXBContext context = JAXBContext.newInstance(Vets.class, Vet.class, Specialty.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		StringWriter writer = new StringWriter();
		marshaller.marshal(vets, writer);
		String xml = writer.toString();
		
		// Verify XML contains multiple vet entries
		assertThat(xml).contains("<vets>");
		assertThat(xml).contains("</vets>");
		assertThat(xml).contains("James");
		assertThat(xml).contains("Carter");
		assertThat(xml).contains("Helen");
		assertThat(xml).contains("Leary");
	}

	@Test
	void testGetVetListConsistency() {
		// Test that multiple calls return consistent results
		List<Vet> firstCall = vets.getVetList();
		List<Vet> secondCall = vets.getVetList();
		List<Vet> thirdCall = vets.getVetList();
		
		assertThat(firstCall).isSameAs(secondCall);
		assertThat(secondCall).isSameAs(thirdCall);
		assertThat(firstCall).isEmpty();
		assertThat(secondCall).isEmpty();
		assertThat(thirdCall).isEmpty();
	}

	@Test
	void testGetVetListAfterModification() {
		List<Vet> vetList = vets.getVetList();
		Vet testVet = createTestVet(1, "Test", "Vet");
		
		// Modify the list
		vetList.add(testVet);
		vetList.remove(testVet);
		
		// List should still be the same instance but empty
		List<Vet> afterModification = vets.getVetList();
		assertThat(afterModification).isSameAs(vetList);
		assertThat(afterModification).isEmpty();
	}

	@Test
	void testVetsWithNullVetInList() {
		List<Vet> vetList = vets.getVetList();
		
		// Add null vet (edge case)
		vetList.add(null);
		
		assertThat(vetList).hasSize(1);
		assertThat(vetList.get(0)).isNull();
	}

	@Test
	void testVetsListBehaviorAfterClear() {
		List<Vet> vetList = vets.getVetList();
		
		// Add some vets
		vetList.add(createTestVet(1, "Vet1", "Test"));
		vetList.add(createTestVet(2, "Vet2", "Test"));
		
		assertThat(vetList).hasSize(2);
		
		// Clear the list
		vetList.clear();
		
		assertThat(vetList).isEmpty();
		
		// Subsequent calls should return the same empty list
		List<Vet> afterClear = vets.getVetList();
		assertThat(afterClear).isSameAs(vetList);
		assertThat(afterClear).isEmpty();
	}

	/**
	 * Helper method to create a test vet with basic information.
	 */
	private Vet createTestVet(int id, String firstName, String lastName) {
		Vet vet = new Vet();
		vet.setId(id);
		vet.setFirstName(firstName);
		vet.setLastName(lastName);
		return vet;
	}

}
