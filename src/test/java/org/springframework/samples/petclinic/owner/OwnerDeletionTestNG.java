package org.springframework.samples.petclinic.owner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TestNG test class for {@link OwnerController}'s owner deletion functionality
 */
@WebMvcTest(OwnerController.class)
public class OwnerDeletionTestNG {

    private static final int TEST_OWNER_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerRepository owners;

    private Owner testOwner;

    @BeforeClass(groups = "deletion")
    public void setupClass() {
        System.out.println("Setting up OwnerDeletionTestNG class");
    }

    @BeforeMethod(groups = "deletion")
    public void setup() {
        testOwner = new Owner();
        testOwner.setId(TEST_OWNER_ID);
        testOwner.setFirstName("George");
        testOwner.setLastName("Franklin");
        testOwner.setAddress("110 W. Liberty St.");
        testOwner.setCity("Madison");
        testOwner.setTelephone("6085551023");
        testOwner.setAge(40);

        when(this.owners.findById(TEST_OWNER_ID)).thenReturn(testOwner);
    }

    @Test(groups = "deletion", description = "Test successful owner deletion")
    public void testDeleteOwnerSuccess() throws Exception {
        mockMvc.perform(get("/owners/{ownerId}/delete", TEST_OWNER_ID))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/owners"))
            .andExpect(flash().attribute("message", "Owner has been deleted successfully"));

        verify(owners, times(1)).deleteById(TEST_OWNER_ID);
    }

    @Test(groups = "deletion", description = "Test deletion of owner with pets")
    public void testDeleteOwnerWithPets() throws Exception {
        // Add a pet to the owner
        Pet pet = new Pet();
        pet.setId(1);
        pet.setName("Max");
        testOwner.addPet(pet);

        // The deletion should still work even with pets
        mockMvc.perform(get("/owners/{ownerId}/delete", TEST_OWNER_ID))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/owners"))
            .andExpect(flash().attribute("message", "Owner has been deleted successfully"));

        verify(owners, times(1)).deleteById(TEST_OWNER_ID);
    }

    @DataProvider(name = "ownerIds")
    public Object[][] ownerIds() {
        return new Object[][] {
            {999},  // Non-existent ID
            {-1},   // Invalid ID
            {0}     // Edge case ID
        };
    }

    @Test(groups = "deletion", dataProvider = "ownerIds", 
          description = "Test deletion with various problematic IDs")
    public void testDeleteProblematicOwners(int ownerId) throws Exception {
        // Setup for problematic owner IDs
        doThrow(new RuntimeException("Owner not found")).when(owners).deleteById(ownerId);

        mockMvc.perform(get("/owners/{ownerId}/delete", ownerId))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/owners"));
    }

    @AfterMethod(groups = "deletion")
    public void tearDown() {
        System.out.println("Tearing down after test method");
    }

    @AfterClass(groups = "deletion")
    public void tearDownClass() {
        System.out.println("Tearing down OwnerDeletionTestNG class");
    }
}
