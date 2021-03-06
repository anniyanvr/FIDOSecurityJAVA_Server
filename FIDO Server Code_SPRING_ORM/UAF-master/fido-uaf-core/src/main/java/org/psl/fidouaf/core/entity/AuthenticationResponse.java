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

package org.psl.fidouaf.core.entity;

import java.util.List;

public class AuthenticationResponse {
	private OperationHeader header;
	private String fcParams;
	private List<AuthenticatorSignAssertion> assertions;

	public List<AuthenticatorSignAssertion> getAssertions() {
		return assertions;
	}

	public void setAssertions(List<AuthenticatorSignAssertion> assertions) {
		this.assertions = assertions;
	}

	public OperationHeader getHeader() {
		return header;
	}

	public void setHeader(OperationHeader header) {
		this.header = header;
	}

	public String getFcParams() {
		return fcParams;
	}

	public void setFcParams(String fcParams) {
		this.fcParams = fcParams;
	}

}
