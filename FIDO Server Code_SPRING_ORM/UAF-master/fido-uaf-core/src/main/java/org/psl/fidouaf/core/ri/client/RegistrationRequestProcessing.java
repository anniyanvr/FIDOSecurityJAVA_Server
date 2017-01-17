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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.psl.fidouaf.core.constants.Constants;
import org.psl.fidouaf.core.entity.AuthenticatorRegistrationAssertion;
import org.psl.fidouaf.core.entity.FinalChallengeParams;
import org.psl.fidouaf.core.entity.RegistrationRequest;
import org.psl.fidouaf.core.entity.RegistrationResponse;

import com.google.gson.Gson;

public class RegistrationRequestProcessing {

	private String assertion;

	public RegistrationRequestProcessing() {
		// Example from spec document
		assertion = "AT7uAgM-sQALLgkAQUJDRCNBQkNEDi4HAAABAQEAAAEKLiAA9tBzZC64ecgVQBGSQb5QtEIPC8-Vav4HsHLZDflLaugJLiAAZMCPn92yHv1Ip-iCiBb6i4ADq6ZOv569KFQCvYSJfNgNLggAAQAAAAEAAAAMLkEABJsvEtUsVKh7tmYHhJ2FBm3kHU-OCdWiUYVijgYa81MfkjQ1z6UiHbKP9_nRzIN9anprHqDGcR6q7O20q_yctZAHPjUCBi5AACv8L7YlRMx10gPnszGO6rLFqZFmmRkhtV0TIWuWqYxd1jO0wxam7i5qdEa19u4sfpHFZ9RGI_WHxINkH8FfvAwFLu0BMIIB6TCCAY8CAQEwCQYHKoZIzj0EATB7MQswCQYDVQQGEwJVUzELMAkGA1UECAwCQ0ExCzAJBgNVBAcMAlBBMRAwDgYDVQQKDAdOTkwsSW5jMQ0wCwYDVQQLDAREQU4xMRMwEQYDVQQDDApOTkwsSW5jIENBMRwwGgYJKoZIhvcNAQkBFg1ubmxAZ21haWwuY29tMB4XDTE0MDgyODIxMzU0MFoXDTE3MDUyNDIxMzU0MFowgYYxCzAJBgNVBAYTAlVTMQswCQYDVQQIDAJDQTEWMBQGA1UEBwwNU2FuIEZyYW5jaXNjbzEQMA4GA1UECgwHTk5MLEluYzENMAsGA1UECwwEREFOMTETMBEGA1UEAwwKTk5MLEluYyBDQTEcMBoGCSqGSIb3DQEJARYNbm5sQGdtYWlsLmNvbTBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IABCGBt3CIjnDowzSiF68C2aErYXnDUsWXOYxqIPim0OWg9FFdUYCa6AgKjn1R99Ek2d803sGKROivnavmdVH-SnEwCQYHKoZIzj0EAQNJADBGAiEAzAQujXnSS9AIAh6lGz6ydypLVTsTnBzqGJ4ypIqy_qUCIQCFsuOEGcRV-o4GHPBph_VMrG3NpYh2GKPjsAim_cSNmQ";
	}

	public RegistrationRequestProcessing(String assertion) {
		this.assertion = assertion;
	}

	public RegistrationResponse processRequest(RegistrationRequest regRequest) {
		RegistrationResponse response = new RegistrationResponse();
		Gson gson = new Gson();
		int[] errCodes = validate(regRequest);
		if (errCodes != null) {
			return setValidationError(response, errCodes);
		}

		setAppId(regRequest, response);
		// response.header = new OperationHeader();
		response.getHeader().setServerData(regRequest.header.serverData);
		response.getHeader().setOp(regRequest.header.op);
		response.getHeader().setUpv(regRequest.header.upv);
		FinalChallengeParams fcParams = new FinalChallengeParams();
		fcParams.appID = regRequest.header.appID;
		fcParams.facetID = Constants.FACET_ID;
		fcParams.challenge = regRequest.challenge;
		response.setFcParams(Base64.encodeBase64URLSafeString(gson.toJson(
				fcParams).getBytes()));
		setAssertions(response);
		return response;
	}

	private void setAssertions(RegistrationResponse response) {
		AuthenticatorRegistrationAssertion assertion = new AuthenticatorRegistrationAssertion();
		assertion.assertionScheme = "UAFV1TLV";
		assertion.assertion = this.assertion;
		List<AuthenticatorRegistrationAssertion> assertions = new ArrayList<AuthenticatorRegistrationAssertion>();
		assertions.add(assertion);
		response.setAssertions(assertions);
	}

	private void setAppId(RegistrationRequest regRequest,
			RegistrationResponse response) {
		if (regRequest.header.appID == null
				&& regRequest.header.appID.isEmpty()) {
			response.getHeader().setAppID(Constants.APP_ID);
		} else {
			setAppID(regRequest, response);
		}
	}

	private RegistrationResponse setValidationError(
			RegistrationResponse response, int[] errCodes) {
		// TODO Auto-generated method stub
		return null;
	}

	private int[] validate(RegistrationRequest regRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	private void setAppID(RegistrationRequest regRequest,
			RegistrationResponse response) {
		// TODO Auto-generated method stub
	}
}
