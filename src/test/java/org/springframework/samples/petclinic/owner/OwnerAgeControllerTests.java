package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link OwnerAgeController}
 */
@WebMvcTest(OwnerAgeController.class)
class OwnerAgeControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OwnerRepository owners;

	private List<Owner> ownersWithAge30;

	@BeforeEach
	void setup() {
		ownersWithAge30 = new ArrayList<>();
		
		Owner owner1 = new Owner();
		owner1.setId(1);
		owner1.setFirstName("George");
		owner1.setLastName("Franklin");
		owner1.setAddress("110 W. Liberty St.");
		owner1.setCity("Madison");
		owner1.setTelephone("6085551023");
		owner1.setBirthDate(LocalDate.now().minusYears(30));
		ownersWithAge30.add(owner1);
		
		Owner owner2 = new Owner();
		owner2.setId(2);
		owner2.setFirstName("Betty");
		owner2.setLastName("Davis");
		owner2.setAddress("638 Cardinal Ave.");
		owner2.setCity("Sun Prairie");
		owner2.setTelephone("6085551749");
		owner2.setBirthDate(LocalDate.now().minusYears(30));
		ownersWithAge30.add(owner2);
	}

	@Test
	void testFindOwnersByAgeWithResults() throws Exception {
		Page<Owner> page = new PageImpl<>(ownersWithAge30);
		given(owners.findByAge(eq(30), any(Pageable.class))).willReturn(page);

		mockMvc.perform(get("/owners/age").param("age", "30"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/ownersList"))
			.andExpect(model().attributeExists("listOwners"))
			.andExpect(model().attribute("totalItems", 2L));
	}

	@Test
	void testFindOwnersByAgeWithNoResults() throws Exception {
		Page<Owner> emptyPage = new PageImpl<>(new ArrayList<>());
		given(owners.findByAge(eq(99), any(Pageable.class))).willReturn(emptyPage);

		mockMvc.perform(get("/owners/age").param("age", "99"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/findOwners"))
			.andExpect(model().attribute("message", "No owners found with age 99"));
	}

	@Test
	void testFindOwnersByAgeWithPagination() throws Exception {
		// Create a list with more than 5 owners (default page size)
		List<Owner> manyOwners = new ArrayList<>();
		for (int i = 0; i < 8; i++) {
			Owner owner = new Owner();
			owner.setId(i);
			owner.setFirstName("Owner");
			owner.setLastName("Number " + i);
			owner.setBirthDate(LocalDate.now().minusYears(40));
			manyOwners.add(owner);
		}
		
		Page<Owner> firstPage = new PageImpl<>(manyOwners.subList(0, 5), PageRequest.of(0, 5), 8);
		Page<Owner> secondPage = new PageImpl<>(manyOwners.subList(5, 8), PageRequest.of(1, 5), 8);
		
		given(owners.findByAge(eq(40), any(Pageable.class))).willReturn(firstPage);
		
		// Test first page
		mockMvc.perform(get("/owners/age").param("age", "40").param("page", "1"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/ownersList"))
			.andExpect(model().attribute("currentPage", 1))
			.andExpect(model().attribute("totalPages", 2))
			.andExpect(model().attribute("totalItems", 8L));
			
		// Test second page
		given(owners.findByAge(eq(40), any(Pageable.class))).willReturn(secondPage);
		mockMvc.perform(get("/owners/age").param("age", "40").param("page", "2"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/ownersList"))
			.andExpect(model().attribute("currentPage", 2))
			.andExpect(model().attribute("totalPages", 2))
			.andExpect(model().attribute("totalItems", 8L));
	}
	
	@Test
	void testFindOwnersByAgeWithInvalidAge() throws Exception {
		mockMvc.perform(get("/owners/age").param("age", "invalid"))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	void testFindOwnersByAgeWithNegativeAge() throws Exception {
		Page<Owner> emptyPage = new PageImpl<>(new ArrayList<>());
		given(owners.findByAge(eq(-5), any(Pageable.class))).willReturn(emptyPage);
		
		mockMvc.perform(get("/owners/age").param("age", "-5"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/findOwners"))
			.andExpect(model().attribute("message", "No owners found with age -5"));
	}
}
