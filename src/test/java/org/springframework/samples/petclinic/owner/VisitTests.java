package org.springframework.samples.petclinic.owner;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Visit}
 */
class VisitTests {

    private Visit visit;
    private LocalDate testDate;

    @BeforeEach
    void setup() {
        testDate = LocalDate.of(2023, 5, 15);
        visit = new Visit();
        visit.setId(1);
        visit.setDate(testDate);
        visit.setDescription("Annual checkup");
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1, visit.getId());
        assertEquals(testDate, visit.getDate());
        assertEquals("Annual checkup", visit.getDescription());
    }
}
