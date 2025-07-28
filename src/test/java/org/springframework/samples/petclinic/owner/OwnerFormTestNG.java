package org.springframework.samples.petclinic.owner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testng.annotations.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TestNG tests for Owner form submission and validation
 */
@WebMvcTest(OwnerController.class)
public class OwnerFormTestNG {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerRepository owners;

    @BeforeClass(groups = "forms")
    public void setupClass() {
        System.out.println("Setting up OwnerFormTestNG class");
    }

    @BeforeMethod(groups = "forms")
    public void setup() {
        // Setup mock to save owner
        when(owners.save(any(Owner.class))).thenAnswer(invocation -> {
            Owner owner = invocation.getArgument(0);
            if (owner.getId() == null) {
                owner.setId(1);
            }
            return owner;
        });
    }

    @DataProvider(name = "validOwnerData")
    public Object[][] validOwnerData() {
        return new Object[][] {
            {"John", "Doe", "123 Main St", "New York", "1234567890", "30"},
            {"Jane", "Smith", "456 Oak Ave", "Chicago", "0987654321", "25"},
            {"Bob", "Johnson", "789 Pine Rd", "Los Angeles", "5551234567", "45"},
            {"Maria", "Garcia", "101 Cedar Ln", "Miami", "3051234567", "35"},
            {"James", "Brown", "202 Maple Dr", "Boston", "6171234567", "50"}
        };
    }

    @Test(groups = "submission", dataProvider = "validOwnerData",
          description = "Test successful form submission with valid data")
    public void testSuccessfulFormSubmission(String firstName, String lastName, 
                                            String address, String city, 
                                            String telephone, String age) throws Exception {
        mockMvc.perform(post("/owners/new")
            .param("firstName", firstName)
            .param("lastName", lastName)
            .param("address", address)
            .param("city", city)
            .param("telephone", telephone)
            .param("age", age))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attributeExists("message"));
        
        verify(owners).save(any(Owner.class));
    }

    @DataProvider(name = "invalidTelephoneData")
    public Object[][] invalidTelephoneData() {
        return new Object[][] {
            {"12345"},         // Too short
            {"12345678901"},   // Too long
            {"abcdefghij"},    // Non-numeric
            {"123-456-7890"},  // Contains non-numeric characters
            {"(123)4567890"}   // Contains non-numeric characters
        };
    }

    @Test(groups = "validation", dataProvider = "invalidTelephoneData",
          description = "Test telephone validation")
    public void testTelephoneValidation(String telephone) throws Exception {
        mockMvc.perform(post("/owners/new")
            .param("firstName", "John")
            .param("lastName", "Doe")
            .param("address", "123 Main St")
            .param("city", "New York")
            .param("telephone", telephone)
            .param("age", "30"))
            .andExpect(status().isOk())
            .andExpect(model().attributeHasFieldErrors("owner", "telephone"));
    }

    @Test(groups = "messages", dependsOnGroups = "submission",
          description = "Test flash messages after form submission")
    public void testFlashMessages() throws Exception {
        MvcResult result = mockMvc.perform(post("/owners/new")
            .param("firstName", "Test")
            .param("lastName", "User")
            .param("address", "123 Test St")
            .param("city", "Test City")
            .param("telephone", "1234567890")
            .param("age", "30"))
            .andExpect(status().is3xxRedirection())
            .andReturn();
        
        assertThat(result.getFlashMap().get("message")).isEqualTo("New Owner Created");
    }

    @Test(groups = "errors", description = "Test error messages display")
    public void testErrorMessages() throws Exception {
        MvcResult result = mockMvc.perform(post("/owners/new")
            .param("firstName", "")
            .param("lastName", "")
            .param("address", "")
            .param("city", "")
            .param("telephone", "")
            .param("age", ""))
            .andExpect(status().isOk())
            .andExpect(model().attributeHasErrors("owner"))
            .andReturn();
        
        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("is required");
    }

    @Test(groups = "update", description = "Test owner update form submission")
    public void testOwnerUpdateFormSubmission() throws Exception {
        // Setup existing owner
        Owner existingOwner = new Owner();
        existingOwner.setId(1);
        existingOwner.setFirstName("Original");
        existingOwner.setLastName("Owner");
        existingOwner.setAddress("Original Address");
        existingOwner.setCity("Original City");
        existingOwner.setTelephone("1234567890");
        existingOwner.setAge(30);
        
        when(owners.findById(1)).thenReturn(existingOwner);
        
        mockMvc.perform(post("/owners/1/edit")
            .param("firstName", "Updated")
            .param("lastName", "Owner")
            .param("address", "Updated Address")
            .param("city", "Updated City")
            .param("telephone", "0987654321")
            .param("age", "35"))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attributeExists("message"));
        
        verify(owners).save(any(Owner.class));
    }

    @AfterMethod(groups = "forms")
    public void tearDown() {
        System.out.println("Tearing down after test method");
    }

    @AfterClass(groups = "forms")
    public void tearDownClass() {
        System.out.println("Tearing down OwnerFormTestNG class");
    }
}
