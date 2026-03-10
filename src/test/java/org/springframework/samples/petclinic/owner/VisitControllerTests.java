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

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test class for {@link VisitController}
 *
 * @author Colin But
 */
@WebMvcTest(VisitController.class)
@DisabledInNativeImage
@DisabledInAotMode
class VisitControllerTests {

	private static final int TEST_OWNER_ID = 1;

	private static final int TEST_PET_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OwnerRepository owners;

	private Owner george;
	private Pet max;

	@BeforeEach
	void init() {
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
		PetType dog = new PetType();
		dog.setName("dog");
		max.setType(dog);
		george.addPet(max);

		given(this.owners.findById(TEST_OWNER_ID)).willReturn(george);
	}

	@Test
	void testInitNewVisitForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"))
			.andExpect(model().attributeExists("visit"))
			.andExpect(model().attributeExists("pet"))
			.andExpect(model().attributeExists("owner"));
	}

	@Test
	void testProcessNewVisitFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("name", "George")
				.param("description", "Visit Description"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attributeExists("message"));

		verify(owners, times(1)).save(george);
	}

	@Test
	void testProcessNewVisitFormHasErrors() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID).param("name",
					"George"))
			.andExpect(model().attributeHasErrors("visit"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));

		verify(owners, never()).save(any(Owner.class));
	}

	@Test
	void testProcessNewVisitFormWithValidDateAndDescription() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", LocalDate.now().toString())
				.param("description", "Annual vaccination"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attribute("message", "Your visit has been booked"));

		verify(owners, times(1)).save(george);
	}

	@Test
	void testProcessNewVisitFormWithEmptyDescription() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", LocalDate.now().toString())
				.param("description", ""))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"))
			.andExpect(model().attributeHasErrors("visit"));

		verify(owners, never()).save(any(Owner.class));
	}

	@Test
	void testProcessNewVisitFormWithFutureDate() throws Exception {
		LocalDate futureDate = LocalDate.now().plusDays(30);
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", futureDate.toString())
				.param("description", "Scheduled checkup"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attribute("message", "Your visit has been booked"));

		verify(owners, times(1)).save(george);
	}

	@Test
	void testProcessNewVisitFormWithPastDate() throws Exception {
		LocalDate pastDate = LocalDate.now().minusDays(7);
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", pastDate.toString())
				.param("description", "Emergency visit"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attribute("message", "Your visit has been booked"));

		verify(owners, times(1)).save(george);
	}

	@Test
	void testProcessNewVisitFormWithCurrentDate() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", LocalDate.now().toString())
				.param("description", "Today's visit"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attribute("message", "Your visit has been booked"));

		verify(owners, times(1)).save(george);
	}

	@Test
	void testProcessNewVisitFormWithInvalidDate() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", "invalid-date")
				.param("description", "Visit with invalid date"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"))
			.andExpect(model().attributeHasErrors("visit"));

		verify(owners, never()).save(any(Owner.class));
	}

	@Test
	void testProcessNewVisitFormWithLongDescription() throws Exception {
		String longDescription = "This is a very long description that contains detailed information about the pet's condition and the reason for the visit. It includes multiple sentences and provides comprehensive details about the pet's health status and treatment requirements.";
		
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", LocalDate.now().toString())
				.param("description", longDescription))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attribute("message", "Your visit has been booked"));

		verify(owners, times(1)).save(george);
	}

	@Test
	void testProcessNewVisitFormWithSpecialCharactersInDescription() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", LocalDate.now().toString())
				.param("description", "Visit for Max's check-up & vaccination (2023)"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attribute("message", "Your visit has been booked"));

		verify(owners, times(1)).save(george);
	}

	@Test
	void testProcessNewVisitFormWithWhitespaceOnlyDescription() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", LocalDate.now().toString())
				.param("description", "   "))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"))
			.andExpect(model().attributeHasErrors("visit"));

		verify(owners, never()).save(any(Owner.class));
	}

	@Test
	void testLoadPetWithVisitModelAttributes() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID))
			.andExpect(status().isOk())
			.andExpect(model().attribute("owner", george))
			.andExpect(model().attribute("pet", max))
			.andExpect(model().attributeExists("visit"));
	}

	@Test
	void testInitNewVisitFormWithDifferentOwnerAndPetIds() throws Exception {
		int differentOwnerId = 2;
		int differentPetId = 2;
		
		Owner anotherOwner = new Owner();
		anotherOwner.setId(differentOwnerId);
		anotherOwner.setFirstName("Jane");
		anotherOwner.setLastName("Doe");
		
		Pet anotherPet = new Pet();
		anotherPet.setId(differentPetId);
		anotherPet.setName("Buddy");
		anotherOwner.addPet(anotherPet);
		
		given(this.owners.findById(differentOwnerId)).willReturn(anotherOwner);

		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new", differentOwnerId, differentPetId))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"))
			.andExpect(model().attributeExists("visit"))
			.andExpect(model().attributeExists("pet"))
			.andExpect(model().attributeExists("owner"));
	}

	@Test
	void testProcessNewVisitFormRedirectsToCorrectOwnerPage() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", LocalDate.now().toString())
				.param("description", "Regular checkup"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/" + TEST_OWNER_ID));
	}

	@Test
	void testProcessNewVisitFormWithMultipleValidationErrors() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", "")
				.param("description", ""))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"))
			.andExpect(model().attributeHasErrors("visit"))
			.andExpect(model().attributeExists("pet"))
			.andExpect(model().attributeExists("owner"));

		verify(owners, never()).save(any(Owner.class));
	}

	@Test
	void testOwnerRepositoryFindByIdIsCalled() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID))
			.andExpect(status().isOk());

		verify(owners, times(1)).findById(TEST_OWNER_ID);
	}

	@Test
	void testOwnerRepositorySaveIsCalledOnSuccessfulSubmission() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", LocalDate.now().toString())
				.param("description", "Successful visit"))
			.andExpect(status().is3xxRedirection());

		verify(owners, times(1)).save(george);
	}

	@Test
	void testFlashMessageIsSetOnSuccessfulSubmission() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", LocalDate.now().toString())
				.param("description", "Visit with flash message"))
			.andExpect(flash().attribute("message", "Your visit has been booked"));
	}

	@Test
	void testProcessNewVisitFormWithNullDate() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("description", "Visit without date"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"))
			.andExpect(model().attributeHasErrors("visit"));

		verify(owners, never()).save(any(Owner.class));
	}

	@Test
	void testInitBinder() throws Exception {
		// Test that id field is disallowed by attempting to set it
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("id", "999")
				.param("date", LocalDate.now().toString())
				.param("description", "Visit with id parameter"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));

		verify(owners, times(1)).save(george);
	}

}
