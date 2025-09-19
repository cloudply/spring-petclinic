package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.samples.petclinic.model.Person;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OwnerTests {

    private Owner owner;
    private Pet pet;

    @BeforeEach
    void setUp() {
        owner = new Owner();
        pet = mock(Pet.class);
    }

    @Test
    void testAddPet() {
        when(pet.isNew()).thenReturn(true);
        owner.addPet(pet);
        List<Pet> pets = owner.getPets();
        assertEquals(1, pets.size());
        assertEquals(pet, pets.get(0));
    }

    @Test
    void testGetPetByName() {
        when(pet.getName()).thenReturn("Buddy");
        owner.addPet(pet);
        Pet foundPet = owner.getPet("Buddy");
        assertNotNull(foundPet);
        assertEquals("Buddy", foundPet.getName());
    }

    @Test
    void testGetPetById() {
        when(pet.getId()).thenReturn(1);
        owner.addPet(pet);
        Pet foundPet = owner.getPet(1);
        assertNotNull(foundPet);
        assertEquals(1, foundPet.getId());
    }

    @Test
    void testAddVisit() {
        Visit visit = mock(Visit.class);
        when(pet.getId()).thenReturn(1);
        owner.addPet(pet);
        owner.addVisit(1, visit);
        verify(pet, times(1)).addVisit(visit);
    }
}
