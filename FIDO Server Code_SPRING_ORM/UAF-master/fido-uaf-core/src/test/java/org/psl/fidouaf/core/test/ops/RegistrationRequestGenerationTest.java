package org.psl.fidouaf.core.test.ops;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.bouncycastle.util.encoders.Base64;
import org.junit.Test;
import org.psl.fidouaf.core.constants.Constants;
import org.psl.fidouaf.core.crypto.Notary;
import org.psl.fidouaf.core.entity.RegistrationRequest;
import org.psl.fidouaf.core.ops.RegistrationRequestGeneration;

import com.google.gson.Gson;

public class RegistrationRequestGenerationTest {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	Gson gson = new Gson();

	@Test
	public void notNull() {
		RegistrationRequest regReq = new RegistrationRequestGeneration()
				.createRegistrationRequest("Username", new NotaryImpl());

		assertNotNull(regReq);
		logger.info(gson.toJson(regReq));
	}

	@Test
	public void basic() {
		Notary notary = new NotaryImpl();
		RegistrationRequest regReq = new RegistrationRequestGeneration()
				.createRegistrationRequest("Username", notary);

		String serverData = regReq.header.serverData;
		serverData = new String(Base64.decode(serverData));
		assertTrue(notary.verify(serverData, serverData));
		assertTrue(RegistrationRequestGeneration.APP_ID
				.equals(regReq.header.appID));
		logger.info(gson.toJson(regReq));
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
