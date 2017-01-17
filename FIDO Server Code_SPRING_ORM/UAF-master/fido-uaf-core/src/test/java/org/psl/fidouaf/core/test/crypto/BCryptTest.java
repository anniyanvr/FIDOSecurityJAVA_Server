package org.psl.fidouaf.core.test.crypto;

import static org.junit.Assert.*;

import org.junit.Test;
import org.psl.fidouaf.core.crypto.BCrypt;

public class BCryptTest {

	@Test
	public void basic() {
		String hashpw = BCrypt.hashpw("password", BCrypt.gensalt());
		assertTrue(BCrypt.checkpw("password", hashpw));

		String gensalt = BCrypt.gensalt();
		hashpw = BCrypt.hashpw(gensalt, BCrypt.gensalt());
		assertTrue(BCrypt.checkpw(gensalt, hashpw));
	}

}
