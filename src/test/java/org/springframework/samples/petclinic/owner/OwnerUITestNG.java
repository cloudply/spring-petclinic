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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TestNG UI tests for Owner functionality
 */
@WebMvcTest(OwnerController.class)
public class OwnerUITestNG {

    private static final int TEST_OWNER_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerRepository owners;

    private Owner testOwner;

    @BeforeClass(groups = {"ui", "forms"})
    public void setupClass() {
        System.out.println("Setting up OwnerUITestNG class");
    }

    @BeforeMethod(groups = {"ui", "forms"})
    public void setup() {
        testOwner = new Owner();
        testOwner.setId(TEST_OWNER_ID);
        testOwner.setFirstName("George");
        testOwner.setLastName("Franklin");
        testOwner.setAddress("110 W. Liberty St.");
        testOwner.setCity("Madison");
        testOwner.setTelephone("6085551023");
        testOwner.setAge(40);

        List<Owner> owners = new ArrayList<>();
        owners.add(testOwner);
        Page<Owner> ownersPage = new PageImpl<>(owners);

        given(this.owners.findById(TEST_OWNER_ID)).willReturn(testOwner);
        given(this.owners.findByLastName(eq("Franklin"), any(Pageable.class))).willReturn(ownersPage);
        given(this.owners.findOwnersByAge(eq(40), any(Pageable.class))).willReturn(ownersPage);
    }

    @Test(groups = "ui", description = "Test the find owners form is displayed correctly")
    public void testFindOwnersFormDisplay() throws Exception {
        MvcResult result = mockMvc.perform(get("/owners/find"))
            .andExpect(status().isOk())
            .andExpect(view().name("owners/findOwners"))
            .andExpect(model().attributeExists("owner"))
            .andReturn();
        
        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Find Owners");
        assertThat(content).contains("Find Owners by Age");
        assertThat(content).contains("Last name");
        assertThat(content).contains("Age");
    }

    @Test(groups = "forms", dependsOnGroups = "ui", 
          description = "Test the create owner form is displayed correctly")
    public void testCreateOwnerFormDisplay() throws Exception {
        MvcResult result = mockMvc.perform(get("/owners/new"))
            .andExpect(status().isOk())
            .andExpect(view().name("owners/createOrUpdateOwnerForm"))
            .andExpect(model().attributeExists("owner"))
            .andReturn();
        
        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("First Name");
        assertThat(content).contains("Last Name");
        assertThat(content).contains("Address");
        assertThat(content).contains("City");
        assertThat(content).contains("Telephone");
        assertThat(content).contains("Age");
        assertThat(content).contains("Add Owner");
    }


    @Test(groups = "details", description = "Test the owner details page is displayed correctly")
    public void testOwnerDetailsDisplay() throws Exception {
        MvcResult result = mockMvc.perform(get("/owners/{ownerId}", TEST_OWNER_ID))
            .andExpect(status().isOk())
            .andExpect(view().name("owners/ownerDetails"))
            .andExpect(model().attribute("owner", testOwner))
            .andReturn();
        
        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Owner Information");
        assertThat(content).contains("George Franklin");
        assertThat(content).contains("110 W. Liberty St.");
        assertThat(content).contains("Madison");
        assertThat(content).contains("6085551023");
        assertThat(content).contains("40"); // Age
        assertThat(content).contains("Edit Owner");
        assertThat(content).contains("Add New Pet");
        assertThat(content).contains("Delete Owner");
    }

    @DataProvider(name = "formFieldValidationData")
    public Object[][] formFieldValidationData() {
        return new Object[][] {
            // firstName, lastName, address, city, telephone, age, expectedError
            {"", "Smith", "123 Main St", "New York", "1234567890", "30", "firstName"},
            {"John", "", "123 Main St", "New York", "1234567890", "30", "lastName"},
            {"John", "Smith", "", "New York", "1234567890", "30", "address"},
            {"John", "Smith", "123 Main St", "", "1234567890", "30", "city"},
            {"John", "Smith", "123 Main St", "New York", "", "30", "telephone"},
            {"John", "Smith", "123 Main St", "New York", "123", "30", "telephone"}, // Invalid phone
            {"John", "Smith", "123 Main St", "New York", "1234567890", "-1", "age"} // Invalid age
        };
    }

    @Test(groups = "validation", dataProvider = "formFieldValidationData",
          description = "Test form validation for different field errors")
    public void testFormValidation(String firstName, String lastName, String address, 
                                  String city, String telephone, String age, 
                                  String expectedErrorField) throws Exception {
        mockMvc.perform(post("/owners/new")
            .param("firstName", firstName)
            .param("lastName", lastName)
            .param("address", address)
            .param("city", city)
            .param("telephone", telephone)
            .param("age", age))
            .andExpect(status().isOk())
            .andExpect(model().attributeHasErrors("owner"))
            .andExpect(model().attributeHasFieldErrors("owner", expectedErrorField));
    }

    @DataProvider(name = "ageSearchData")
    public Object[][] ageSearchData() {
        return new Object[][] {
            {25, true},  // age, hasResults
            {40, true},
            {99, false}
        };
    }

    @Test(groups = "search", dataProvider = "ageSearchData",
          description = "Test searching owners by different ages")
    public void testSearchByAge(int age, boolean hasResults) throws Exception {
        // Setup mock for empty results
        if (!hasResults) {
            given(this.owners.findOwnersByAge(eq(age), any(Pageable.class)))
                .willReturn(new PageImpl<>(new ArrayList<>()));
        }

        MvcResult result = mockMvc.perform(get("/owners/byage")
            .param("age", String.valueOf(age)))
            .andReturn();

        if (hasResults) {
            assertThat(result.getModelAndView().getViewName()).isEqualTo("owners/ownersList");
        } else {
            assertThat(result.getModelAndView().getViewName()).isEqualTo("owners/findOwners");
            assertThat(result.getModelAndView().getModel().get("message"))
                .isEqualTo("No owners found with age " + age);
        }
    }

    @Test(groups = "responsive", description = "Test responsive design elements")
    public void testResponsiveDesignElements() throws Exception {
        MvcResult result = mockMvc.perform(get("/owners/find"))
            .andExpect(status().isOk())
            .andReturn();
        
        String content = result.getResponse().getContentAsString();
        // Check for Bootstrap responsive classes
        assertThat(content).contains("col-sm-");
        assertThat(content).contains("form-group");
        assertThat(content).contains("form-control");
        assertThat(content).contains("btn");
    }

    @AfterMethod(groups = {"ui", "forms"})
    public void tearDown() {
        System.out.println("Tearing down after test method");
    }

    @AfterClass(groups = {"ui", "forms"})
    public void tearDownClass() {
        System.out.println("Tearing down OwnerUITestNG class");
    }
}
