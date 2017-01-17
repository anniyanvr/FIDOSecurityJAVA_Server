/*
 * Copyright 2015 eBay Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.psl.fidouaf.core.ri.client;

import java.util.List;
import java.util.logging.Logger;

import org.psl.fidouaf.core.dao.StorageInterface;
import org.psl.fidouaf.core.entity.AuthenticationRequest;
import org.psl.fidouaf.core.entity.AuthenticationResponse;
import org.psl.fidouaf.core.entity.AuthenticatorRecord;
import org.psl.fidouaf.core.entity.Operation;
import org.psl.fidouaf.core.entity.OperationHeader;
import org.psl.fidouaf.core.entity.RegistrationRecord;
import org.psl.fidouaf.core.entity.RegistrationRequest;
import org.psl.fidouaf.core.entity.RegistrationResponse;
import org.psl.fidouaf.core.entity.Version;
import org.psl.fidouaf.core.ops.AuthenticationResponseProcessing;
import org.psl.fidouaf.core.ops.RegistrationResponseProcessing;

import com.google.gson.Gson;

public class App {

	Logger logger = Logger.getLogger(this.getClass().getName());
	Gson gson = new Gson();
	StorageInterface storage = null;

	/**
	 * Registration Flow
	 * 
	 * @throws Exception
	 */
	public void startRegistration() throws Exception {
		RegistrationRequest req = invokeRegistration();
		logger.info(" : RegistrationRequest obtained :");
		logger.info(" : Reg request : "
				+ gson.toJson(req, RegistrationRequest.class));
		RegistrationRequestProcessing requestProcessor = new RegistrationRequestProcessing();
		RegistrationResponse resp = requestProcessor.processRequest(req);
		logger.info(" : RegistrationResponse created : ");
		logger.info(" : Reg response : "
				+ gson.toJson(resp, RegistrationResponse.class));
		serverSideRegResponseProcessing(resp);
		logger.info(" : RegistrationResponse sent : ");
		logger.info(" : Reg response : "
				+ gson.toJson(resp, RegistrationResponse.class));
	}

	private RegistrationRequest invokeRegistration() {
		RegistrationRequest req = new RegistrationRequest();
		req.header = new OperationHeader();
		req.header.op = Operation.Reg;
		req.header.appID = "https://uaf-test-1.noknoktest.com:8443/SampleApp/uaf/facets";
		req.header.serverData = "IjycjPZYiWMaQ1tKLrJROiXQHmYG0tSSYGjP5mgjsDaM17RQgq0dl3NNDDTx9d-aSR_6hGgclrU2F2Yj-12S67v5VmQHj4eWVseLulHdpk2v_hHtKSvv_DFqL4n2IiUY6XZWVbOnvg";
		req.challenge = "H9iW9yA9aAXF_lelQoi_DhUk514Ad8Tqv0zCnCqKDpo";
		req.username = "apa";
		req.header.upv = new Version(1, 0);
		return req;
	}

	/**
	 * Shows an example on what should be processing on UAF server
	 * 
	 * @param resp
	 * @throws Exception
	 */
	private void serverSideRegResponseProcessing(RegistrationResponse resp)
			throws Exception {
		RegistrationResponseProcessing respProcessing = new RegistrationResponseProcessing();
		List<RegistrationRecord> regRecord = respProcessing
				.processResponse(resp);
		storage = new Storage(regRecord.get(0).PublicKey);
		logger.info(" : Reg records : "
				+ gson.toJson(regRecord, RegistrationRecord[].class));

	}

	/**
	 * Authentication Flow
	 * 
	 * @return
	 * @throws Exception
	 */
	public String uafAuthentication() throws Exception {
		AuthenticationRequest req = invokeAuthentication();
		logger.info(" : AuthenticationRequest obtained : ");
		logger.info(" : Auth request : "
				+ gson.toJson(req, AuthenticationRequest.class));
		AuthenticationRequestProcessing authProcessor = new AuthenticationRequestProcessing();
		AuthenticationResponse resp = authProcessor.processRequest(req);
		logger.info(" : AuthenticationResponse created : ");
		logger.info(" : Auth response : "
				+ gson.toJson(resp, AuthenticationResponse.class));
		String accessToken = serverSideAuthResponseProcessing(resp);
		return accessToken;
	}

	private AuthenticationRequest invokeAuthentication() {
		AuthenticationRequest req = new AuthenticationRequest();
		req.header = new OperationHeader();
		req.header.op = Operation.Auth;
		req.header.appID = "https://uaf-test-1.noknoktest.com:8443/SampleApp/uaf/facets";
		req.header.serverData = "5s7n8-7_LDAtRIKKYqbAtTTOezVKCjl2mPorYzbpxRrZ-_3wWroMXsF_pLYjNVm_l7bplAx4bkEwK6ibil9EHGfdfKOQ1q0tyEkNJFOgqdjVmLioroxgThlj8Istpt7q";
		req.challenge = "HQ1VkTUQC1NJDOo6OOWdxewrb9i5WthjfKIehFxpeuU";
		req.header.upv = new Version(1, 0);
		return req;
	}

	/**
	 * Shows an example on what should be processing on UAF server
	 * 
	 * @param resp
	 * @throws Exception
	 */
	private String serverSideAuthResponseProcessing(AuthenticationResponse resp)
			throws Exception {
		AuthenticationResponseProcessing respProcessing = new AuthenticationResponseProcessing();

		List<AuthenticatorRecord> authRec = respProcessing
				.verify(resp, storage);
		logger.info(" : Auth records : "
				+ gson.toJson(authRec, AuthenticatorRecord[].class));
		if (authRec != null && authRec.get(0).getAAID() != null) {
			return "<access_token_goes_here>";
		}
		return null;
	}

	class Storage implements StorageInterface {

		private String b64PubKey;

		public Storage(String b64PubKey) {
			this.b64PubKey = b64PubKey;

		}

		public void update(List<RegistrationRecord> records) {
		}

		public void storeServerDataString(String username,
				String serverDataString) {
		}

		public void store(RegistrationRecord[] records) {
		}

		public RegistrationRecord readRegistrationRecord(String key) {
			RegistrationRecord r = new RegistrationRecord();
			r.PublicKey = b64PubKey;
			return r;
		}

		public String getUsername(String serverDataString) {
			return null;
		}

		@Override
		public RegistrationRecord readRegistrationRecordfromDB(String string) {
			// TODO Auto-generated method stub
			return null;
		}

	}

}
