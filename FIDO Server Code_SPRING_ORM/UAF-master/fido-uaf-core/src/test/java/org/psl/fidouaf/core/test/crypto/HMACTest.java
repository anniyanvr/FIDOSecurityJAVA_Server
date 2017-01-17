package org.psl.fidouaf.core.test.crypto;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.security.InvalidParameterException;

import org.junit.Test;
import org.psl.fidouaf.core.crypto.HMAC;

public class HMACTest {

	private String result;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	@Test
	public void testSignNotNullNotEqual() {
		try {
			byte[] Signature = HMAC.sign("Some_String", "Password");
			assertNotNull(Signature);
			assertTrue(!Signature.toString().equals("SOME_STRING"));
		} catch (Exception e) {
			assertTrue(e instanceof Exception);
		}
	}

	@Test
	public void nullPassword() {
		setResult("");
		try {
			setResult(HMAC.sign("Some_String", null).toString());
		} catch (Exception e) {
			assertTrue(e instanceof InvalidParameterException);
		}
	}

	@Test
	public void nullInputString() {
		setResult("");
		try {
			setResult(HMAC.sign(null, "Password").toString());
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e instanceof InvalidParameterException);
		}
	}
}
