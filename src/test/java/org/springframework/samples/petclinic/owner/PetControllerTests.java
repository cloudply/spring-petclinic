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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

/**
 * Test class for the {@link PetController}
 *
 * @author Colin But
 */
@WebMvcTest(value = PetController.class,
		includeFilters = @ComponentScan.Filter(value = PetTypeFormatter.class, type = FilterType.ASSIGNABLE_TYPE))
@DisabledInNativeImage
@DisabledInAotMode
class PetControllerTests {

	private static final int TEST_OWNER_ID = 1;

	private static final int TEST_PET_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OwnerRepository owners;

	private Owner george;
	private PetType cat;
	private Pet max;

	@BeforeEach
	void setup() {
		cat = new PetType();
		cat.setId(3);
		cat.setName("hamster");
		
		george = new Owner();
		george.setId(TEST_OWNER_ID);
		george.setFirstName("George");
		george.setLastName("Franklin");
		george.setAddress("110 W. Liberty St.");
		george.setCity("Madison");
		george.setTelephone("6085551023");
		
		max = new Pet();
		max.setId(TEST_PET_ID);
		max.setName("Max");
		max.setBirthDate(LocalDate.now().minusYears(2));
		max.setType(cat);
		george.addPet(max);

		given(this.owners.findPetTypes()).willReturn(Lists.newArrayList(cat));
		given(this.owners.findById(TEST_OWNER_ID)).willReturn(george);
	}

	@Test
	void testInitCreationForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/new", TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpected(view().name("pets/createOrUpdatePetForm"))
			.andExpect(model().attributeExists("pet"))
			.andExpect(model().attributeExists("owner"))
			.andExpect(model().attributeExists("types"));
	}

	@Test
	void testProcessCreationFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID).param("name", "Betty")
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attributeExists("message"));
		
		verify(owners).save(any(Owner.class));
	}

	@Test
	void testProcessCreationFormHasErrors() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID).param("name", "Betty")
				.param("birthDate", "2015-02-12"))
			.andExpect(model().attributeHasNoErrors("owner"))
			.andExpect(model().attributeHasErrors("pet"))
			.andExpect(model().attributeHasFieldErrors("pet", "type"))
			.andExpect(model().attributeHasFieldErrorCode("pet", "type", "required"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testProcessCreationFormDuplicatePetName() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID).param("name", "Max")
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(model().attributeHasErrors("pet"))
			.andExpect(model().attributeHasFieldErrors("pet", "name"))
			.andExpect(model().attributeHasFieldErrorCode("pet", "name", "duplicate"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testProcessCreationFormFutureBirthDate() throws Exception {
		LocalDate futureDate = LocalDate.now().plusDays(1);
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID).param("name", "Betty")
				.param("type", "hamster")
				.param("birthDate", futureDate.toString()))
			.andExpect(model().attributeHasErrors("pet"))
			.andExpect(model().attributeHasFieldErrors("pet", "birthDate"))
			.andExpect(model().attributeHasFieldErrorCode("pet", "birthDate", "typeMismatch.birthDate"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testProcessCreationFormWithTodayBirthDate() throws Exception {
		LocalDate today = LocalDate.now();
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID).param("name", "Betty")
				.param("type", "hamster")
				.param("birthDate", today.toString()))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attributeExists("message"));
		
		verify(owners).save(any(Owner.class));
	}

	@Test
	void testProcessCreationFormWithNullBirthDate() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID).param("name", "Betty")
				.param("type", "hamster"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attributeExists("message"));
		
		verify(owners).save(any(Owner.class));
	}

	@Test
	void testInitUpdateForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("pet"))
			.andExpect(model().attributeExists("owner"))
			.andExpect(model().attributeExists("types"))
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testProcessUpdateFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID).param("name", "Betty")
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attributeExists("message"));
		
		verify(owners).save(any(Owner.class));
	}

	@Test
	void testProcessUpdateFormHasErrors() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID).param("name", "Betty")
				.param("birthDate", "2015/02/12"))
			.andExpect(model().attributeHasNoErrors("owner"))
			.andExpect(model().attributeHasErrors("pet"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testProcessUpdateFormDuplicatePetName() throws Exception {
		// Add another pet to the owner
		Pet anotherPet = new Pet();
		anotherPet.setId(2);
		anotherPet.setName("Bella");
		anotherPet.setBirthDate(LocalDate.now().minusYears(1));
		anotherPet.setType(cat);
		george.addPet(anotherPet);

		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID).param("name", "Bella")
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(model().attributeHasErrors("pet"))
			.andExpect(model().attributeHasFieldErrors("pet", "name"))
			.andExpect(model().attributeHasFieldErrorCode("pet", "name", "duplicate"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testProcessUpdateFormSameName() throws Exception {
		// Test updating pet with same name should be allowed
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID).param("name", "Max")
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attributeExists("message"));
		
		verify(owners).save(any(Owner.class));
	}

	@Test
	void testProcessUpdateFormFutureBirthDate() throws Exception {
		LocalDate futureDate = LocalDate.now().plusDays(1);
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID).param("name", "Betty")
				.param("type", "hamster")
				.param("birthDate", futureDate.toString()))
			.andExpect(model().attributeHasErrors("pet"))
			.andExpect(model().attributeHasFieldErrors("pet", "birthDate"))
			.andExpect(model().attributeHasFieldErrorCode("pet", "birthDate", "typeMismatch.birthDate"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testProcessUpdateFormWithTodayBirthDate() throws Exception {
		LocalDate today = LocalDate.now();
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID).param("name", "Betty")
				.param("type", "hamster")
				.param("birthDate", today.toString()))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attributeExists("message"));
		
		verify(owners).save(any(Owner.class));
	}

	@Test
	void testProcessUpdateFormWithNullBirthDate() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID).param("name", "Betty")
				.param("type", "hamster"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attributeExists("message"));
		
		verify(owners).save(any(Owner.class));
	}

	@Test
	void testOwnerNotFoundForCreation() throws Exception {
		given(this.owners.findById(anyInt())).willReturn(null);
		
		mockMvc.perform(get("/owners/{ownerId}/pets/new", 999))
			.andExpect(status().is5xxServerError());
	}

	@Test
	void testOwnerNotFoundForUpdate() throws Exception {
		given(this.owners.findById(anyInt())).willReturn(null);
		
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/edit", 999, TEST_PET_ID))
			.andExpect(status().is5xxServerError());
	}

	@Test
	void testFindOwnerModelAttribute() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/new", TEST_OWNER_ID))
			.andExpect(model().attribute("owner", george));
	}

	@Test
	void testPopulatePetTypesModelAttribute() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/new", TEST_OWNER_ID))
			.andExpect(model().attribute("types", Lists.newArrayList(cat)));
	}

	@Test
	void testFindPetModelAttributeNewPet() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/new", TEST_OWNER_ID))
			.andExpect(model().attributeExists("pet"));
	}

	@Test
	void testFindPetModelAttributeExistingPet() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID))
			.andExpect(model().attribute("pet", max));
	}

	@Test
	void testProcessCreationFormEmptyName() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID).param("name", "")
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(model().attributeHasErrors("pet"))
			.andExpect(model().attributeHasFieldErrors("pet", "name"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testProcessUpdateFormEmptyName() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID).param("name", "")
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(model().attributeHasErrors("pet"))
			.andExpect(model().attributeHasFieldErrors("pet", "name"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testProcessCreationFormCaseInsensitiveDuplicateName() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID).param("name", "max")
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(model().attributeHasErrors("pet"))
			.andExpect(model().attributeHasFieldErrors("pet", "name"))
			.andExpect(model().attributeHasFieldErrorCode("pet", "name", "duplicate"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testProcessUpdateFormCaseInsensitiveDuplicateName() throws Exception {
		// Add another pet to the owner
		Pet anotherPet = new Pet();
		anotherPet.setId(2);
		anotherPet.setName("Bella");
		anotherPet.setBirthDate(LocalDate.now().minusYears(1));
		anotherPet.setType(cat);
		george.addPet(anotherPet);

		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID).param("name", "bella")
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(model().attributeHasErrors("pet"))
			.andExpect(model().attributeHasFieldErrors("pet", "name"))
			.andExpect(model().attributeHasFieldErrorCode("pet", "name", "duplicate"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

}
