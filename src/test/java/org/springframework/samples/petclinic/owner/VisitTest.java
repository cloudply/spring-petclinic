package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class VisitTest {

    @Test
    void testDefaultConstructorSetsDate() {
        Visit visit = new Visit();
        assertThat(visit.getDate()).isNotNull();
    }

    @Test
    void testDateGetterAndSetter() {
        Visit visit = new Visit();
        LocalDate date = LocalDate.of(2022, 2, 2);
        visit.setDate(date);
        assertThat(visit.getDate()).isEqualTo(date);
    }

    @Test
    void testDescriptionGetterAndSetter() {
        Visit visit = new Visit();
        visit.setDescription("Checkup");
        assertThat(visit.getDescription()).isEqualTo("Checkup");
    }
}
