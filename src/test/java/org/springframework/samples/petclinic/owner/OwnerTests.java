package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Owner}
 */
class OwnerTests {

    private Owner owner;

    @BeforeEach
    void setup() {
        owner = new Owner();
        owner.setId(1);
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setAddress("123 Main St");
        owner.setCity("Springfield");
        owner.setTelephone("1234567890");
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1, owner.getId());
        assertEquals("John", owner.getFirstName());
        assertEquals("Doe", owner.getLastName());
        assertEquals("123 Main St", owner.getAddress());
        assertEquals("Springfield", owner.getCity());
        assertEquals("1234567890", owner.getTelephone());
    }

    @Test
    void testPetsInitializedAsEmptyList() {
        assertNotNull(owner.getPets());
        assertTrue(owner.getPets().isEmpty());
    }

    @Test
    void testAddPet() {
        Pet pet = new Pet();
        pet.setName("Fluffy");
        
        owner.addPet(pet);
        
        assertEquals(1, owner.getPets().size());
        assertEquals("Fluffy", owner.getPets().get(0).getName());
    }

    @Test
    void testAddPetDoesNotAddNonNewPet() {
        Pet pet = new Pet();
        pet.setId(1);
        pet.setName("Fluffy");
        
        owner.addPet(pet);
        
        assertEquals(0, owner.getPets().size());
    }

    @Test
    void testGetPetByName() {
        Pet pet1 = new Pet();
        pet1.setName("Fluffy");
        
        Pet pet2 = new Pet();
        pet2.setName("Buddy");
        
        owner.addPet(pet1);
        owner.addPet(pet2);
        
        Pet foundPet = owner.getPet("Fluffy");
        assertNotNull(foundPet);
        assertEquals("Fluffy", foundPet.getName());
        
        Pet notFoundPet = owner.getPet("Rex");
        assertNull(notFoundPet);
    }

    @Test
    void testGetPetByNameIgnoreCase() {
        Pet pet = new Pet();
        pet.setName("Fluffy");
        
        owner.addPet(pet);
        
        Pet foundPet = owner.getPet("fluffy");
        assertNotNull(foundPet);
        assertEquals("Fluffy", foundPet.getName());
    }

    @Test
    void testGetPetById() {
        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Fluffy");
        
        Pet pet2 = new Pet();
        pet2.setId(2);
        pet2.setName("Buddy");
        
        // Need to add pets directly to the list since they have IDs
        List<Pet> pets = new ArrayList<>();
        pets.add(pet1);
        pets.add(pet2);
        owner.setPets(pets);
        
        Pet foundPet = owner.getPet(1);
        assertNotNull(foundPet);
        assertEquals("Fluffy", foundPet.getName());
        
        Pet notFoundPet = owner.getPet(3);
        assertNull(notFoundPet);
    }

    @Test
    void testToString() {
        String ownerString = owner.toString();
        assertThat(ownerString).contains("John");
        assertThat(ownerString).contains("Doe");
        assertThat(ownerString).contains("123 Main St");
        assertThat(ownerString).contains("Springfield");
        assertThat(ownerString).contains("1234567890");
    }

    @Test
    void testAddVisit() {
        Pet pet = new Pet();
        pet.setId(1);
        pet.setName("Fluffy");
        
        List<Pet> pets = new ArrayList<>();
        pets.add(pet);
        owner.setPets(pets);
        
        Visit visit = new Visit();
        visit.setDescription("Annual checkup");
        
        owner.addVisit(1, visit);
        
        assertEquals(1, pet.getVisits().size());
        assertEquals("Annual checkup", pet.getVisits().iterator().next().getDescription());
    }
}
