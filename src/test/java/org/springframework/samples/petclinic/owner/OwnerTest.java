package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.Visit;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OwnerTest {

    @Test
    void testAddressCityTelephone() {
        Owner owner = new Owner();
        owner.setAddress("123 Main St");
        owner.setCity("Springfield");
        owner.setTelephone("1234567890");
        assertThat(owner.getAddress()).isEqualTo("123 Main St");
        assertThat(owner.getCity()).isEqualTo("Springfield");
        assertThat(owner.getTelephone()).isEqualTo("1234567890");
    }

    @Test
    void testAddAndGetPet() {
        Owner owner = new Owner();
        Pet pet = new Pet();
        pet.setName("Fluffy");
        owner.addPet(pet);
        assertThat(owner.getPets()).contains(pet);
        assertThat(owner.getPet("Fluffy")).isEqualTo(pet);
    }

    @Test
    void testGetPetById() {
        Owner owner = new Owner();
        Pet pet = new Pet();
        pet.setName("Fluffy");
        pet.setId(5);
        owner.addPet(pet);
        assertThat(owner.getPet(5)).isEqualTo(pet);
        assertThat(owner.getPet(99)).isNull();
    }

    @Test
    void testGetPetWithIgnoreNew() {
        Owner owner = new Owner();
        Pet pet = new Pet();
        pet.setName("Fluffy");
        owner.addPet(pet);
        assertThat(owner.getPet("Fluffy", true)).isEqualTo(pet);
    }

    @Test
    void testAddVisit() {
        Owner owner = new Owner();
        Pet pet = new Pet();
        pet.setId(1);
        owner.addPet(pet);
        Visit visit = new Visit();
        owner.addVisit(1, visit);
        assertThat(pet.getVisits()).contains(visit);
    }

    @Test
    void testAddVisitWithNullPetIdThrows() {
        Owner owner = new Owner();
        assertThrows(IllegalArgumentException.class, () -> owner.addVisit(null, new Visit()));
    }

    @Test
    void testAddVisitWithNullVisitThrows() {
        Owner owner = new Owner();
        Pet pet = new Pet();
        pet.setId(1);
        owner.addPet(pet);
        assertThrows(IllegalArgumentException.class, () -> owner.addVisit(1, null));
    }

    @Test
    void testAddVisitWithInvalidPetIdThrows() {
        Owner owner = new Owner();
        assertThrows(IllegalArgumentException.class, () -> owner.addVisit(99, new Visit()));
    }

    @Test
    void testToString() {
        Owner owner = new Owner();
        owner.setId(1);
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setAddress("123 Main St");
        owner.setCity("Springfield");
        owner.setTelephone("1234567890");
        String str = owner.toString();
        assertThat(str).contains("John").contains("Doe").contains("1234567890");
    }
}
