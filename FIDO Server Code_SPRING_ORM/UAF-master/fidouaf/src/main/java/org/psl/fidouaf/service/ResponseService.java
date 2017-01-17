package org.psl.fidouaf.service;

import org.psl.fidouaf.entity.TransactionDetails;
import org.psl.fidouaf.entity.VendorDetails;
import org.springframework.stereotype.Service;

@Service
public interface ResponseService {
	public String constructJSONForRPRegRequest(String otp);

	public String constructJSONForAPPRegRequest(String otp);

	public String constructJSONForMakeCredential(VendorDetails vendorDetails);

	public String constructJSONForQRVerification(boolean isQRVerified);

	public String constructJSONForPushNotifiedDevice(boolean deviceDataUpdated);

	public String constructJSONForPushNotifyAck(
			TransactionDetails transactionDetails);

	public String constructJSONForSignedContext(String signedContext);

	public String constructOutputForNotifyAppAuth(VendorDetails vendorDetails);
}
