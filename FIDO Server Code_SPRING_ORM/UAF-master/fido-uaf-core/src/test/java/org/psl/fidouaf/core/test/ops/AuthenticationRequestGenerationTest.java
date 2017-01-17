package org.psl.fidouaf.core.test.ops;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.Test;
import org.psl.fidouaf.core.constants.Constants;
import org.psl.fidouaf.core.crypto.Notary;
import org.psl.fidouaf.core.entity.AuthenticationRequest;
import org.psl.fidouaf.core.ops.AuthenticationRequestGeneration;

import com.google.gson.Gson;

public class AuthenticationRequestGenerationTest {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	Gson gson = new Gson();

	@Test
	public void notNull() {
		AuthenticationRequest authReq = new AuthenticationRequestGeneration()
				.createAuthenticationRequest(new NotaryImpl());
		assertNotNull(authReq);
		logger.info(gson.toJson(authReq));
	}

	@Test
	public void withPolicy() {
		String[] aaids = { "ABCD#ABCD" };
		AuthenticationRequest authReq = new AuthenticationRequestGeneration(
				"https://uaf.ebay.com/uaf/facets", aaids)
				.createAuthenticationRequest(new NotaryImpl());
		assertNotNull(authReq);
		logger.info(gson.toJson(authReq));
	}

	class NotaryImpl implements Notary {

		public boolean verify(String dataToSign, String signature) {
			return signature.startsWith(Constants.TEST_SIGNATURE);
		}

		public String sign(String dataToSign) {
			// For testing
			return Constants.TEST_SIGNATURE;
		}
	}

}
