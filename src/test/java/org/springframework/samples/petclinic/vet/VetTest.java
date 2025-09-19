package org.springframework.samples.petclinic.vet;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VetTest {

    @Test
    void testAddAndGetSpecialties() {
        Vet vet = new Vet();
        Specialty specialty = new Specialty();
        specialty.setName("Dentistry");
        vet.addSpecialty(specialty);
        List<Specialty> specialties = vet.getSpecialties();
        assertThat(specialties).contains(specialty);
        assertThat(vet.getNrOfSpecialties()).isEqualTo(1);
    }

    @Test
    void testSetSpecialtiesInternal() {
        Vet vet = new Vet();
        Specialty specialty = new Specialty();
        specialty.setName("Surgery");
        vet.setSpecialtiesInternal(new java.util.HashSet<>(List.of(specialty)));
        assertThat(vet.getSpecialties()).contains(specialty);
    }
}
