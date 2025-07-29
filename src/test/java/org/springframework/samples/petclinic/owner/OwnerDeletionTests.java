package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link OwnerController}'s owner deletion functionality
 */
@WebMvcTest(OwnerController.class)
public class OwnerDeletionTests {

    private static final int TEST_OWNER_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerRepository owners;

    private Owner testOwner;

    @BeforeEach
    void setup() {
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

    @Test
    void testDeleteOwnerSuccess() throws Exception {
        mockMvc.perform(get("/owners/{ownerId}/delete", TEST_OWNER_ID))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/owners"))
            .andExpect(flash().attribute("message", "Owner has been deleted successfully"));

        verify(owners, times(1)).deleteById(TEST_OWNER_ID);
    }

    @Test
    void testDeleteOwnerWithPets() throws Exception {
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

    @Test
    void testDeleteNonExistentOwner() throws Exception {
        // Setup for non-existent owner
        int nonExistentId = 999;
        doThrow(new RuntimeException("Owner not found")).when(owners).deleteById(nonExistentId);

        mockMvc.perform(get("/owners/{ownerId}/delete", nonExistentId))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/owners"))
            .andExpect(flash().attributeExists("message"));
    }
}
