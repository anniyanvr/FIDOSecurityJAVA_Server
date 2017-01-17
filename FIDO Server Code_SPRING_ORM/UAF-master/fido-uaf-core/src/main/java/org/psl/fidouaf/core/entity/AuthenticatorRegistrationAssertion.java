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

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AuthenticatorRegistrationAssertion {
	public String assertionScheme;
	public String assertion;

	@JsonIgnore
	public List<DisplayPNGCharacteristicsDescriptor> tcDisplayPNGCharacteristics;

	@JsonIgnore
	public List<Extension> exts;

	public String getAssertionScheme() {
		return assertionScheme;
	}

	public void setAssertionScheme(String assertionScheme) {
		this.assertionScheme = assertionScheme;
	}

	public String getAssertion() {
		return assertion;
	}

	public void setAssertion(String assertion) {
		this.assertion = assertion;
	}

	public List<DisplayPNGCharacteristicsDescriptor> getTcDisplayPNGCharacteristics() {
		return tcDisplayPNGCharacteristics;
	}

	public void setTcDisplayPNGCharacteristics(
			List<DisplayPNGCharacteristicsDescriptor> tcDisplayPNGCharacteristics) {
		this.tcDisplayPNGCharacteristics = tcDisplayPNGCharacteristics;
	}

	public List<Extension> getExts() {
		return exts;
	}

	public void setExts(List<Extension> exts) {
		this.exts = exts;
	}

	public AuthenticatorRegistrationAssertion() {

	}

	public AuthenticatorRegistrationAssertion(String assertionScheme,
			String assertion) {
		super();
		this.assertionScheme = assertionScheme;
		this.assertion = assertion;
	}

	public AuthenticatorRegistrationAssertion(
			String assertionScheme,
			String assertion,
			List<DisplayPNGCharacteristicsDescriptor> tcDisplayPNGCharacteristics,
			List<Extension> exts) {
		super();
		this.assertionScheme = assertionScheme;
		this.assertion = assertion;
		this.tcDisplayPNGCharacteristics = tcDisplayPNGCharacteristics;
		this.exts = exts;
	}
}
