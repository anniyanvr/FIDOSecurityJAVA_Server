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

package org.psl.fidouaf.res.util;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.psl.fidouaf.core.constants.Constants;
import org.psl.fidouaf.core.entity.AuthenticatorRecord;
import org.psl.fidouaf.core.entity.DeregisterAuthenticator;
import org.psl.fidouaf.core.entity.DeregistrationRequest;
import org.psl.fidouaf.entity.Device;
import org.psl.fidouaf.entity.PublicKeyDetails;
import org.psl.fidouaf.entity.Registration;
import org.psl.fidouaf.entity.TransactionDetails;
import org.psl.fidouaf.entity.VendorDetails;
import org.psl.fidouaf.stats.Dash;

public class DeregRequestProcessor {

	// private static final String SUCCESS = "Success";

	public String process(List<DeregistrationRequest> deregistrationRequest) {
		try {
			DeregistrationRequest deregRequest = deregistrationRequest.get(0);
			Dash.getInstance().stats
					.put(Constants.LAST_DEREG_REQ, deregRequest);
			AuthenticatorRecord authRecord = new AuthenticatorRecord();
			for (DeregisterAuthenticator authenticator : deregRequest.authenticators) {
				authRecord.AAID = authenticator.aaid;
				authRecord.KeyID = authenticator.keyID;
				try {
					String Key = authRecord.toString();
					String deviceId = null;
					int rpAccountId = 0;

					Session session = ConnectionUtil.OpenSession();

					PublicKeyDetails publicKeyDetails = (PublicKeyDetails) session
							.get(PublicKeyDetails.class, Key);

					if (publicKeyDetails != null) {
						rpAccountId = publicKeyDetails.getRpAccountId();
						deviceId = publicKeyDetails.getDeviceId();

						Transaction transaction = session.beginTransaction();

						// Delete transaction table record
						try {
							TransactionDetails transactionDetails = (TransactionDetails) session
									.get(TransactionDetails.class, rpAccountId);
							session.delete(transactionDetails);
							transaction.commit();
						} catch (Exception e) {
							transaction.rollback();
							e.printStackTrace();
						}

						// Delete key_info table record
						try {
							transaction = session.beginTransaction();
							PublicKeyDetails keyDetails = (PublicKeyDetails) session
									.get(PublicKeyDetails.class, Key);
							session.delete(keyDetails);
							transaction.commit();
						} catch (Exception e) {
							transaction.rollback();
							e.printStackTrace();
						}

						// Delete registration table record
						try {
							transaction = session.beginTransaction();
							Registration registrationDetails = (Registration) session
									.get(Registration.class, rpAccountId);
							session.delete(registrationDetails);
							transaction.commit();
						} catch (Exception e) {
							transaction.rollback();
							e.printStackTrace();
						}

						// Delete Device table record
						try {
							transaction = session.beginTransaction();
							Device deviceDetails = (Device) session.get(
									Device.class, deviceId);
							session.delete(deviceDetails);
							transaction.commit();
						} catch (Exception e) {
							transaction.rollback();
							e.printStackTrace();
						}

						// Delete Vendor table record
						try {
							transaction = session.beginTransaction();
							VendorDetails vendorDetails = (VendorDetails) session
									.get(VendorDetails.class, rpAccountId);
							session.delete(vendorDetails);
							transaction.commit();
						} catch (Exception e) {
							transaction.rollback();
							e.printStackTrace();
						}
					} else {
						System.out
								.println("No Key Information was found for the Given AAID#KEYID.");
					}
				} catch (Exception e) {
					e.printStackTrace();
					return "Failure: Problem in deleting record from local DB";
				}
			}
		} catch (Exception e) {
			return "Failure: problem processing deregistration request";
		}
		return Constants.SUCCESS_RESPONSE_STATUS;
	}
}
