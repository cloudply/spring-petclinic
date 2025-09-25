package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerModelTests {

	@Test
	void getPetByNameShouldBeCaseInsensitive() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Fluffy");
		owner.addPet(pet);

		Pet result = owner.getPet("flUfFy");

		assertThat(result).isSameAs(pet);
	}

	@Test
	void getPetWithIgnoreNewShouldNotReturnNewPets() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Buddy");
		// pet is new (no id)
		owner.addPet(pet);

		assertThat(owner.getPet("Buddy", true)).isNull();
		assertThat(owner.getPet("Buddy", false)).isSameAs(pet);
	}

	@Test
	void getPetByIdShouldReturnMatchingPetOrNull() {
		Owner owner = new Owner();

		Pet p1 = new Pet();
		p1.setName("Max");
		owner.addPet(p1);
		p1.setId(123);

		Pet p2 = new Pet();
		p2.setName("Bella");
		owner.addPet(p2);
		p2.setId(456);

		assertThat(owner.getPet(123)).isSameAs(p1);
		assertThat(owner.getPet(999)).isNull();
	}
}
