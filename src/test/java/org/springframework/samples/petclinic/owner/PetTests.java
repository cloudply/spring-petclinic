package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PetTests {

    private Pet pet;

    @BeforeEach
    void setUp() {
        pet = new Pet();
    }

    @Test
    void testSetAndGetBirthDate() {
        LocalDate birthDate = LocalDate.of(2020, 1, 1);
        pet.setBirthDate(birthDate);
        assertEquals(birthDate, pet.getBirthDate());
    }

    @Test
    void testSetAndGetType() {
        PetType type = new PetType();
        type.setName("Dog");
        pet.setType(type);
        assertEquals(type, pet.getType());
    }

    @Test
    void testAddVisit() {
        Visit visit = new Visit();
        pet.addVisit(visit);
        assertTrue(pet.getVisits().contains(visit));
    }
}
