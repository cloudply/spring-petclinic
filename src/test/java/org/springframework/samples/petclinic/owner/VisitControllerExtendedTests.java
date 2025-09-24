package org.springframework.samples.petclinic.owner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Extended tests for {@link VisitController}
 */
@WebMvcTest(VisitController.class)
class VisitControllerExtendedTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerRepository owners;

    private Owner owner;
    private Pet pet;

    @BeforeEach
    void setup() {
        owner = new Owner();
        owner.setId(1);
        owner.setFirstName("John");
        owner.setLastName("Doe");

        pet = new Pet();
        pet.setId(1);
        pet.setName("Fluffy");
        pet.setBirthDate(LocalDate.now());
        
        PetType petType = new PetType();
        petType.setName("Cat");
        pet.setType(petType);
        
        owner.addPet(pet);

        given(this.owners.findById(1)).willReturn(owner);
        given(this.owners.findById(eq(1))).willReturn(owner);
    }

    @Test
    void testInitNewVisitFormWithInvalidOwnerId() throws Exception {
        given(this.owners.findById(99)).willReturn(null);
        
        mockMvc.perform(get("/owners/99/pets/1/visits/new"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testInitNewVisitFormWithInvalidPetId() throws Exception {
        mockMvc.perform(get("/owners/1/pets/99/visits/new"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testProcessNewVisitFormWithFutureDate() throws Exception {
        mockMvc.perform(post("/owners/1/pets/1/visits/new")
            .param("date", LocalDate.now().plusDays(1).toString())
            .param("description", "Future checkup"))
            .andExpect(status().isOk())
            .andExpect(model().attributeHasFieldErrors("visit", "date"))
            .andExpect(view().name("pets/createOrUpdateVisitForm"));
    }

    @Test
    void testProcessNewVisitFormWithEmptyDescription() throws Exception {
        mockMvc.perform(post("/owners/1/pets/1/visits/new")
            .param("date", LocalDate.now().toString())
            .param("description", ""))
            .andExpect(status().isOk())
            .andExpect(model().attributeHasFieldErrors("visit", "description"))
            .andExpect(view().name("pets/createOrUpdateVisitForm"));
    }

    @Test
    void testProcessNewVisitFormSuccess() throws Exception {
        mockMvc.perform(post("/owners/1/pets/1/visits/new")
            .param("date", LocalDate.now().toString())
            .param("description", "Regular checkup"))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/owners/{ownerId}"));
        
        verify(owners).save(any(Owner.class));
    }
}
