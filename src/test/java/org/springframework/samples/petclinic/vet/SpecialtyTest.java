package org.springframework.samples.petclinic.vet;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyTest {

    @Test
    void testSpecialtyName() {
        Specialty specialty = new Specialty();
        specialty.setName("Dentistry");
        assertThat(specialty.getName()).isEqualTo("Dentistry");
    }
}
