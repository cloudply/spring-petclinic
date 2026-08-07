package org.springframework.samples.petclinic.system;

/**
 * Temporary fixture for KBR-3834: entropy ladder for hardcoded passwords.
 * <p>
 * Purpose: find at what Shannon entropy SAST 2 (esp. Bearer-style secret rules with
 * entropy_greater_than ~3.5) starts reporting findings. Values are approximate Shannon
 * entropy of the string itself (bits/char).
 * <p>
 * Ladder (local variables + fields; fields help distinguish field-only detectors):
 * <ul>
 * <li>~1.59 - "abc"</li>
 * <li>~0.00 - "aaaa"</li>
 * <li>~2.75 - "password"</li>
 * <li>~3.00 - "admin123"</li>
 * <li>~3.17 - "Admin123!"</li>
 * <li>~3.22 - "CorrectHorseBattery"</li>
 * <li>~3.52 - "CorrectHorseBatteryStaple" (near typical 3.5 threshold)</li>
 * <li>~3.68 - "AKIAIOSFODNN7EXAMPLE"</li>
 * <li>~4.17 - "xK9#mQ2vL8pR4nT6wY"</li>
 * </ul>
 * Safe to delete after the experiment.
 */
public final class HardcodedSecretEntropyLadderFixture {

	// Field assignments (some detectors only flag fields, not locals)
	private final String passwordFieldEntropy159 = "abc";

	private final String passwordFieldEntropy000 = "aaaa";

	private final String passwordFieldEntropy275 = "password";

	private final String passwordFieldEntropy300 = "admin123";

	private final String passwordFieldEntropy317 = "Admin123!";

	private final String passwordFieldEntropy322 = "CorrectHorseBattery";

	private final String passwordFieldEntropy352 = "CorrectHorseBatteryStaple";

	private final String passwordFieldEntropy368 = "AKIAIOSFODNN7EXAMPLE";

	private final String passwordFieldEntropy417 = "xK9#mQ2vL8pR4nT6wY";

	private HardcodedSecretEntropyLadderFixture() {
	}

	public static String collectLocalPasswordLadder() {
		String passwordEntropy159 = "abc";
		String passwordEntropy000 = "aaaa";
		String passwordEntropy275 = "password";
		String passwordEntropy300 = "admin123";
		String passwordEntropy317 = "Admin123!";
		String passwordEntropy322 = "CorrectHorseBattery";
		String passwordEntropy352 = "CorrectHorseBatteryStaple";
		String passwordEntropy368 = "AKIAIOSFODNN7EXAMPLE";
		String passwordEntropy417 = "xK9#mQ2vL8pR4nT6wY";
		return String.join(":", passwordEntropy159, passwordEntropy000, passwordEntropy275, passwordEntropy300,
				passwordEntropy317, passwordEntropy322, passwordEntropy352, passwordEntropy368, passwordEntropy417);
	}

	public String collectFieldPasswordLadder() {
		return String.join(":", this.passwordFieldEntropy159, this.passwordFieldEntropy000,
				this.passwordFieldEntropy275, this.passwordFieldEntropy300, this.passwordFieldEntropy317,
				this.passwordFieldEntropy322, this.passwordFieldEntropy352, this.passwordFieldEntropy368,
				this.passwordFieldEntropy417);
	}

}