package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for Owner domain behavior that is not covered by controller/service tests.
 */
class OwnerTests {

	@Test
	void getPetByNameShouldBeCaseInsensitive() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Fido");

		// pet is new (no id), so addPet should add it
		owner.addPet(pet);

		assertThat(owner.getPet("fido")).isSameAs(pet);
		assertThat(owner.getPet("FIDO")).isSameAs(pet);
		assertThat(owner.getPet("unknown")).isNull();
	}

	@Test
	void getPetByNameShouldRespectIgnoreNewFlag() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Bella");

		// When pet is new (id == null), ignoreNew=true should skip it
		owner.addPet(pet);
		assertThat(owner.getPet("Bella", true)).isNull();

		// Mark existing (assign id) and then it should be found even when ignoreNew=true
		pet.setId(1);
		assertThat(owner.getPet("Bella", true)).isSameAs(pet);
	}

	@Test
	void getPetByIdShouldReturnMatchingPetOrNull() {
		Owner owner = new Owner();

		Pet p1 = new Pet();
		p1.setName("Max");
		owner.addPet(p1);
		p1.setId(11);

		Pet p2 = new Pet();
		p2.setName("Luna");
		owner.addPet(p2);
		p2.setId(22);

		assertThat(owner.getPet(11)).isSameAs(p1);
		assertThat(owner.getPet(22)).isSameAs(p2);
		assertThat(owner.getPet(99)).isNull();
	}

	@Test
	void addPetShouldOnlyAddWhenNew() {
		Owner owner = new Owner();

		Pet newPet = new Pet();
		newPet.setName("Charlie");
		assertThat(owner.getPets()).hasSize(0);
		owner.addPet(newPet);
		assertThat(owner.getPets()).hasSize(1).contains(newPet);

		Pet existingPet = new Pet();
		existingPet.setName("Milo");
		existingPet.setId(100); // not new
		owner.addPet(existingPet);
		// Should not add an existing pet per Owner.addPet contract
		assertThat(owner.getPets()).hasSize(1).doesNotContain(existingPet);
	}

	@Test
	void addVisitShouldValidateInputsAndAddToCorrectPet() {
		Owner owner = new Owner();

		// create and attach a pet
		Pet pet = new Pet();
		pet.setName("Rocky");
		owner.addPet(pet);
		pet.setId(7);

		// null petId
		assertThatThrownBy(() -> owner.addVisit(null, new Visit()))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Pet identifier must not be null");

		// null visit
		assertThatThrownBy(() -> owner.addVisit(7, null))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Visit must not be null");

		// invalid pet id
		assertThatThrownBy(() -> owner.addVisit(99, new Visit()))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Invalid Pet identifier");

		// valid add
		Visit visit = new Visit();
		assertThat(pet.getVisits()).isEmpty();
		owner.addVisit(7, visit);
		assertThat(pet.getVisits()).containsExactly(visit);
	}

	@Test
	void addPetShouldSetBackReferenceOwnerOnPet() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Buddy");

		owner.addPet(pet);

		assertThat(pet.getOwner()).isSameAs(owner);
	}

	@Test
	void getPetByNameNullShouldThrowNullPointerException() {
		Owner owner = new Owner();
		assertThatThrownBy(() -> owner.getPet(null))
			.isInstanceOf(NullPointerException.class);
	}
}
