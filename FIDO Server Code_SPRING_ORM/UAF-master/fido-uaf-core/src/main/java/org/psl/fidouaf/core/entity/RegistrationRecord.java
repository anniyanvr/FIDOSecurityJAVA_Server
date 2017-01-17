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

public class RegistrationRecord {
	public AuthenticatorRecord authenticator;
	public String PublicKey;
	public String SignCounter;
	public String AuthenticatorVersion;
	public String tcDisplayPNGCharacteristics;
	public String username;
	public String userId;
	public String deviceId;
	public String timeStamp;
	public String status;
	public String attestCert;
	public String attestDataToSign;
	public String attestSignature;
	public String attestVerifiedStatus;

	public AuthenticatorRecord getAuthenticator() {
		return authenticator;
	}

	public void setAuthenticator(AuthenticatorRecord authenticator) {
		this.authenticator = authenticator;
	}

	public String getPublicKey() {
		return PublicKey;
	}

	public void setPublicKey(String publicKey) {
		PublicKey = publicKey;
	}

	public String getSignCounter() {
		return SignCounter;
	}

	public void setSignCounter(String signCounter) {
		SignCounter = signCounter;
	}

	public String getAuthenticatorVersion() {
		return AuthenticatorVersion;
	}

	public void setAuthenticatorVersion(String authenticatorVersion) {
		AuthenticatorVersion = authenticatorVersion;
	}

	public String getTcDisplayPNGCharacteristics() {
		return tcDisplayPNGCharacteristics;
	}

	public void setTcDisplayPNGCharacteristics(
			String tcDisplayPNGCharacteristics) {
		this.tcDisplayPNGCharacteristics = tcDisplayPNGCharacteristics;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAttestCert() {
		return attestCert;
	}

	public void setAttestCert(String attestCert) {
		this.attestCert = attestCert;
	}

	public String getAttestDataToSign() {
		return attestDataToSign;
	}

	public void setAttestDataToSign(String attestDataToSign) {
		this.attestDataToSign = attestDataToSign;
	}

	public String getAttestSignature() {
		return attestSignature;
	}

	public void setAttestSignature(String attestSignature) {
		this.attestSignature = attestSignature;
	}

	public String getAttestVerifiedStatus() {
		return attestVerifiedStatus;
	}

	public void setAttestVerifiedStatus(String attestVerifiedStatus) {
		this.attestVerifiedStatus = attestVerifiedStatus;
	}

}
