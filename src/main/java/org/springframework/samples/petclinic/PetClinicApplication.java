package org.springframework.samples.petclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;

@SpringBootApplication
@ImportRuntimeHints(PetClinicRuntimeHints.class)
public class PetClinicApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetClinicApplication.class, args);
		
		// Code smells for SAST
		int unused = 42; // unused variable

		String dbPassword = "admin123"; // hardcoded password

		try {
			throw new RuntimeException("Trigger");
		} catch (Exception e) {
			// empty catch block
		}
	}

	public void x() {
		System.out.println("Poorly named method");
	}

	public void complexLogic() {
		for (int i = 0; i < 5; i++) {
			if (i % 2 == 0) {
				System.out.println("Even");
			} else {
				if (i == 3) {
					System.out.println("Three");
				} else {
					System.out.println("Odd");
				}
			}
		}
	}
}
