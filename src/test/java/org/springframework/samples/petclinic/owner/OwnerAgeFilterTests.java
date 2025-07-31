package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link OwnerController}'s age filtering functionality
 */
@WebMvcTest(OwnerController.class)
public class OwnerAgeFilterTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerRepository owners;

    private List<Owner> ownersWithAge30;
    private List<Owner> ownersWithAge40;
    private List<Owner> ownersWithAge50;

    @BeforeEach
    void setup() {
        // Create test data with different ages
        ownersWithAge30 = createOwnersWithAge(30, 3);
        ownersWithAge40 = createOwnersWithAge(40, 2);
        ownersWithAge50 = createOwnersWithAge(50, 1);

        // Setup mock repository responses
        given(this.owners.findOwnersByAge(eq(30), any(Pageable.class)))
            .willReturn(new PageImpl<>(ownersWithAge30));
        given(this.owners.findOwnersByAge(eq(40), any(Pageable.class)))
            .willReturn(new PageImpl<>(ownersWithAge40));
        given(this.owners.findOwnersByAge(eq(50), any(Pageable.class)))
            .willReturn(new PageImpl<>(ownersWithAge50));
        given(this.owners.findOwnersByAge(eq(99), any(Pageable.class)))
            .willReturn(new PageImpl<>(new ArrayList<>()));
    }

    @Test
    void testFindOwnersByAgeWithResults() throws Exception {
        mockMvc.perform(get("/owners/byage")
                .param("age", "30"))
            .andExpect(status().isOk())
            .andExpect(view().name("owners/ownersList"))
            .andExpect(model().attributeExists("listOwners"))
            .andExpect(model().attribute("totalItems", 3L));
    }

    @Test
    void testFindOwnersByAgeWithNoResults() throws Exception {
        mockMvc.perform(get("/owners/byage")
                .param("age", "99"))
            .andExpect(status().isOk())
            .andExpect(view().name("owners/findOwners"))
            .andExpect(model().attribute("message", "No owners found with age 99"));
    }

    @Test
    void testFindOwnersByAgeWithMultiplePages() throws Exception {
        // Create a large list of owners to test pagination
        List<Owner> manyOwners = createOwnersWithAge(60, 12);
        Page<Owner> pagedOwners = new PageImpl<>(manyOwners.subList(0, 5), 
                                                Pageable.ofSize(5), 12);
        
        given(this.owners.findOwnersByAge(eq(60), any(Pageable.class)))
            .willReturn(pagedOwners);

        mockMvc.perform(get("/owners/byage")
                .param("age", "60"))
            .andExpect(status().isOk())
            .andExpect(view().name("owners/ownersList"))
            .andExpect(model().attribute("totalItems", 12L))
            .andExpect(model().attribute("totalPages", 3));
    }

    @Test
    void testFindOwnersByAgeWithInvalidInput() throws Exception {
        MvcResult result = mockMvc.perform(get("/owners/byage"))
            .andExpect(status().isOk())
            .andExpect(view().name("owners/findOwners"))
            .andReturn();
        
        // Verify we get redirected back to the form with an appropriate message
        assertThat(result.getModelAndView().getModel().containsKey("message")).isTrue();
    }

    @Test
    void testFindOwnersByAgeContentCheck() throws Exception {
        MvcResult result = mockMvc.perform(get("/owners/byage")
                .param("age", "50"))
            .andExpect(status().isOk())
            .andExpect(view().name("owners/ownersList"))
            .andReturn();
        
        @SuppressWarnings("unchecked")
        List<Owner> resultOwners = (List<Owner>) result.getModelAndView().getModel().get("listOwners");
        
        assertThat(resultOwners).hasSize(1);
        assertThat(resultOwners.get(0).getAge()).isEqualTo(50);
        assertThat(resultOwners.get(0).getFirstName()).isEqualTo("Owner-50-1");
    }

    /**
     * Helper method to create a list of owners with the specified age
     */
    private List<Owner> createOwnersWithAge(int age, int count) {
        List<Owner> owners = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Owner owner = new Owner();
            owner.setId(i * 100 + age); // Create unique IDs
            owner.setFirstName("Owner-" + age + "-" + i);
            owner.setLastName("LastName-" + i);
            owner.setAddress(i + " Test Street");
            owner.setCity("Test City");
            owner.setTelephone("1234567890");
            owner.setAge(age);
            owners.add(owner);
        }
        return owners;
    }
}
