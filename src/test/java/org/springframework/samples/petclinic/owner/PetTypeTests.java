package org.springframework.samples.petclinic.owner;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link PetType}
 */
class PetTypeTests {

    private PetType petType;

    @BeforeEach
    void setup() {
        petType = new PetType();
        petType.setId(1);
        petType.setName("Cat");
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1, petType.getId());
        assertEquals("Cat", petType.getName());
    }
}
