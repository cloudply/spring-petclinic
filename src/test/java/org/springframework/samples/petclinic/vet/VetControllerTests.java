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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
		List<Vet> allVets = Lists.newArrayList(james(), helen(), linda());
		given(this.vets.findAll()).willReturn(allVets);
		
		// Setup pagination - page size is 5 in controller
		given(this.vets.findAll(PageRequest.of(0, 5)))
			.willReturn(new PageImpl<>(allVets, PageRequest.of(0, 5), 3));
		given(this.vets.findAll(PageRequest.of(1, 5)))
			.willReturn(new PageImpl<>(new ArrayList<>(), PageRequest.of(1, 5), 3));
		given(this.vets.findAll(any(Pageable.class)))
			.willReturn(new PageImpl<>(Lists.newArrayList(james(), helen())));
	}

	@Test
	void testShowVetListHtml() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/vets.html?page=1"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("listVets"))
			.andExpect(model().attributeExists("currentPage"))
			.andExpect(model().attributeExists("totalPages"))
			.andExpect(model().attributeExists("totalItems"))
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	void testShowVetListHtmlDefaultPage() throws Exception {
		mockMvc.perform(get("/vets.html"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("listVets"))
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
	void testShowVetListHtmlWithZeroPage() throws Exception {
		mockMvc.perform(get("/vets.html").param("page", "0"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("listVets"))
			.andExpect(model().attribute("currentPage", 0))
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	void testShowVetListHtmlWithNegativePage() throws Exception {
		mockMvc.perform(get("/vets.html").param("page", "-1"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("listVets"))
			.andExpect(model().attribute("currentPage", -1))
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	void testShowVetListHtmlPaginationAttributes() throws Exception {
		mockMvc.perform(get("/vets.html").param("page", "1"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("currentPage", 1))
			.andExpect(model().attributeExists("totalPages"))
			.andExpect(model().attributeExists("totalItems"))
			.andExpect(model().attributeExists("listVets"));
	}

	@Test
	void testShowResourcesVetList() throws Exception {
		ResultActions actions = mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		actions.andExpected(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.vetList[0].id").value(1))
			.andExpect(jsonPath("$.vetList[0].firstName").value("James"))
			.andExpect(jsonPath("$.vetList[0].lastName").value("Carter"));
	}

	@Test
	void testShowResourcesVetListWithXmlAccept() throws Exception {
		mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_XML))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_XML));
	}

	@Test
	void testShowResourcesVetListJsonStructure() throws Exception {
		mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.vetList").isArray())
			.andExpect(jsonPath("$.vetList", hasSize(3)))
			.andExpect(jsonPath("$.vetList[0].id").value(1))
			.andExpect(jsonPath("$.vetList[1].id").value(2))
			.andExpect(jsonPath("$.vetList[2].id").value(3));
	}

	@Test
	void testShowResourcesVetListWithSpecialties() throws Exception {
		mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.vetList[1].specialties").isArray())
			.andExpect(jsonPath("$.vetList[1].specialties[0].name").value("radiology"))
			.andExpect(jsonPath("$.vetList[2].specialties", hasSize(2)))
			.andExpect(jsonPath("$.vetList[2].specialties[0].name").value("surgery"))
			.andExpect(jsonPath("$.vetList[2].specialties[1].name").value("dentistry"));
	}

	@Test
	void testShowResourcesVetListWithoutSpecialties() throws Exception {
		mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.vetList[0].specialties").isEmpty());
	}

	@Test
	void testShowResourcesVetListWithEmptyRepository() throws Exception {
		given(this.vets.findAll()).willReturn(new ArrayList<>());
		
		mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.vetList").isArray())
			.andExpect(jsonPath("$.vetList").isEmpty());
	}

	@Test
	void testShowVetListHtmlWithEmptyRepository() throws Exception {
		given(this.vets.findAll(any(Pageable.class)))
			.willReturn(new PageImpl<>(new ArrayList<>(), PageRequest.of(0, 5), 0));
		
		mockMvc.perform(get("/vets.html"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("listVets", hasSize(0)))
			.andExpect(model().attribute("totalItems", 0L))
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	void testShowResourcesVetListDefaultContentType() throws Exception {
		mockMvc.perform(get("/vets"))
			.andExpect(status().isOk());
	}

	@Test
	void testShowVetListHtmlWithLargePageNumber() throws Exception {
		given(this.vets.findAll(PageRequest.of(999, 5)))
			.willReturn(new PageImpl<>(new ArrayList<>(), PageRequest.of(999, 5), 3));
		
		mockMvc.perform(get("/vets.html").param("page", "1000"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("currentPage", 1000))
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	void testVetListPaginationCalculation() throws Exception {
		// Test that pagination is correctly calculated
		given(this.vets.findAll(PageRequest.of(0, 5)))
			.willReturn(new PageImpl<>(Lists.newArrayList(james(), helen()), PageRequest.of(0, 5), 10));
		
		mockMvc.perform(get("/vets.html").param("page", "1"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("currentPage", 1))
			.andExpect(model().attribute("totalPages", 2))
			.andExpect(model().attribute("totalItems", 10L))
			.andExpect(model().attribute("listVets", hasSize(2)));
	}

	@Test
	void testVetResourceResponseHeaders() throws Exception {
		mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(header().string("Content-Type", "application/json"));
	}

	@Test
	void testVetListModelAttributeTypes() throws Exception {
		mockMvc.perform(get("/vets.html"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("currentPage", is(1)))
			.andExpect(model().attributeExists("totalPages"))
			.andExpect(model().attributeExists("totalItems"))
			.andExpect(model().attributeExists("listVets"));
	}
}
