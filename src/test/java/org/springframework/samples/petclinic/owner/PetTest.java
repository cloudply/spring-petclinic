package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class PetTest {

    @Test
    void testBirthDateGetterAndSetter() {
        Pet pet = new Pet();
        LocalDate date = LocalDate.of(2020, 1, 1);
        pet.setBirthDate(date);
        assertThat(pet.getBirthDate()).isEqualTo(date);
    }

    @Test
    void testTypeGetterAndSetter() {
        Pet pet = new Pet();
        PetType type = new PetType();
        type.setName("Dog");
        pet.setType(type);
        assertThat(pet.getType()).isEqualTo(type);
    }

    @Test
    void testVisits() {
        Pet pet = new Pet();
        Visit visit = new Visit();
        pet.addVisit(visit);
        Collection<Visit> visits = pet.getVisits();
        assertThat(visits).contains(visit);
    }
}
