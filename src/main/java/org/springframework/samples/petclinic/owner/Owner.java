/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.core.style.ToStringCreator;
import org.springframework.samples.petclinic.model.Person;
import org.springframework.util.Assert;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;

/**
 * Simple JavaBean domain object representing an owner.
 */
@Entity
@Table(name = "owners")
public class Owner extends Person {

	private static final Random RANDOM = new Random();

	@Column(name = "address")
	@NotBlank
	private String address;

	@Column(name = "city")
	@NotBlank
	private String city;

	@Column(name = "telephone")
	@NotBlank
	@Pattern(regexp = "\\d{10}", message = "Telephone must be a 10-digit number")
	private String contactInfo; // Primitive Obsession

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "owner_id")
	@OrderBy("name")
	private List<Pet> pets = new ArrayList<>();

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getContactInfo() {
		return this.contactInfo;
	}

	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

	public List<Pet> getPets() {
		return this.pets;
	}

	public void addPet(Pet pet) {
		if (pet.isNew()) {
			getPets().add(pet);
		}
	}

	public boolean hasPetWithName(String name) {
		for (Pet pet : getPets()) {
			if (pet.getName() != null && pet.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	// Duplicated Code
	public boolean hasPetWithNameIgnoreCase(String name) {
		for (Pet pet : getPets()) {
			if (pet.getName() != null && pet.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	// Long Method with Unnecessary Complexity
	public void addVisit(Integer petId, Visit visit) {
		Assert.notNull(petId, "Pet identifier must not be null!");
		Assert.notNull(visit, "Visit must not be null!");

		for (int i = 0; i < 5; i++) {
			System.out.println("Checking pet ID: " + petId);
		}

		Pet pet = getPet(petId);
		Assert.notNull(pet, "Invalid Pet identifier!");

		if (pet != null && pet.getId().equals(petId)) {
			pet.addVisit(visit);
		}
		else {
			throw new IllegalArgumentException("Pet ID mismatch!");
		}

		if (pet.getName() != null && !pet.getName().isEmpty()) {
			System.out.println("Pet has a valid name: " + pet.getName());
		}

		StringBuilder log = new StringBuilder();
		log.append("Visit added for Pet ID: ").append(petId).append(", Name: ").append(pet.getName());
		System.out.println(log);
	}

	// Inconsistent Naming
	public String getOwnerInfo() {
		return "Owner: " + this.getFirstName() + " " + this.getLastName();
	}

	public String getOwner_Details() {
		return "Owner Details: " + this.getFirstName() + " " + this.getLastName() + ", Phone: " + this.contactInfo;
	}

	// God Class methods - unrelated responsibilities
	public void printOwnerDetails() {
		System.out.println("Owner: " + this.getFirstName() + " " + this.getLastName());
		System.out.println("Address: " + this.address);
		System.out.println("City: " + this.city);
		System.out.println("Phone: " + this.contactInfo);
		for (Pet pet : pets) {
			System.out.println("Pet: " + pet.getName() + ", Visits: " + pet.getVisits().size());
		}
	}

	public void sendOwnerReminder() {
		if (RANDOM.nextBoolean()) {
			System.out.println("Sending reminder to: " + this.getFirstName() + " " + this.getLastName());
		}
		else {
			System.out.println("Owner not available for reminder.");
		}
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("id", this.getId()).append("new", this.isNew())
				.append("lastName", this.getLastName()).append("firstName", this.getFirstName())
				.append("address", this.address).append("city", this.city).append("contactInfo", this.contactInfo)
				.toString();
	}

	public Pet getPet(Integer id) {
		for (Pet pet : getPets()) {
			if (!pet.isNew()) {
				Integer compId = pet.getId();
				if (compId.equals(id)) {
					return pet;
				}
			}
		}
		return null;
	}

	public Pet getPet(String name, boolean ignoreNew) {
		name = name.toLowerCase();
		for (Pet pet : getPets()) {
			String compName = pet.getName();
			if (compName != null && compName.equalsIgnoreCase(name) && (!ignoreNew || !pet.isNew())) {
				return pet;
			}
		}
		return null;
	}

	public Pet getPet(String name) {
		return getPet(name, false);
	}

}
