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
	void testShowVetListWithSpecificPage() throws Exception {
		Page<Vet> vetPage = new PageImpl<>(Lists.newArrayList(james(), helen()));
		given(this.vets.findAll(any(Pageable.class))).willReturn(vetPage);
		
		mockMvc.perform(get("/vets.html").param("page", "2"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("currentPage", 2))
			.andExpect(model().attributeExists("totalPages"))
			.andExpect(model().attributeExists("totalItems"))
			.andExpect(model().attributeExists("listVets"))
			.andExpect(view().name("vets/vetList"));
	}
	
	@Test
	void testShowVetListWithEmptyList() throws Exception {
		Page<Vet> emptyPage = new PageImpl<>(Lists.newArrayList());
		given(this.vets.findAll(any(Pageable.class))).willReturn(emptyPage);
		
		mockMvc.perform(get("/vets.html"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("listVets", hasSize(0)))
			.andExpect(view().name("vets/vetList"));
	}
	
	@Test
	void testShowResourcesVetListWithEmptyList() throws Exception {
		given(this.vets.findAll()).willReturn(Lists.newArrayList());
		
		mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.vetList", hasSize(0)));
	}
	
	@Test
	void testShowResourcesVetListWithFullDetails() throws Exception {
		given(this.vets.findAll()).willReturn(Lists.newArrayList(james(), helen()));
		
		mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.vetList[0].firstName").value("James"))
			.andExpect(jsonPath("$.vetList[0].lastName").value("Carter"))
			.andExpect(jsonPath("$.vetList[0].specialties").isEmpty())
			.andExpect(jsonPath("$.vetList[1].firstName").value("Helen"))
			.andExpect(jsonPath("$.vetList[1].lastName").value("Leary"))
			.andExpect(jsonPath("$.vetList[1].specialties[0].name").value("radiology"));
	}

}
