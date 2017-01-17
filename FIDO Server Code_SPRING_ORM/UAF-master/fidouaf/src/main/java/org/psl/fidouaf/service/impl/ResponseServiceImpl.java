package org.psl.fidouaf.service.impl;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.psl.fidouaf.entity.TransactionDetails;
import org.psl.fidouaf.entity.VendorDetails;
import org.psl.fidouaf.service.ResponseService;
import org.springframework.stereotype.Service;

@Service
public class ResponseServiceImpl implements ResponseService {

	/**
	 * Method to construct JSON Response for RP Reg Request API.
	 * 
	 * @param otp
	 * @return generated OTP
	 */
	public String constructJSONForRPRegRequest(String otp) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("registrationResponse", otp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj.toString();
	}

	/**
	 * Method to construct JSON Response for APP Reg Request API.
	 * 
	 * @param otp
	 * @return generated OTP
	 */
	public String constructJSONForAPPRegRequest(String otp) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("registrationResponse", otp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj.toString();
	}

	/**
	 * Method to construct JSON Response for Get Make Credentials API.
	 * 
	 * @param vendorDetails
	 * @return
	 */
	public String constructJSONForMakeCredential(VendorDetails vendorDetails) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("rpDisplayName", vendorDetails.getVendorName());
			obj.put("displayName", vendorDetails.getUserName());
			obj.put("email", vendorDetails.getEmail());
			obj.put("accountID", vendorDetails.getAccountid());
		} catch (JSONException e) {
		}
		return obj.toString();
	}

	/**
	 * Method to construct JSON Response for Verify QR Contents API.
	 * 
	 * @param isQRVerified
	 * @return
	 */
	public String constructJSONForQRVerification(boolean isQRVerified) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("QRCodeVerification", new Boolean(isQRVerified));
		} catch (JSONException e) {
		}
		return obj.toString();
	}

	/**
	 * Method to construct JSON response for Enable PushNotifications API.
	 * 
	 * @param deviceDataUpdated
	 * @return
	 */
	public String constructJSONForPushNotifiedDevice(boolean deviceDataUpdated) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("deviceDataUpdateStatus", new Boolean(deviceDataUpdated));
		} catch (JSONException e) {
		}
		return obj.toString();
	}

	/**
	 * Method to construct JSON for Push Notify Acknowledgement API.
	 * 
	 * @param trnasactionDetails
	 * @return String
	 */
	public String constructJSONForPushNotifyAck(
			TransactionDetails transactionDetails) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("appId", transactionDetails.getAppId());
			obj.put("content", "Approve Transaction of Rs. "
					+ transactionDetails.getAmount_transferred()
					+ " from account with username: "
					+ transactionDetails.getUsername() + " to: "
					+ transactionDetails.getTo());
		} catch (JSONException e) {
		}
		return obj.toString();
	}

	/**
	 * Method to construct JSON for Get Signed Context API.
	 * 
	 * @param signedContext
	 * @return
	 */
	public String constructJSONForSignedContext(String signedContext) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("signedContext", signedContext);
		} catch (JSONException e) {
		}
		return obj.toString();
	}

	/**
	 * Method that construct output String for Notify APP Authentication API.
	 */
	public String constructOutputForNotifyAppAuth(VendorDetails vendorDetails) {
		String response = "Provide approval for login attempt by user: "
				+ vendorDetails.getUserName() + " into: "
				+ vendorDetails.getVendorName() + " app.";
		return response;
	}
}
