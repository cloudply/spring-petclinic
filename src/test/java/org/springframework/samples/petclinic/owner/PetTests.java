package org.springframework.samples.petclinic.owner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Pet}
 */
class PetTests {

    private Pet pet;

    @BeforeEach
    void setup() {
        pet = new Pet();
        pet.setId(1);
        pet.setName("Fluffy");
        pet.setBirthDate(LocalDate.of(2020, 1, 1));
        
        PetType petType = new PetType();
        petType.setName("Cat");
        pet.setType(petType);
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1, pet.getId());
        assertEquals("Fluffy", pet.getName());
        assertEquals(LocalDate.of(2020, 1, 1), pet.getBirthDate());
        assertEquals("Cat", pet.getType().getName());
    }

    @Test
    void testVisitsInitializedAsEmptyCollection() {
        assertNotNull(pet.getVisits());
        assertTrue(pet.getVisits().isEmpty());
    }

    @Test
    void testAddVisit() {
        Visit visit = new Visit();
        visit.setDate(LocalDate.now());
        visit.setDescription("Annual checkup");
        
        pet.addVisit(visit);
        
        assertEquals(1, pet.getVisits().size());
        Visit addedVisit = pet.getVisits().iterator().next();
        assertEquals("Annual checkup", addedVisit.getDescription());
    }
}
