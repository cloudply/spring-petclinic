package org.springframework.samples.petclinic.owner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OwnerController.class)
public class OwnerControllerTestNG {

    private static final int TEST_OWNER_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerRepository owners;

    private Owner george;

    @BeforeClass
    public void setupClass() {
        System.out.println("Setting up OwnerControllerTestNG class");
    }

    @BeforeMethod
    public void setup() {
        george = new Owner();
        george.setId(TEST_OWNER_ID);
        george.setFirstName("George");
        george.setLastName("Franklin");
        george.setAddress("110 W. Liberty St.");
        george.setCity("Madison");
        george.setTelephone("6085551023");
        george.setAge(40);

        List<Owner> owners = new ArrayList<>();
        owners.add(george);
        Page<Owner> ownersPage = new PageImpl<>(owners);

        given(this.owners.findById(TEST_OWNER_ID)).willReturn(george);
        given(this.owners.findByLastName(eq("Franklin"), any(Pageable.class))).willReturn(ownersPage);
        given(this.owners.findOwnersByAge(eq(40), any(Pageable.class))).willReturn(ownersPage);
    }

    @Test(groups = "controller")
    public void testInitCreationForm() throws Exception {
        mockMvc.perform(get("/owners/new"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("owner"))
            .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }

    @Test(groups = "controller", dependsOnMethods = "testInitCreationForm")
    public void testProcessCreationFormSuccess() throws Exception {
        mockMvc.perform(post("/owners/new")
            .param("firstName", "Joe")
            .param("lastName", "Bloggs")
            .param("address", "123 Caramel Street")
            .param("city", "London")
            .param("telephone", "0123456789")
            .param("age", "35"))
            .andExpect(status().is3xxRedirection());
    }

    @Test(groups = "controller")
    public void testProcessCreationFormHasErrors() throws Exception {
        mockMvc.perform(post("/owners/new")
            .param("firstName", "Joe")
            .param("lastName", "Bloggs")
            .param("city", "London"))
            .andExpect(status().isOk())
            .andExpect(model().attributeHasErrors("owner"))
            .andExpect(model().attributeHasFieldErrors("owner", "address"))
            .andExpect(model().attributeHasFieldErrors("owner", "telephone"))
            .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }

    @Test(groups = "controller")
    public void testInitFindForm() throws Exception {
        mockMvc.perform(get("/owners/find"))
            .andExpect(status().isOk())
            .andExpect(view().name("owners/findOwners"));
    }

    @Test(groups = "controller", dependsOnMethods = "testInitFindForm")
    public void testProcessFindFormSuccess() throws Exception {
        mockMvc.perform(get("/owners")
            .param("lastName", "Franklin"))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));
    }

    @Test(groups = "controller")
    public void testFindOwnersByAge() throws Exception {
        mockMvc.perform(get("/owners/byage")
            .param("age", "40"))
            .andExpect(status().isOk())
            .andExpect(view().name("owners/ownersList"));
    }

    @Test(groups = "controller")
    public void testShowOwner() throws Exception {
        mockMvc.perform(get("/owners/{ownerId}", TEST_OWNER_ID))
            .andExpect(status().isOk())
            .andExpect(model().attribute("owner", george))
            .andExpect(view().name("owners/ownerDetails"));
    }

    @Test(groups = "controller", dependsOnMethods = "testShowOwner")
    public void testDeleteOwner() throws Exception {
        mockMvc.perform(get("/owners/{ownerId}/delete", TEST_OWNER_ID))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/owners"))
            .andExpect(flash().attributeExists("message"));

        verify(owners).deleteById(TEST_OWNER_ID);
    }
    

    @DataProvider(name = "ownerData")
    public Object[][] ownerData() {
        return new Object[][] {
            {"John", "Doe", "123 Main St", "New York", "1234567890", 30},
            {"Jane", "Smith", "456 Oak Ave", "Chicago", "0987654321", 25},
            {"Bob", "Johnson", "789 Pine Rd", "Los Angeles", "5551234567", 45}
        };
    }

    @Test(groups = "data", dataProvider = "ownerData")
    public void testOwnerCreationWithDifferentData(String firstName, String lastName, 
                                                  String address, String city, 
                                                  String telephone, Integer age) throws Exception {
        mockMvc.perform(post("/owners/new")
            .param("firstName", firstName)
            .param("lastName", lastName)
            .param("address", address)
            .param("city", city)
            .param("telephone", telephone)
            .param("age", String.valueOf(age)))
            .andExpect(status().is3xxRedirection());
    }

    @AfterMethod
    public void tearDown() {
        System.out.println("Tearing down after test method");
    }

    @AfterClass
    public void tearDownClass() {
        System.out.println("Tearing down OwnerControllerTestNG class");
    }
}
