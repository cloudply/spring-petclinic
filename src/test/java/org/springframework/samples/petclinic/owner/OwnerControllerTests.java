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

package org.springframework.samples.petclinic.owner;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Test class for {@link OwnerController}
 *
 * @author Colin But
 */
@WebMvcTest(OwnerController.class)
@DisabledInNativeImage
@DisabledInAotMode
class OwnerControllerTests {

	private static final int TEST_OWNER_ID = 1;
	private static final String LAST_NAME_BLOGGS = "Bloggs";
	private static final String LAST_NAME = "lastName";
	private static final String ADDRESS = "address";
	private static final String CITY_LONDON = "London";
	private static final String CITY_MADISON = "Madison";
	private static final String FIRST_NAME = "firstName";
	private static final String TELEPHONE = "telephone";
	private static final String OWNER = "owner";
	private static final String LAST_NAME_FRANKLIN = "Franklin";
	private static final String OWNERS_NEW_PATH = "/owners/new";
	private static final String OWNERS_PAGE_PATH = "/owners?page=1";
	private static final String OWNERS_EDIT_PATH = "/owners/{ownerId}/edit";
	private static final String FIRST_NAME_GEORGE = "George";
	private static final String LIBERTY_ADDRESS = "110 W. Liberty St.";
	private static final String TELEPHONE_NUMBER = "6085551023";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OwnerRepository owners;

	private Owner george() {
		Owner george = new Owner();
		george.setId(TEST_OWNER_ID);
		george.setFirstName(FIRST_NAME_GEORGE);
		george.setLastName(LAST_NAME_FRANKLIN);
		george.setAddress(LIBERTY_ADDRESS);
		george.setCity(CITY_MADISON);
		george.setTelephone(TELEPHONE_NUMBER);
		Pet max = new Pet();
		PetType dog = new PetType();
		dog.setName("dog");
		max.setType(dog);
		max.setName("Max");
		max.setBirthDate(LocalDate.now());
		george.addPet(max);
		max.setId(1);
		return george;
	}

	@BeforeEach
	void setup() {

		Owner george = george();
		given(this.owners.findByLastName(eq(LAST_NAME_FRANKLIN), any(Pageable.class)))
			.willReturn(new PageImpl<>(Lists.newArrayList(george)));

		given(this.owners.findAll(any(Pageable.class))).willReturn(new PageImpl<>(Lists.newArrayList(george)));

		given(this.owners.findById(TEST_OWNER_ID)).willReturn(george);
		Visit visit = new Visit();
		visit.setDate(LocalDate.now());
		george.getPet("Max").getVisits().add(visit);

	}

	@Test
	void testInitCreationForm() throws Exception {
		mockMvc.perform(get(OWNERS_NEW_PATH))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists(OWNER))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessCreationFormSuccess() throws Exception {
		mockMvc
			.perform(post(OWNERS_NEW_PATH).param(FIRST_NAME, "Joe")
				.param("lastName", LAST_NAME_BLOGGS)
				.param(ADDRESS, "123 Caramel Street")
				.param("city", CITY_LONDON)
				.param(TELEPHONE, "1316761638"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	void testProcessCreationFormHasErrors() throws Exception {
		mockMvc
			.perform(post(OWNERS_NEW_PATH).param(FIRST_NAME, "Joe").param("lastName", LAST_NAME_BLOGGS).param("city", CITY_LONDON))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors(OWNER))
			.andExpect(model().attributeHasFieldErrors(OWNER, ADDRESS))
			.andExpect(model().attributeHasFieldErrors(OWNER, TELEPHONE))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testInitFindForm() throws Exception {
		mockMvc.perform(get("/owners/find"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists(OWNER))
			.andExpect(view().name("owners/findOwners"));
	}

	@Test
	void testProcessFindFormSuccess() throws Exception {
		Page<Owner> tasks = new PageImpl<>(Lists.newArrayList(george(), new Owner()));
		Mockito.when(this.owners.findByLastName(anyString(), any(Pageable.class))).thenReturn(tasks);
		mockMvc.perform(get(OWNERS_PAGE_PATH)).andExpect(status().isOk()).andExpect(view().name("owners/ownersList"));
	}

	@Test
	void testProcessFindFormByLastName() throws Exception {
		Page<Owner> tasks = new PageImpl<>(Lists.newArrayList(george()));
		Mockito.when(this.owners.findByLastName(eq(LAST_NAME_FRANKLIN), any(Pageable.class))).thenReturn(tasks);
		mockMvc.perform(get(OWNERS_PAGE_PATH).param(LAST_NAME, "Franklin"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));
	}

	@Test
	void testProcessFindFormNoOwnersFound() throws Exception {
		Page<Owner> tasks = new PageImpl<>(Lists.newArrayList());
		Mockito.when(this.owners.findByLastName(eq("Unknown Surname"), any(Pageable.class))).thenReturn(tasks);
		mockMvc.perform(get(OWNERS_PAGE_PATH).param(LAST_NAME, "Unknown Surname"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors(OWNER, LAST_NAME))
			.andExpect(model().attributeHasFieldErrorCode(OWNER, LAST_NAME, "notFound"))
			.andExpect(view().name("owners/findOwners"));

	}

	@Test
	void testInitUpdateOwnerForm() throws Exception {
		mockMvc.perform(get(OWNERS_EDIT_PATH, TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists(OWNER))
			.andExpect(model().attribute(OWNER, hasProperty(LAST_NAME, is(LAST_NAME_FRANKLIN))))
			.andExpect(model().attribute(OWNER, hasProperty(FIRST_NAME, is(FIRST_NAME_GEORGE))))
			.andExpect(model().attribute(OWNER, hasProperty(ADDRESS, is(LIBERTY_ADDRESS))))
			.andExpect(model().attribute(OWNER, hasProperty("city", is(CITY_MADISON))))
			.andExpect(model().attribute(OWNER, hasProperty(TELEPHONE, is(TELEPHONE_NUMBER))))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessUpdateOwnerFormSuccess() throws Exception {
		mockMvc
			.perform(post(OWNERS_EDIT_PATH, TEST_OWNER_ID).param(FIRST_NAME, "Joe")
				.param("lastName", LAST_NAME_BLOGGS)
				.param(ADDRESS, "123 Caramel Street")
				.param("city", CITY_LONDON)
				.param(TELEPHONE, "1616291589"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void testProcessUpdateOwnerFormUnchangedSuccess() throws Exception {
		mockMvc.perform(post(OWNERS_EDIT_PATH, TEST_OWNER_ID))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void testProcessUpdateOwnerFormHasErrors() throws Exception {
		mockMvc
			.perform(post(OWNERS_EDIT_PATH, TEST_OWNER_ID).param("firstName", "Joe")
				.param("lastName", LAST_NAME_BLOGGS)
				.param(ADDRESS, "")
				.param(TELEPHONE, ""))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors(OWNER))
			.andExpect(model().attributeHasFieldErrors(OWNER, ADDRESS))
			.andExpect(model().attributeHasFieldErrors(OWNER, TELEPHONE))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testShowOwner() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}", TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpect(model().attribute(OWNER, hasProperty(LAST_NAME, is(LAST_NAME_FRANKLIN))))
			.andExpect(model().attribute(OWNER, hasProperty(FIRST_NAME, is(FIRST_NAME_GEORGE))))
			.andExpect(model().attribute(OWNER, hasProperty(ADDRESS, is(LIBERTY_ADDRESS))))
			.andExpect(model().attribute(OWNER, hasProperty("city", is(CITY_MADISON))))
			.andExpect(model().attribute(OWNER, hasProperty("telephone", is(TELEPHONE_NUMBER))))
			.andExpect(model().attribute(OWNER, hasProperty("pets", not(empty()))))
			.andExpect(model().attribute(OWNER,
					hasProperty("pets", hasItem(hasProperty("visits", hasSize(greaterThan(0)))))))
			.andExpect(view().name("owners/ownerDetails"));
	}

}
