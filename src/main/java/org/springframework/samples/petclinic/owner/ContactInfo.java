package org.springframework.samples.petclinic.owner;

public class ContactInfo {

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
