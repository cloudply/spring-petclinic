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

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the {@link VetController}
 */

@WebMvcTest(VetController.class)
@DisabledInNativeImage
@DisabledInAotMode
class VetControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private VetRepository vets;

	private Vet james() {
		Vet james = new Vet();
		james.setFirstName("James");
		james.setLastName("Carter");
		james.setId(1);
		return james;
	}

	private Vet helen() {
		Vet helen = new Vet();
		helen.setFirstName("Helen");
		helen.setLastName("Leary");
		helen.setId(2);
		Specialty radiology = new Specialty();
		radiology.setId(1);
		radiology.setName("radiology");
		helen.addSpecialty(radiology);
		return helen;
	}

	private Vet linda() {
		Vet linda = new Vet();
		linda.setFirstName("Linda");
		linda.setLastName("Douglas");
		linda.setId(3);
		Specialty surgery = new Specialty();
		surgery.setId(2);
		surgery.setName("surgery");
		linda.addSpecialty(surgery);
		Specialty dentistry = new Specialty();
		dentistry.setId(3);
		dentistry.setName("dentistry");
		linda.addSpecialty(dentistry);
		return linda;
	}

	@BeforeEach
	void setup() {
		given(this.vets.findAll()).willReturn(Lists.newArrayList(james(), helen()));
		given(this.vets.findAll(any(Pageable.class)))
			.willReturn(new PageImpl<Vet>(Lists.newArrayList(james(), helen())));

	}

	@Test
	void testShowVetListHtml() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get("/vets.html?page=1"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("listVets"))
			.andExpect(view().name("vets/vetList"));

	}

	@Test
	void testShowResourcesVetList() throws Exception {
		ResultActions actions = mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		actions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.vetList[0].id").value(1));
	}

	@Test
	void testShowVetListHtmlWithDefaultPage() throws Exception {
		mockMvc.perform(get("/vets.html"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("listVets"))
			.andExpect(model().attributeExists("currentPage"))
			.andExpect(model().attributeExists("totalPages"))
			.andExpect(model().attributeExists("totalItems"))
			.andExpect(model().attribute("currentPage", 1))
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	void testShowVetListHtmlWithSpecificPage() throws Exception {
		mockMvc.perform(get("/vets.html").param("page", "2"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("listVets"))
			.andExpect(model().attribute("currentPage", 2))
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	void testShowVetListHtmlWithInvalidPageParameter() throws Exception {
		mockMvc.perform(get("/vets.html").param("page", "invalid"))
			.andExpect(status().isBadRequest());
	}

	@Test
	void testShowVetListHtmlWithNegativePage() throws Exception {
		mockMvc.perform(get("/vets.html").param("page", "-1"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("listVets"))
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	void testShowVetListHtmlWithZeroPage() throws Exception {
		mockMvc.perform(get("/vets.html").param("page", "0"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("listVets"))
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	void testShowVetListHtmlWithEmptyResults() throws Exception {
		given(this.vets.findAll(any(Pageable.class)))
			.willReturn(new PageImpl<Vet>(Collections.emptyList()));

		mockMvc.perform(get("/vets.html"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("listVets"))
			.andExpect(model().attribute("totalItems", 0L))
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	void testShowVetListHtmlWithLargeDataset() throws Exception {
		List<Vet> largeVetList = new ArrayList<>();
		for (int i = 1; i <= 10; i++) {
			Vet vet = new Vet();
			vet.setId(i);
			vet.setFirstName("FirstName" + i);
			vet.setLastName("LastName" + i);
			largeVetList.add(vet);
		}

		given(this.vets.findAll(any(Pageable.class)))
			.willReturn(new PageImpl<Vet>(largeVetList.subList(0, 5), 
				org.springframework.data.domain.PageRequest.of(0, 5), largeVetList.size()));

		mockMvc.perform(get("/vets.html"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("listVets"))
			.andExpect(model().attribute("totalItems", 10L))
			.andExpect(model().attribute("totalPages", 2))
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	void testShowResourcesVetListWithEmptyResults() throws Exception {
		given(this.vets.findAll()).willReturn(Collections.emptyList());

		mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.vetList", hasSize(0)));
	}

	@Test
	void testShowResourcesVetListWithMultipleVets() throws Exception {
		given(this.vets.findAll()).willReturn(Lists.newArrayList(james(), helen(), linda()));

		mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.vetList", hasSize(3)))
			.andExpect(jsonPath("$.vetList[0].id").value(1))
			.andExpect(jsonPath("$.vetList[0].firstName").value("James"))
			.andExpected(jsonPath("$.vetList[0].lastName").value("Carter"))
			.andExpect(jsonPath("$.vetList[1].id").value(2))
			.andExpect(jsonPath("$.vetList[1].firstName").value("Helen"))
			.andExpect(jsonPath("$.vetList[1].lastName").value("Leary"))
			.andExpect(jsonPath("$.vetList[2].id").value(3))
			.andExpect(jsonPath("$.vetList[2].firstName").value("Linda"))
			.andExpect(jsonPath("$.vetList[2].lastName").value("Douglas"));
	}

	@Test
	void testShowResourcesVetListWithSpecialties() throws Exception {
		given(this.vets.findAll()).willReturn(Lists.newArrayList(helen(), linda()));

		mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.vetList", hasSize(2)))
			.andExpect(jsonPath("$.vetList[0].specialties", hasSize(1)))
			.andExpect(jsonPath("$.vetList[0].specialties[0].name").value("radiology"))
			.andExpect(jsonPath("$.vetList[1].specialties", hasSize(2)));
	}

	@Test
	void testShowResourcesVetListWithXmlAcceptHeader() throws Exception {
		mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_XML))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_XML));
	}

	@Test
	void testShowResourcesVetListWithTextHtmlAcceptHeader() throws Exception {
		mockMvc.perform(get("/vets").accept(MediaType.TEXT_HTML))
			.andExpect(status().isOk());
	}

	@Test
	void testShowVetListHtmlPaginationAttributes() throws Exception {
		List<Vet> vetList = Lists.newArrayList(james(), helen());
		given(this.vets.findAll(any(Pageable.class)))
			.willReturn(new PageImpl<Vet>(vetList, 
				org.springframework.data.domain.PageRequest.of(1, 5), 15));

		mockMvc.perform(get("/vets.html").param("page", "2"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("currentPage", 2))
			.andExpect(model().attribute("totalPages", 3))
			.andExpect(model().attribute("totalItems", 15L))
			.andExpect(model().attributeExists("listVets"))
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	void testShowVetListHtmlWithVeryHighPageNumber() throws Exception {
		mockMvc.perform(get("/vets.html").param("page", "999"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("listVets"))
			.andExpect(model().attribute("currentPage", 999))
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	void testVetControllerHandlesRepositoryException() throws Exception {
		given(this.vets.findAll(any(Pageable.class)))
			.willThrow(new RuntimeException("Database connection failed"));

		mockMvc.perform(get("/vets.html"))
			.andExpect(status().is5xxServerError());
	}

	@Test
	void testShowResourcesVetListHandlesRepositoryException() throws Exception {
		given(this.vets.findAll())
			.willThrow(new RuntimeException("Database connection failed"));

		mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().is5xxServerError());
	}
}
