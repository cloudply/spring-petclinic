package org.springframework.samples.petclinic.owner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
	private ContactInfo contactInfo;

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

	public ContactInfo getContactInfo() {
		return this.contactInfo;
	}

	public void setContactInfo(ContactInfo contactInfo) {
		this.contactInfo = contactInfo;
	}

	public List<Pet> getPets() {
		return this.pets;
	}

	public void addPet(Pet pet) {
		if (pet.isNew()) {
			pets.add(pet);
		}
	}

	public boolean hasPetWithName(String name) {
		return pets.stream()
			.filter(pet -> pet.getName() != null)
			.anyMatch(pet -> pet.getName().equalsIgnoreCase(name));
	}

	public Optional<Pet> getPetById(Integer id) {
		return pets.stream()
			.filter(pet -> pet.getId() != null && pet.getId().equals(id))
			.findFirst();
	}

	public Optional<Pet> getPetByName(String name, boolean ignoreNew) {
		String lowerName = name.toLowerCase();
		return pets.stream()
			.filter(pet -> pet.getName() != null && pet.getName().equalsIgnoreCase(lowerName) && (!ignoreNew || !pet.isNew()))
			.findFirst();
	}

	public void addVisit(Integer petId, Visit visit) {
		Assert.notNull(petId, "Pet identifier must not be null!");
		Assert.notNull(visit, "Visit must not be null!");
		Pet pet = getPetById(petId).orElseThrow(() -> new IllegalArgumentException("Invalid Pet identifier!"));
		pet.addVisit(visit);
		System.out.println("Visit added for Pet ID: " + petId + ", Name: " + pet.getName());
	}

	public String getOwnerInfo() {
		return "Owner: " + this.getFirstName() + " " + this.getLastName();
	}

	public String getOwnerDetails() {
		return "Owner Details: " + this.getFirstName() + " " + this.getLastName() + ", Phone: " + this.contactInfo.masked();
	}

	@Override
	public String toString() {
		return new ToStringCreator(this)
			.append("id", this.getId())
			.append("new", this.isNew())
			.append("lastName", this.getLastName())
			.append("firstName", this.getFirstName())
			.append("address", this.address)
			.append("city", this.city)
			.append("contactInfo", this.contactInfo.masked())
			.toString();
	}
}

// ContactInfo.java (Refactored for Primitive Obsession)
package org.springframework.samples.petclinic.owner;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;

public class ContactInfo {
	@NotBlank
	@Pattern(regexp = "\\d{10}", message = "Telephone must be a 10-digit number")
	private final String phoneNumber;

	public ContactInfo(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String masked() {
		return phoneNumber.replaceAll("(\\d{3})(\\d{3})(\\d{4})", "$1-***-****");
	}

	@Override
	public String toString() {
		return masked();
	}
}
