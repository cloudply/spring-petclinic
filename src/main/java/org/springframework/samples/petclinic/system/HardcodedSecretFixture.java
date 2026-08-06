package org.springframework.samples.petclinic.system;

/**
 * Temporary fixture for KBR-3834: verify whether SAST 2 detects low-entropy hardcoded
 * passwords (variable-name + literal pattern). Safe to delete after the experiment.
 */
public final class HardcodedSecretFixture {

	private HardcodedSecretFixture() {
	}

	public static String getCredentials() {
		String pwd = "abc";
		String password = "admin123";
		return pwd + ":" + password;
	}

}