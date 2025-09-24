package org.springframework.samples.petclinic.owner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Extended tests for {@link PetController}
 */
@WebMvcTest(value = PetController.class, 
    includeFilters = @ComponentScan.Filter(
                            value = PetTypeFormatter.class, 
                            type = FilterType.ASSIGNABLE_TYPE))
class PetControllerExtendedTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerRepository owners;

    private Owner owner;
    private List<PetType> petTypes;

    @BeforeEach
    void setup() {
        owner = new Owner();
        owner.setId(1);
        owner.setFirstName("John");
        owner.setLastName("Doe");
        
        PetType dog = new PetType();
        dog.setId(1);
        dog.setName("Dog");
        
        PetType cat = new PetType();
        cat.setId(2);
        cat.setName("Cat");
        
        petTypes = new ArrayList<>();
        petTypes.add(dog);
        petTypes.add(cat);
        
        Pet pet = new Pet();
        pet.setId(1);
        pet.setName("Fluffy");
        pet.setType(cat);
        pet.setBirthDate(LocalDate.now().minusYears(1));
        owner.addPet(pet);
        
        given(this.owners.findById(1)).willReturn(owner);
        given(this.owners.findPetTypes()).willReturn(petTypes);
    }

    @Test
    void testInitCreationFormWithInvalidOwnerId() throws Exception {
        given(this.owners.findById(99)).willReturn(null);
        
        mockMvc.perform(get("/owners/99/pets/new"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testProcessCreationFormWithDuplicatePetName() throws Exception {
        mockMvc.perform(post("/owners/1/pets/new")
            .param("name", "Fluffy") // Already exists
            .param("type", "Dog")
            .param("birthDate", LocalDate.now().minusYears(1).toString()))
            .andExpect(status().isOk())
            .andExpect(model().attributeHasErrors("pet"))
            .andExpect(model().attributeHasFieldErrors("pet", "name"))
            .andExpect(view().name("pets/createOrUpdatePetForm"));
    }

    @Test
    void testInitUpdateFormWithInvalidPetId() throws Exception {
        mockMvc.perform(get("/owners/1/pets/99/edit"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testProcessUpdateFormWithInvalidData() throws Exception {
        mockMvc.perform(post("/owners/1/pets/1/edit")
            .param("name", "")
            .param("type", "Cat")
            .param("birthDate", LocalDate.now().toString()))
            .andExpect(status().isOk())
            .andExpect(model().attributeHasErrors("pet"))
            .andExpect(model().attributeHasFieldErrors("pet", "name"))
            .andExpect(view().name("pets/createOrUpdatePetForm"));
    }

    @Test
    void testProcessUpdateFormWithFutureBirthDate() throws Exception {
        mockMvc.perform(post("/owners/1/pets/1/edit")
            .param("name", "Fluffy Updated")
            .param("type", "Cat")
            .param("birthDate", LocalDate.now().plusDays(1).toString()))
            .andExpect(status().isOk())
            .andExpect(model().attributeHasErrors("pet"))
            .andExpect(model().attributeHasFieldErrors("pet", "birthDate"))
            .andExpect(view().name("pets/createOrUpdatePetForm"));
    }

    @Test
    void testProcessUpdateFormSuccess() throws Exception {
        mockMvc.perform(post("/owners/1/pets/1/edit")
            .param("name", "Fluffy Updated")
            .param("type", "Cat")
            .param("birthDate", LocalDate.now().minusYears(2).toString()))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/owners/{ownerId}"));
        
        verify(owners).save(any(Owner.class));
    }
}
