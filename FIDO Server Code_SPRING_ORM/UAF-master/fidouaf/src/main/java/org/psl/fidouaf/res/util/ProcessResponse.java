/*
 * Copyright 2016 Persistent Systems Limited.
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

package org.psl.fidouaf.res.util;

import java.util.ArrayList;
import java.util.List;

import org.psl.fidouaf.core.constants.Constants;
import org.psl.fidouaf.core.entity.AuthenticationResponse;
import org.psl.fidouaf.core.entity.AuthenticatorRecord;
import org.psl.fidouaf.core.entity.RegistrationRecord;
import org.psl.fidouaf.core.entity.RegistrationResponse;
import org.psl.fidouaf.core.ops.AuthenticationResponseProcessing;
import org.psl.fidouaf.core.ops.RegistrationResponseProcessing;
import org.psl.fidouaf.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;

public class ProcessResponse {

	@Autowired
	private RegistrationService registrationService;

	public List<AuthenticatorRecord> processAuthResponse(
			AuthenticationResponse resp) {
		List<AuthenticatorRecord> result = new ArrayList<AuthenticatorRecord>();
		try {
			result = new AuthenticationResponseProcessing(
					Constants.SERVER_DATA_EXPIRY_IN_MS, NotaryImpl.getInstance()).verify(
					resp, StorageImpl.getInstance());
		} catch (Exception e) {
			System.out
					.println("!!!!!!!!!!!!!!!!!!!..............................."
							+ e.getMessage());
			result.get(0).status = e.getMessage();
		}
		return result;
	}

	public List<RegistrationRecord> processRegResponse(RegistrationResponse resp) {
		List<RegistrationRecord> result = new ArrayList<RegistrationRecord>();
		try {
			result = new RegistrationResponseProcessing(
					Constants.SERVER_DATA_EXPIRY_IN_MS, NotaryImpl.getInstance())
					.processResponse(resp);
		} catch (Exception e) {
			System.out
					.println("!!!!!!!!!!!!!!!!!!!..............................."
							+ e.getMessage());
			result.get(0).status = e.getMessage();
		}
		return result;
	}
}