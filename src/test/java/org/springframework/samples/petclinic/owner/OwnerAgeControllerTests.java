package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

	@Nested
	@DisplayName("Find Owners By Age Tests")
	class FindOwnersByAgeTests {
		
		@Test
		@DisplayName("Should find owners with matching age")
		void testFindOwnersByAgeWithResults() throws Exception {
			// Arrange
			Page<Owner> page = new PageImpl<>(ownersWithAge30);
			given(owners.findByAge(eq(30), any(Pageable.class))).willReturn(page);

			// Act & Assert
			mockMvc.perform(get("/owners/age").param("age", "30"))
				.andExpect(status().isOk())
				.andExpect(view().name("owners/ownersList"))
				.andExpect(model().attributeExists("listOwners"))
				.andExpect(model().attribute("totalItems", 2L))
				.andExpect(model().attribute("listOwners", hasSize(2)))
				.andExpect(model().attribute("listOwners", containsInAnyOrder(
					hasProperty("firstName", is("George")),
					hasProperty("firstName", is("Betty"))
				)));
				
			// Verify repository was called with correct parameters
			verify(owners, times(1)).findByAge(eq(30), any(Pageable.class));
		}

		@Test
		@DisplayName("Should return to find form when no owners found")
		void testFindOwnersByAgeWithNoResults() throws Exception {
			// Arrange
			Page<Owner> emptyPage = new PageImpl<>(Collections.emptyList());
			given(owners.findByAge(eq(99), any(Pageable.class))).willReturn(emptyPage);

			// Act & Assert
			mockMvc.perform(get("/owners/age").param("age", "99"))
				.andExpect(status().isOk())
				.andExpect(view().name("owners/findOwners"))
				.andExpect(model().attribute("message", "No owners found with age 99"));
				
			// Verify repository was called
			verify(owners, times(1)).findByAge(eq(99), any(Pageable.class));
		}

		@Test
		@DisplayName("Should handle pagination correctly")
		void testFindOwnersByAgeWithPagination() throws Exception {
			// Arrange - Create a list with more than 5 owners (default page size)
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
			
			// Test first page
			given(owners.findByAge(eq(40), any(Pageable.class))).willReturn(firstPage);
			
			ResultActions firstPageResult = mockMvc.perform(get("/owners/age")
				.param("age", "40")
				.param("page", "1"))
				.andExpect(status().isOk())
				.andExpect(view().name("owners/ownersList"))
				.andExpect(model().attribute("currentPage", 1))
				.andExpect(model().attribute("totalPages", 2))
				.andExpect(model().attribute("totalItems", 8L))
				.andExpect(model().attribute("listOwners", hasSize(5)));
				
			// Test second page
			given(owners.findByAge(eq(40), any(Pageable.class))).willReturn(secondPage);
			
			ResultActions secondPageResult = mockMvc.perform(get("/owners/age")
				.param("age", "40")
				.param("page", "2"))
				.andExpect(status().isOk())
				.andExpect(view().name("owners/ownersList"))
				.andExpect(model().attribute("currentPage", 2))
				.andExpect(model().attribute("totalPages", 2))
				.andExpect(model().attribute("totalItems", 8L))
				.andExpect(model().attribute("listOwners", hasSize(3)));
		}
	}
	
	@Nested
	@DisplayName("Edge Cases and Error Handling")
	class EdgeCasesTests {
		
		@Test
		@DisplayName("Should return 400 Bad Request for invalid age format")
		void testFindOwnersByAgeWithInvalidAge() throws Exception {
			mockMvc.perform(get("/owners/age").param("age", "invalid"))
				.andExpect(status().isBadRequest());
		}
		
		@Test
		@DisplayName("Should handle negative age values")
		void testFindOwnersByAgeWithNegativeAge() throws Exception {
			// Arrange
			Page<Owner> emptyPage = new PageImpl<>(Collections.emptyList());
			given(owners.findByAge(eq(-5), any(Pageable.class))).willReturn(emptyPage);
			
			// Act & Assert
			mockMvc.perform(get("/owners/age").param("age", "-5"))
				.andExpect(status().isOk())
				.andExpect(view().name("owners/findOwners"))
				.andExpect(model().attribute("message", "No owners found with age -5"));
		}
		
		@Test
		@DisplayName("Should handle missing age parameter")
		void testFindOwnersByAgeWithMissingAge() throws Exception {
			mockMvc.perform(get("/owners/age"))
				.andExpect(status().isBadRequest());
		}
		
		@Test
		@DisplayName("Should handle zero age value")
		void testFindOwnersByAgeWithZeroAge() throws Exception {
			// Arrange
			Page<Owner> emptyPage = new PageImpl<>(Collections.emptyList());
			given(owners.findByAge(eq(0), any(Pageable.class))).willReturn(emptyPage);
			
			// Act & Assert
			mockMvc.perform(get("/owners/age").param("age", "0"))
				.andExpect(status().isOk())
				.andExpect(view().name("owners/findOwners"))
				.andExpect(model().attribute("message", "No owners found with age 0"));
		}
		
		@Test
		@DisplayName("Should handle extremely large age values")
		void testFindOwnersByAgeWithLargeAge() throws Exception {
			// Arrange
			Page<Owner> emptyPage = new PageImpl<>(Collections.emptyList());
			given(owners.findByAge(eq(200), any(Pageable.class))).willReturn(emptyPage);
			
			// Act & Assert
			mockMvc.perform(get("/owners/age").param("age", "200"))
				.andExpect(status().isOk())
				.andExpect(view().name("owners/findOwners"))
				.andExpect(model().attribute("message", "No owners found with age 200"));
		}
	}
}
