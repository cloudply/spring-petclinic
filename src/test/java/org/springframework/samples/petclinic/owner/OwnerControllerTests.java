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
import org.springframework.data.domain.PageRequest;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

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

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OwnerRepository owners;

	private Owner george() {
		Owner george = new Owner();
		george.setId(TEST_OWNER_ID);
		george.setFirstName("George");
		george.setLastName("Franklin");
		george.setAddress("110 W. Liberty St.");
		george.setCity("Madison");
		george.setTelephone("6085551023");
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
		given(this.owners.findByLastName(eq("Franklin"), any(Pageable.class)))
			.willReturn(new PageImpl<>(Lists.newArrayList(george)));

		given(this.owners.findAll(any(Pageable.class))).willReturn(new PageImpl<>(Lists.newArrayList(george)));

		given(this.owners.findById(TEST_OWNER_ID)).willReturn(george);
		Visit visit = new Visit();
		visit.setDate(LocalDate.now());
		george.getPet("Max").getVisits().add(visit);

	}

	@Test
	void testInitCreationForm() throws Exception {
		mockMvc.perform(get("/owners/new"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("owner"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessCreationFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/new").param("firstName", "Joe")
				.param("lastName", "Bloggs")
				.param("address", "123 Caramel Street")
				.param("city", "London")
				.param("telephone", "1316761638"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	void testProcessCreationFormHasErrors() throws Exception {
		mockMvc
			.perform(post("/owners/new").param("firstName", "Joe").param("lastName", "Bloggs").param("city", "London"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("owner"))
			.andExpect(model().attributeHasFieldErrors("owner", "address"))
			.andExpect(model().attributeHasFieldErrors("owner", "telephone"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testInitFindForm() throws Exception {
		mockMvc.perform(get("/owners/find"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("owner"))
			.andExpect(view().name("owners/findOwners"));
	}

	@Test
	void testProcessFindFormSuccess() throws Exception {
		Page<Owner> tasks = new PageImpl<>(Lists.newArrayList(george(), new Owner()));
		Mockito.when(this.owners.findByLastName(anyString(), any(Pageable.class))).thenReturn(tasks);
		mockMvc.perform(get("/owners?page=1")).andExpect(status().isOk()).andExpect(view().name("owners/ownersList"));
	}

	@Test
	void testProcessFindFormByLastName() throws Exception {
		Page<Owner> tasks = new PageImpl<>(Lists.newArrayList(george()));
		Mockito.when(this.owners.findByLastName(eq("Franklin"), any(Pageable.class))).thenReturn(tasks);
		mockMvc.perform(get("/owners?page=1").param("lastName", "Franklin"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));
	}

	@Test
	void testProcessFindFormNoOwnersFound() throws Exception {
		Page<Owner> tasks = new PageImpl<>(Lists.newArrayList());
		Mockito.when(this.owners.findByLastName(eq("Unknown Surname"), any(Pageable.class))).thenReturn(tasks);
		mockMvc.perform(get("/owners?page=1").param("lastName", "Unknown Surname"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("owner", "lastName"))
			.andExpect(model().attributeHasFieldErrorCode("owner", "lastName", "notFound"))
			.andExpect(view().name("owners/findOwners"));

	}

	@Test
	void testInitUpdateOwnerForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/edit", TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("owner"))
			.andExpect(model().attribute("owner", hasProperty("lastName", is("Franklin"))))
			.andExpect(model().attribute("owner", hasProperty("firstName", is("George"))))
			.andExpect(model().attribute("owner", hasProperty("address", is("110 W. Liberty St."))))
			.andExpect(model().attribute("owner", hasProperty("city", is("Madison"))))
			.andExpect(model().attribute("owner", hasProperty("telephone", is("6085551023"))))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessUpdateOwnerFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID).param("firstName", "Joe")
				.param("lastName", "Bloggs")
				.param("address", "123 Caramel Street")
				.param("city", "London")
				.param("telephone", "1616291589"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void testProcessUpdateOwnerFormUnchangedSuccess() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void testProcessUpdateOwnerFormHasErrors() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID).param("firstName", "Joe")
				.param("lastName", "Bloggs")
				.param("address", "")
				.param("telephone", ""))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("owner"))
			.andExpect(model().attributeHasFieldErrors("owner", "address"))
			.andExpect(model().attributeHasFieldErrors("owner", "telephone"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testShowOwner() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}", TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpect(model().attribute("owner", hasProperty("lastName", is("Franklin"))))
			.andExpect(model().attribute("owner", hasProperty("firstName", is("George"))))
			.andExpect(model().attribute("owner", hasProperty("address", is("110 W. Liberty St."))))
			.andExpect(model().attribute("owner", hasProperty("city", is("Madison"))))
			.andExpect(model().attribute("owner", hasProperty("telephone", is("6085551023"))))
			.andExpect(model().attribute("owner", hasProperty("pets", not(empty()))))
			.andExpect(model().attribute("owner",
					hasProperty("pets", hasItem(hasProperty("visits", hasSize(greaterThan(0)))))))
			.andExpect(view().name("owners/ownerDetails"));
	}

	// Additional comprehensive test cases

	@Test
	void testProcessCreationFormWithEmptyFirstName() throws Exception {
		mockMvc.perform(post("/owners/new")
			.param("firstName", "")
			.param("lastName", "Bloggs")
			.param("address", "123 Caramel Street")
			.param("city", "London")
			.param("telephone", "1316761638"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("owner"))
			.andExpect(model().attributeHasFieldErrors("owner", "firstName"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessCreationFormWithEmptyLastName() throws Exception {
		mockMvc.perform(post("/owners/new")
			.param("firstName", "Joe")
			.param("lastName", "")
			.param("address", "123 Caramel Street")
			.param("city", "London")
			.param("telephone", "1316761638"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("owner"))
			.andExpect(model().attributeHasFieldErrors("owner", "lastName"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessCreationFormWithInvalidTelephone() throws Exception {
		mockMvc.perform(post("/owners/new")
			.param("firstName", "Joe")
			.param("lastName", "Bloggs")
			.param("address", "123 Caramel Street")
			.param("city", "London")
			.param("telephone", "invalid"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("owner"))
			.andExpect(model().attributeHasFieldErrors("owner", "telephone"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessUpdateOwnerFormWithEmptyFirstName() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID)
			.param("firstName", "")
			.param("lastName", "Bloggs")
			.param("address", "123 Caramel Street")
			.param("city", "London")
			.param("telephone", "1616291589"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("owner"))
			.andExpect(model().attributeHasFieldErrors("owner", "firstName"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessUpdateOwnerFormWithEmptyLastName() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID)
			.param("firstName", "Joe")
			.param("lastName", "")
			.param("address", "123 Caramel Street")
			.param("city", "London")
			.param("telephone", "1616291589"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("owner"))
			.andExpect(model().attributeHasFieldErrors("owner", "lastName"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessFindFormWithPagination() throws Exception {
		Owner owner1 = new Owner();
		owner1.setId(1);
		owner1.setFirstName("John");
		owner1.setLastName("Smith");
		
		Owner owner2 = new Owner();
		owner2.setId(2);
		owner2.setFirstName("Jane");
		owner2.setLastName("Smith");

		Page<Owner> ownersPage = new PageImpl<>(Lists.newArrayList(owner1, owner2), PageRequest.of(0, 5), 2);
		given(this.owners.findByLastName(eq("Smith"), any(Pageable.class))).willReturn(ownersPage);

		mockMvc.perform(get("/owners").param("lastName", "Smith").param("page", "1"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("currentPage", 1))
			.andExpect(model().attribute("totalPages", 1))
			.andExpect(model().attribute("totalItems", 2L))
			.andExpect(model().attributeExists("listOwners"))
			.andExpect(view().name("owners/ownersList"));
	}

	@Test
	void testProcessFindFormWithPageParameter() throws Exception {
		Owner owner1 = new Owner();
		owner1.setId(1);
		owner1.setFirstName("John");
		owner1.setLastName("Doe");

		Page<Owner> ownersPage = new PageImpl<>(Lists.newArrayList(owner1), PageRequest.of(1, 5), 6);
		given(this.owners.findByLastName(eq(""), any(Pageable.class))).willReturn(ownersPage);

		mockMvc.perform(get("/owners").param("page", "2"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("currentPage", 2))
			.andExpect(model().attribute("totalPages", 2))
			.andExpect(model().attribute("totalItems", 6L))
			.andExpect(view().name("owners/ownersList"));
	}

	@Test
	void testProcessFindFormDefaultPage() throws Exception {
		Owner owner1 = new Owner();
		owner1.setId(1);
		owner1.setFirstName("John");
		owner1.setLastName("Doe");

		Page<Owner> ownersPage = new PageImpl<>(Lists.newArrayList(owner1), PageRequest.of(0, 5), 1);
		given(this.owners.findByLastName(eq(""), any(Pageable.class))).willReturn(ownersPage);

		mockMvc.perform(get("/owners"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("currentPage", 1))
			.andExpect(view().name("owners/ownersList"));
	}

	@Test
	void testProcessCreationFormSavesOwner() throws Exception {
		mockMvc.perform(post("/owners/new")
			.param("firstName", "Joe")
			.param("lastName", "Bloggs")
			.param("address", "123 Caramel Street")
			.param("city", "London")
			.param("telephone", "1316761638"))
			.andExpect(status().is3xxRedirection());
		
		verify(this.owners, times(1)).save(any(Owner.class));
	}

	@Test
	void testProcessUpdateOwnerFormSavesOwner() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID)
			.param("firstName", "Joe")
			.param("lastName", "Bloggs")
			.param("address", "123 Caramel Street")
			.param("city", "London")
			.param("telephone", "1616291589"))
			.andExpect(status().is3xxRedirection());
		
		verify(this.owners, times(1)).save(any(Owner.class));
	}

	@Test
	void testProcessCreationFormDoesNotSaveOnValidationError() throws Exception {
		mockMvc.perform(post("/owners/new")
			.param("firstName", "Joe")
			.param("lastName", "Bloggs")
			.param("city", "London"))
			.andExpect(status().isOk());
		
		verify(this.owners, never()).save(any(Owner.class));
	}

	@Test
	void testProcessUpdateOwnerFormDoesNotSaveOnValidationError() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID)
			.param("firstName", "Joe")
			.param("lastName", "Bloggs")
			.param("city", "London"))
			.andExpect(status().isOk());
		
		verify(this.owners, never()).save(any(Owner.class));
	}

	@Test
	void testProcessCreationFormWithAllFieldsEmpty() throws Exception {
		mockMvc.perform(post("/owners/new"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("owner"))
			.andExpect(model().attributeHasFieldErrors("owner", "firstName"))
			.andExpect(model().attributeHasFieldErrors("owner", "lastName"))
			.andExpect(model().attributeHasFieldErrors("owner", "address"))
			.andExpect(model().attributeHasFieldErrors("owner", "telephone"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessUpdateOwnerFormWithAllFieldsEmpty() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("owner"))
			.andExpect(model().attributeHasFieldErrors("owner", "firstName"))
			.andExpect(model().attributeHasFieldErrors("owner", "lastName"))
			.andExpect(model().attributeHasFieldErrors("owner", "address"))
			.andExpect(model().attributeHasFieldErrors("owner", "telephone"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessFindFormWithExactlyOneResult() throws Exception {
		Owner singleOwner = george();
		given(this.owners.findByLastName(eq("Franklin"), any(Pageable.class)))
			.willReturn(new PageImpl<>(Lists.newArrayList(singleOwner)));

		mockMvc.perform(get("/owners").param("lastName", "Franklin"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/" + TEST_OWNER_ID));
	}

	@Test
	void testProcessFindFormWithMultipleResults() throws Exception {
		Owner owner1 = george();
		Owner owner2 = new Owner();
		owner2.setId(2);
		owner2.setFirstName("Betty");
		owner2.setLastName("Franklin");
		
		given(this.owners.findByLastName(eq("Franklin"), any(Pageable.class)))
			.willReturn(new PageImpl<>(Lists.newArrayList(owner1, owner2)));

		mockMvc.perform(get("/owners").param("lastName", "Franklin"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/ownersList"))
			.andExpect(model().attributeExists("listOwners"));
	}

	@Test
	void testShowOwnerWithValidId() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}", TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("owner"))
			.andExpect(view().name("owners/ownerDetails"));
		
		verify(this.owners, times(1)).findById(TEST_OWNER_ID);
	}

	@Test
	void testProcessCreationFormWithFlashMessage() throws Exception {
		mockMvc.perform(post("/owners/new")
			.param("firstName", "Joe")
			.param("lastName", "Bloggs")
			.param("address", "123 Caramel Street")
			.param("city", "London")
			.param("telephone", "1316761638"))
			.andExpect(status().is3xxRedirection())
			.andExpect(flash().attributeExists("message"));
	}

	@Test
	void testProcessUpdateOwnerFormWithFlashMessage() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID)
			.param("firstName", "Joe")
			.param("lastName", "Bloggs")
			.param("address", "123 Caramel Street")
			.param("city", "London")
			.param("telephone", "1616291589"))
			.andExpect(status().is3xxRedirection())
			.andExpect(flash().attributeExists("message"));
	}

	@Test
	void testProcessCreationFormWithFlashErrorMessage() throws Exception {
		mockMvc.perform(post("/owners/new")
			.param("firstName", "Joe")
			.param("lastName", "Bloggs")
			.param("city", "London"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessUpdateOwnerFormWithFlashErrorMessage() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID)
			.param("firstName", "Joe")
			.param("lastName", "Bloggs")
			.param("city", "London"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessFindFormWithNullLastName() throws Exception {
		given(this.owners.findByLastName(eq(""), any(Pageable.class)))
			.willReturn(new PageImpl<>(Lists.newArrayList(george(), new Owner())));

		mockMvc.perform(get("/owners"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/ownersList"));
	}

	@Test
	void testPaginationModelAttributes() throws Exception {
		Owner owner1 = new Owner();
		owner1.setId(1);
		Owner owner2 = new Owner();
		owner2.setId(2);
		
		Page<Owner> ownersPage = new PageImpl<>(Lists.newArrayList(owner1, owner2), PageRequest.of(0, 5), 10);
		given(this.owners.findByLastName(eq(""), any(Pageable.class))).willReturn(ownersPage);

		mockMvc.perform(get("/owners").param("page", "1"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("currentPage", 1))
			.andExpect(model().attribute("totalPages", 2))
			.andExpect(model().attribute("totalItems", 10L))
			.andExpect(model().attributeExists("listOwners"))
			.andExpect(view().name("owners/ownersList"));
	}

	@Test
	void testProcessCreationFormWithValidLongName() throws Exception {
		mockMvc.perform(post("/owners/new")
			.param("firstName", "VeryLongFirstNameThatShouldStillBeValid")
			.param("lastName", "VeryLongLastNameThatShouldStillBeValid")
			.param("address", "123 Very Long Address Street Name")
			.param("city", "VeryLongCityName")
			.param("telephone", "1234567890"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	void testProcessUpdateOwnerFormWithValidLongName() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID)
			.param("firstName", "VeryLongFirstNameThatShouldStillBeValid")
			.param("lastName", "VeryLongLastNameThatShouldStillBeValid")
			.param("address", "123 Very Long Address Street Name")
			.param("city", "VeryLongCityName")
			.param("telephone", "1234567890"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	void testProcessCreationFormWithSpecialCharacters() throws Exception {
		mockMvc.perform(post("/owners/new")
			.param("firstName", "José")
			.param("lastName", "García-López")
			.param("address", "123 O'Connor St.")
			.param("city", "São Paulo")
			.param("telephone", "1234567890"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	void testProcessUpdateOwnerFormWithSpecialCharacters() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID)
			.param("firstName", "José")
			.param("lastName", "García-López")
			.param("address", "123 O'Connor St.")
			.param("city", "São Paulo")
			.param("telephone", "1234567890"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	void testProcessFindFormWithEmptyStringLastName() throws Exception {
		given(this.owners.findByLastName(eq(""), any(Pageable.class)))
			.willReturn(new PageImpl<>(Lists.newArrayList(george(), new Owner())));

		mockMvc.perform(get("/owners").param("lastName", ""))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/ownersList"));
	}

	@Test
	void testProcessFindFormWithWhitespaceLastName() throws Exception {
		given(this.owners.findByLastName(eq("   "), any(Pageable.class)))
			.willReturn(new PageImpl<>(Lists.newArrayList()));

		mockMvc.perform(get("/owners").param("lastName", "   "))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("owner", "lastName"))
			.andExpect(view().name("owners/findOwners"));
	}

	@Test
	void testProcessCreationFormWithMinimalValidData() throws Exception {
		mockMvc.perform(post("/owners/new")
			.param("firstName", "A")
			.param("lastName", "B")
			.param("address", "1")
			.param("city", "C")
			.param("telephone", "1"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	void testProcessUpdateOwnerFormWithMinimalValidData() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID)
			.param("firstName", "A")
			.param("lastName", "B")
			.param("address", "1")
			.param("city", "C")
			.param("telephone", "1"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	void testFindOwnerModelAttributeReturnsNewOwnerWhenIdIsNull() throws Exception {
		mockMvc.perform(get("/owners/new"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("owner"));
		
		verify(this.owners, never()).findById(any());
	}

	@Test
	void testFindOwnerModelAttributeReturnsExistingOwnerWhenIdProvided() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/edit", TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("owner"));
		
		verify(this.owners, times(1)).findById(TEST_OWNER_ID);
	}

}
