package org.springframework.samples.petclinic.vet;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VetsTest {

    @Test
    void testGetVetList() {
        Vets vets = new Vets();
        Vet vet = new Vet();
        vet.setFirstName("John");
        vets.getVetList().add(vet);
        assertThat(vets.getVetList()).contains(vet);
    }
}
