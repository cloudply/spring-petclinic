package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for {@link OwnerRepository}
 */
@DataJpaTest
class OwnerRepositoryTests {

    @Autowired
    private OwnerRepository ownerRepository;

    @Test
    void testFindByLastName() {
        Page<Owner> owners = this.ownerRepository.findByLastName("Davis", PageRequest.of(0, 10));
        assertThat(owners).hasSize(2);

        owners = this.ownerRepository.findByLastName("Daviss", PageRequest.of(0, 10));
        assertThat(owners).isEmpty();
    }

    @Test
    void testFindByLastNameWithEmptyString() {
        Page<Owner> owners = this.ownerRepository.findByLastName("", PageRequest.of(0, 10));
        assertThat(owners).isNotEmpty();
    }

    @Test
    void testFindByLastNameWithWildcard() {
        Page<Owner> owners = this.ownerRepository.findByLastName("%", PageRequest.of(0, 10));
        assertThat(owners).isNotEmpty();
    }

    @Test
    void testFindById() {
        Owner owner = this.ownerRepository.findById(1);
        assertThat(owner.getFirstName()).isEqualTo("George");
        assertThat(owner.getLastName()).isEqualTo("Franklin");
        assertThat(owner.getPets()).hasSize(1);
        assertThat(owner.getPets().get(0).getType()).isNotNull();
        assertThat(owner.getPets().get(0).getType().getName()).isEqualTo("cat");
    }

    @Test
    void testFindPetTypes() {
        List<PetType> petTypes = this.ownerRepository.findPetTypes();
        assertThat(petTypes).isNotEmpty();
        assertThat(petTypes.get(0).getName()).isNotEmpty();
    }

    @Test
    @Transactional
    void testSaveOwner() {
        Owner owner = new Owner();
        owner.setFirstName("Sam");
        owner.setLastName("Smith");
        owner.setAddress("123 Main St");
        owner.setCity("Weston");
        owner.setTelephone("1234567890");

        this.ownerRepository.save(owner);
        assertThat(owner.getId()).isNotNull();
    }

    @Test
    void testFindAll() {
        Page<Owner> owners = this.ownerRepository.findAll(PageRequest.of(0, 10));
        assertThat(owners).isNotEmpty();
        assertThat(owners.getTotalElements()).isGreaterThan(0);
    }
}
