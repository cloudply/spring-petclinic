package org.springframework.samples.petclinic.owner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TestNG test class for {@link OwnerController}'s age filtering functionality
 */
@WebMvcTest(OwnerController.class)
public class OwnerAgeFilterTestNG {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerRepository owners;

    @BeforeClass(groups = "ageFilter")
    public void setupClass() {
        System.out.println("Setting up OwnerAgeFilterTestNG class");
    }

    @BeforeMethod(groups = "ageFilter")
    public void setup() {
        // Setup mock repository responses for different ages
        setupMockRepositoryForAge(25, 3);
        setupMockRepositoryForAge(35, 2);
        setupMockRepositoryForAge(45, 1);
        setupMockRepositoryForAge(99, 0);
    }

    private void setupMockRepositoryForAge(int age, int count) {
        List<Owner> ownersWithAge = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Owner owner = new Owner();
            owner.setId(i * 100 + age);
            owner.setFirstName("Owner-" + age + "-" + i);
            owner.setLastName("LastName-" + i);
            owner.setAddress(i + " Test Street");
            owner.setCity("Test City");
            owner.setTelephone("1234567890");
            owner.setAge(age);
            ownersWithAge.add(owner);
        }
        
        given(this.owners.findOwnersByAge(eq(age), any(Pageable.class)))
            .willReturn(new PageImpl<>(ownersWithAge));
    }

    @Test(groups = "ageFilter", description = "Test finding owners by age with results")
    public void testFindOwnersByAgeWithResults() throws Exception {
        mockMvc.perform(get("/owners/byage")
                .param("age", "25"))
            .andExpect(status().isOk())
            .andExpect(view().name("owners/ownersList"))
            .andExpect(model().attributeExists("listOwners"))
            .andExpect(model().attribute("totalItems", 3L));
    }

    @Test(groups = "ageFilter", description = "Test finding owners by age with no results")
    public void testFindOwnersByAgeWithNoResults() throws Exception {
        mockMvc.perform(get("/owners/byage")
                .param("age", "99"))
            .andExpect(status().isOk())
            .andExpect(view().name("owners/findOwners"))
            .andExpect(model().attribute("message", "No owners found with age 99"));
    }

    @Test(groups = "ageFilter", description = "Test finding owners by age without providing age parameter")
    public void testFindOwnersByAgeWithoutParameter() throws Exception {
        mockMvc.perform(get("/owners/byage"))
            .andExpect(status().isOk())
            .andExpect(view().name("owners/findOwners"))
            .andExpect(model().attribute("message", "Please provide an age to search"));
    }

    @DataProvider(name = "ageData")
    public Object[][] ageData() {
        return new Object[][] {
            {25, 3},
            {35, 2},
            {45, 1},
            {99, 0}
        };
    }

    @Test(groups = "ageFilter", dataProvider = "ageData", 
          description = "Test finding owners by different ages")
    public void testFindOwnersByDifferentAges(int age, int expectedCount) throws Exception {
        MvcResult result = mockMvc.perform(get("/owners/byage")
                .param("age", String.valueOf(age)))
            .andReturn();
        
        if (expectedCount > 0) {
            assertThat(result.getModelAndView().getViewName()).isEqualTo("owners/ownersList");
            @SuppressWarnings("unchecked")
            List<Owner> resultOwners = (List<Owner>) result.getModelAndView().getModel().get("listOwners");
            assertThat(resultOwners).hasSize(expectedCount);
            assertThat(resultOwners.get(0).getAge()).isEqualTo(age);
        } else {
            assertThat(result.getModelAndView().getViewName()).isEqualTo("owners/findOwners");
            assertThat(result.getModelAndView().getModel().get("message"))
                .isEqualTo("No owners found with age " + age);
        }
    }

    @AfterMethod(groups = "ageFilter")
    public void tearDown() {
        System.out.println("Tearing down after test method");
    }

    @AfterClass(groups = "ageFilter")
    public void tearDownClass() {
        System.out.println("Tearing down OwnerAgeFilterTestNG class");
    }
}
