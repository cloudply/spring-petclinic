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
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;

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

	@BeforeEach
	void setup() {
		PetType cat = new PetType();
		cat.setId(3);
		cat.setName("hamster");
		given(this.owners.findPetTypes()).willReturn(Lists.newArrayList(cat));
		Owner owner = new Owner();
		Pet pet = new Pet();
		owner.addPet(pet);
		pet.setId(TEST_PET_ID);
		given(this.owners.findById(TEST_OWNER_ID)).willReturn(owner);
	}

	@Test
	void testInitCreationForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/new", TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpected(view().name("pets/createOrUpdatePetForm"))
			.andExpect(model().attributeExists("pet"));
	}

	@Test
	void testProcessCreationFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID).param("name", "Betty")
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
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
	void testInitUpdateForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("pet"))
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testProcessUpdateFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID).param("name", "Betty")
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
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
	void testProcessCreationFormWithDuplicateName() throws Exception {
		Owner owner = new Owner();
		Pet existingPet = new Pet();
		existingPet.setName("Betty");
		existingPet.setId(2);
		owner.addPet(existingPet);
		given(this.owners.findById(TEST_OWNER_ID)).willReturn(owner);

		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
				.param("name", "Betty")
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(model().attributeHasErrors("pet"))
			.andExpect(model().attributeHasFieldErrors("pet", "name"))
			.andExpect(model().attributeHasFieldErrorCode("pet", "name", "duplicate"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testProcessUpdateFormWithDuplicateName() throws Exception {
		Owner owner = new Owner();
		Pet existingPet = new Pet();
		existingPet.setName("Existing");
		existingPet.setId(2);
		owner.addPet(existingPet);
		
		Pet petToUpdate = new Pet();
		petToUpdate.setId(TEST_PET_ID);
		petToUpdate.setName("Current");
		owner.addPet(petToUpdate);
		
		given(this.owners.findById(TEST_OWNER_ID)).willReturn(owner);

		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
				.param("name", "Existing")
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(model().attributeHasErrors("pet"))
			.andExpect(model().attributeHasFieldErrors("pet", "name"))
			.andExpect(model().attributeHasFieldErrorCode("pet", "name", "duplicate"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testProcessCreationFormWithFutureBirthDate() throws Exception {
		LocalDate futureDate = LocalDate.now().plusDays(1);
		
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
				.param("name", "Betty")
				.param("type", "hamster")
				.param("birthDate", futureDate.toString()))
			.andExpect(model().attributeHasErrors("pet"))
			.andExpect(model().attributeHasFieldErrors("pet", "birthDate"))
			.andExpect(model().attributeHasFieldErrorCode("pet", "birthDate", "typeMismatch.birthDate"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testProcessUpdateFormWithFutureBirthDate() throws Exception {
		LocalDate futureDate = LocalDate.now().plusDays(1);
		
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
				.param("name", "Betty")
				.param("type", "hamster")
				.param("birthDate", futureDate.toString()))
			.andExpect(model().attributeHasErrors("pet"))
			.andExpect(model().attributeHasFieldErrors("pet", "birthDate"))
			.andExpect(model().attributeHasFieldErrorCode("pet", "birthDate", "typeMismatch.birthDate"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testProcessCreationFormWithCurrentDate() throws Exception {
		LocalDate currentDate = LocalDate.now();
		
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
				.param("name", "Betty")
				.param("type", "hamster")
				.param("birthDate", currentDate.toString()))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attribute("message", "New Pet has been Added"));
	}

	@Test
	void testProcessUpdateFormWithCurrentDate() throws Exception {
		LocalDate currentDate = LocalDate.now();
		
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
				.param("name", "Betty")
				.param("type", "hamster")
				.param("birthDate", currentDate.toString()))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attribute("message", "Pet details has been edited"));
	}

	@Test
	void testProcessCreationFormWithPastBirthDate() throws Exception {
		LocalDate pastDate = LocalDate.now().minusYears(2);
		
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
				.param("name", "Betty")
				.param("type", "hamster")
				.param("birthDate", pastDate.toString()))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attribute("message", "New Pet has been Added"));
	}

	@Test
	void testProcessCreationFormWithNullBirthDate() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
				.param("name", "Betty")
				.param("type", "hamster"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpected(flash().attribute("message", "New Pet has been Added"));
	}

	@Test
	void testProcessUpdateFormWithNullBirthDate() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
				.param("name", "Betty")
				.param("type", "hamster"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attribute("message", "Pet details has been edited"));
	}

	@Test
	void testProcessCreationFormWithEmptyName() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
				.param("name", "")
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(model().attributeHasErrors("pet"))
			.andExpect(model().attributeHasFieldErrors("pet", "name"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testProcessUpdateFormWithEmptyName() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
				.param("name", "")
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(model().attributeHasErrors("pet"))
			.andExpect(model().attributeHasFieldErrors("pet", "name"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testProcessCreationFormWithWhitespaceOnlyName() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
				.param("name", "   ")
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(model().attributeHasErrors("pet"))
			.andExpect(model().attributeHasFieldErrors("pet", "name"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testProcessUpdateFormWithSameNameDifferentCase() throws Exception {
		Owner owner = new Owner();
		Pet existingPet = new Pet();
		existingPet.setName("betty");
		existingPet.setId(2);
		owner.addPet(existingPet);
		
		Pet petToUpdate = new Pet();
		petToUpdate.setId(TEST_PET_ID);
		petToUpdate.setName("Current");
		owner.addPet(petToUpdate);
		
		given(this.owners.findById(TEST_OWNER_ID)).willReturn(owner);

		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
				.param("name", "BETTY")
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(model().attributeHasErrors("pet"))
			.andExpect(model().attributeHasFieldErrors("pet", "name"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testProcessUpdateFormWithSameNameSamePet() throws Exception {
		Owner owner = new Owner();
		Pet petToUpdate = new Pet();
		petToUpdate.setId(TEST_PET_ID);
		petToUpdate.setName("Betty");
		owner.addPet(petToUpdate);
		
		given(this.owners.findById(TEST_OWNER_ID)).willReturn(owner);

		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
				.param("name", "Betty")
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attribute("message", "Pet details has been edited"));
	}

	@Test
	void testFindOwnerThrowsExceptionWhenOwnerNotFound() throws Exception {
		given(this.owners.findById(999)).willReturn(null);
		
		mockMvc.perform(get("/owners/{ownerId}/pets/new", 999))
			.andExpect(status().is5xxServerError());
	}

	@Test
	void testFindOwnerThrowsExceptionWhenOwnerNotFoundForUpdate() throws Exception {
		given(this.owners.findById(999)).willReturn(null);
		
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/edit", 999, TEST_PET_ID))
			.andExpect(status().is5xxServerError());
	}

	@Test
	void testFindPetReturnsNewPetWhenPetIdIsNull() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/new", TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("pet"))
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testFindPetReturnsExistingPetWhenPetIdProvided() throws Exception {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setId(TEST_PET_ID);
		pet.setName("TestPet");
		owner.addPet(pet);
		given(this.owners.findById(TEST_OWNER_ID)).willReturn(owner);

		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("pet"))
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testPopulatePetTypesIsCalledOnFormLoad() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/new", TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("types"));
		
		verify(owners, times(1)).findPetTypes();
	}

	@Test
	void testOwnerRepositorySaveIsCalledOnSuccessfulCreation() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
				.param("name", "Betty")
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(status().is3xxRedirection());
		
		verify(owners, times(1)).save(any(Owner.class));
	}

	@Test
	void testOwnerRepositorySaveIsCalledOnSuccessfulUpdate() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
				.param("name", "Betty")
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(status().is3xxRedirection());
		
		verify(owners, times(1)).save(any(Owner.class));
	}

	@Test
	void testOwnerRepositorySaveIsNotCalledOnValidationErrors() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
				.param("name", "Betty")
				.param("birthDate", "2015-02-12"))
			.andExpect(status().isOk());
		
		verify(owners, never()).save(any(Owner.class));
	}

	@Test
	void testOwnerRepositorySaveIsNotCalledOnUpdateValidationErrors() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
				.param("name", "Betty")
				.param("birthDate", "2015-02-12"))
			.andExpect(status().isOk());
		
		verify(owners, never()).save(any(Owner.class));
	}

	@Test
	void testModelAttributesArePopulatedCorrectly() throws Exception {
		PetType dog = new PetType();
		dog.setId(1);
		dog.setName("dog");
		
		PetType cat = new PetType();
		cat.setId(2);
		cat.setName("cat");
		
		List<PetType> petTypes = new ArrayList<>();
		petTypes.add(dog);
		petTypes.add(cat);
		
		given(this.owners.findPetTypes()).willReturn(petTypes);

		mockMvc.perform(get("/owners/{ownerId}/pets/new", TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("owner"))
			.andExpect(model().attributeExists("pet"))
			.andExpect(model().attributeExists("types"))
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testProcessCreationFormWithMultipleValidationErrors() throws Exception {
		LocalDate futureDate = LocalDate.now().plusDays(1);
		
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
				.param("name", "")
				.param("birthDate", futureDate.toString()))
			.andExpect(model().attributeHasErrors("pet"))
			.andExpect(model().attributeHasFieldErrors("pet", "name"))
			.andExpect(model().attributeHasFieldErrors("pet", "type"))
			.andExpect(model().attributeHasFieldErrors("pet", "birthDate"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testProcessUpdateFormWithMultipleValidationErrors() throws Exception {
		LocalDate futureDate = LocalDate.now().plusDays(1);
		
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
				.param("name", "")
				.param("birthDate", futureDate.toString()))
			.andExpect(model().attributeHasErrors("pet"))
			.andExpect(model().attributeHasFieldErrors("pet", "name"))
			.andExpect(model().attributeHasFieldErrors("pet", "type"))
			.andExpect(model().attributeHasFieldErrors("pet", "birthDate"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testProcessCreationFormWithValidLongName() throws Exception {
		String longName = "ThisIsAVeryLongPetNameThatShouldStillBeValid";
		
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
				.param("name", longName)
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attribute("message", "New Pet has been Added"));
	}

	@Test
	void testProcessCreationFormWithSpecialCharactersInName() throws Exception {
		String nameWithSpecialChars = "Fluffy-O'Malley";
		
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
				.param("name", nameWithSpecialChars)
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attribute("message", "New Pet has been Added"));
	}

	@Test
	void testProcessCreationFormWithNumericName() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
				.param("name", "123")
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attribute("message", "New Pet has been Added"));
	}

	@Test
	void testProcessCreationFormWithVeryOldBirthDate() throws Exception {
		LocalDate veryOldDate = LocalDate.of(1990, 1, 1);
		
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
				.param("name", "Betty")
				.param("type", "hamster")
				.param("birthDate", veryOldDate.toString()))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attribute("message", "New Pet has been Added"));
	}

	@Test
	void testProcessUpdateFormWithDifferentValidName() throws Exception {
		Owner owner = new Owner();
		Pet existingPet = new Pet();
		existingPet.setName("ExistingPet");
		existingPet.setId(2);
		owner.addPet(existingPet);
		
		Pet petToUpdate = new Pet();
		petToUpdate.setId(TEST_PET_ID);
		petToUpdate.setName("CurrentPet");
		owner.addPet(petToUpdate);
		
		given(this.owners.findById(TEST_OWNER_ID)).willReturn(owner);

		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
				.param("name", "NewValidName")
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attribute("message", "Pet details has been edited"));
	}

	@Test
	void testInitCreationFormAddsNewPetToOwner() throws Exception {
		Owner owner = new Owner();
		given(this.owners.findById(TEST_OWNER_ID)).willReturn(owner);

		mockMvc.perform(get("/owners/{ownerId}/pets/new", TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("pet"))
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void testProcessCreationFormFlashMessage() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
				.param("name", "Betty")
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attribute("message", "New Pet has been Added"));
	}

	@Test
	void testProcessUpdateFormFlashMessage() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
				.param("name", "Betty")
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attribute("message", "Pet details has been edited"));
	}

}
