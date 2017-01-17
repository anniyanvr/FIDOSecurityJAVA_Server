package org.psl.fidouaf.core.test.crypto;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.psl.fidouaf.core.constants.Constants;
import org.psl.fidouaf.core.crypto.SHA;

public class SHATest {
	private String sha256;

	public String getSha256() {
		return sha256;
	}

	public void setSha256(String sha256) {
		this.sha256 = sha256;
	}

	@Test
	public void basic() {
		String sha256 = SHA.sha256(Constants.SOME_STRING);
		assertNotNull(sha256);
		assertTrue(!sha256.equals(Constants.SOME_STRING));
	}

	@Test
	public void uniqeResult() {
		String sha1 = SHA.sha256(Constants.SOME_STRING);
		String sha2 = SHA.sha256(Constants.SOME_OTHER_STRING);
		assertTrue(!sha1.equals(sha2));
	}

	@Test
	public void deterministic() {
		String sha1 = SHA.sha256(Constants.SOME_STRING);
		assertTrue(sha1.equals(SHA.sha256(Constants.SOME_STRING)));
	}

	@Test
	public void nullInput() {
		try {
			setSha256(SHA.sha256(null));
		} catch (Exception e) {
			assertTrue(e instanceof RuntimeException);
		}
	}
}
