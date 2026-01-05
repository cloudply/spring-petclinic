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
	private VetRepository vetRepository;

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

	@BeforeEach
	void setup() {
		given(this.vetRepository.findAll()).willReturn(Lists.newArrayList(james(), helen()));
		given(this.vetRepository.findAll(any(Pageable.class)))
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
	void testShowVetListWithPagination() throws Exception {
		mockMvc.perform(get("/vets.html").param("page", "2"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("currentPage"))
			.andExpect(model().attribute("currentPage", 2))
			.andExpect(model().attributeExists("totalPages"))
			.andExpect(model().attributeExists("totalItems"))
			.andExpect(model().attributeExists("listVets"))
			.andExpect(view().name("vets/vetList"));
	}
	
	@Test
	void testShowVetListWithDefaultPagination() throws Exception {
		mockMvc.perform(get("/vets.html"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("currentPage"))
			.andExpect(model().attribute("currentPage", 1))
			.andExpect(model().attributeExists("listVets"))
			.andExpect(view().name("vets/vetList"));
	}
	
	@Test
	void testShowVetListWithEmptyRepository() throws Exception {
		given(this.vetRepository.findAll(any(Pageable.class)))
			.willReturn(new PageImpl<>(List.of()));
		
		mockMvc.perform(get("/vets.html"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("listVets"))
			.andExpect(model().attribute("listVets", hasSize(0)))
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	void testShowResourcesVetList() throws Exception {
		ResultActions actions = mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		actions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.vetList[0].id").value(1))
			.andExpect(jsonPath("$.vetList[0].firstName").value("James"))
			.andExpect(jsonPath("$.vetList[0].lastName").value("Carter"))
			.andExpect(jsonPath("$.vetList[1].id").value(2))
			.andExpect(jsonPath("$.vetList[1].firstName").value("Helen"))
			.andExpect(jsonPath("$.vetList[1].lastName").value("Leary"))
			.andExpect(jsonPath("$.vetList[1].specialties[0].name").value("radiology"));
	}
	
	@Test
	void testShowResourcesVetListWithEmptyRepository() throws Exception {
		given(this.vetRepository.findAll()).willReturn(List.of());
		
		mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.vetList").isArray())
			.andExpect(jsonPath("$.vetList").isEmpty());
	}
	
	@Test
	void testShowResourcesVetListWithMultipleSpecialties() throws Exception {
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
		
		given(this.vetRepository.findAll()).willReturn(List.of(linda));
		
		mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.vetList[0].id").value(3))
			.andExpect(jsonPath("$.vetList[0].firstName").value("Linda"))
			.andExpect(jsonPath("$.vetList[0].lastName").value("Douglas"))
			.andExpect(jsonPath("$.vetList[0].specialties", hasSize(2)))
			.andExpect(jsonPath("$.vetList[0].specialties[0].name").value("surgery"))
			.andExpect(jsonPath("$.vetList[0].specialties[1].name").value("dentistry"));
	}

}
