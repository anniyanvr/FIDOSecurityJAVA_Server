package org.psl.fidouaf.controllers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.psl.fidouaf.core.constants.Constants;
import org.psl.fidouaf.core.crypto.BasicSignAndVerify;
import org.psl.fidouaf.core.entity.AuthenticationRequest;
import org.psl.fidouaf.core.entity.AuthenticationResponse;
import org.psl.fidouaf.core.entity.AuthenticatorRecord;
import org.psl.fidouaf.core.entity.DeregistrationRequest;
import org.psl.fidouaf.core.entity.RegistrationRecord;
import org.psl.fidouaf.core.entity.RegistrationRequest;
import org.psl.fidouaf.core.entity.RegistrationResponse;
import org.psl.fidouaf.core.entity.Version;
import org.psl.fidouaf.entity.Device;
import org.psl.fidouaf.entity.LogoutResponse;
import org.psl.fidouaf.entity.OTPDetails;
import org.psl.fidouaf.entity.PublicKeyDetails;
import org.psl.fidouaf.entity.PushOperation;
import org.psl.fidouaf.entity.Registration;
import org.psl.fidouaf.entity.SendPushNotification;
import org.psl.fidouaf.entity.TransactionDetails;
import org.psl.fidouaf.entity.VendorDetails;
import org.psl.fidouaf.exceptions.DataRecordNotFoundException;
import org.psl.fidouaf.exceptions.DuplicateAAIDKeyIdException;
import org.psl.fidouaf.exceptions.DuplicatePublicKeyException;
import org.psl.fidouaf.exceptions.DuplicateVendorException;
import org.psl.fidouaf.facets.Facets;
import org.psl.fidouaf.facets.TrustedFacets;
import org.psl.fidouaf.res.util.DeregRequestProcessor;
import org.psl.fidouaf.res.util.FetchRequest;
import org.psl.fidouaf.res.util.ProcessResponse;
import org.psl.fidouaf.service.DeviceService;
import org.psl.fidouaf.service.LogoutService;
import org.psl.fidouaf.service.OTPGenerationService;
import org.psl.fidouaf.service.PushNotificationService;
import org.psl.fidouaf.service.QRVerificationService;
import org.psl.fidouaf.service.RegistrationService;
import org.psl.fidouaf.service.ResponseService;
import org.psl.fidouaf.service.TransactionsService;
import org.psl.fidouaf.service.UtilService;
import org.psl.fidouaf.service.VendorService;
import org.psl.fidouaf.stats.Dash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class FidoUafController {

	/**
	 * PreConfigured Service Classes.
	 */
	@Autowired
	private QRVerificationService qrVerificationService;

	@Autowired
	private OTPGenerationService otpGenerationService;

	@Autowired
	private PushNotificationService pushNotificationService;

	@Autowired
	private UtilService utilService;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private DeviceService deviceService;

	@Autowired
	private VendorService vendorService;

	@Autowired
	private ResponseService responseService;

	@Autowired
	private LogoutService logOutService;

	@Autowired
	private TransactionsService transactionService;

	/**
	 * PreDefined Variables and Constants
	 */
	ObjectMapper mapper = new ObjectMapper();

	@RequestMapping(value = "/stats", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody Map<String, Object> getStats() {
		return Dash.getInstance().stats;
	}

	@RequestMapping(value = "/history", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	public List<Object> getHistory() {
		return Dash.getInstance().history;
	}

	// CODE_MODIFIED_BY_JASMIRA
	@RequestMapping(value = "/public/regRequest", params = "username", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody List<RegistrationRequest> getRegisReqPublic(
			@RequestParam("username") String userName) {

		List<RegistrationRequest> registrationRequest = new ArrayList<RegistrationRequest>();
		registrationRequest
				.add(new FetchRequest(utilService.getAppId(), utilService
						.getAllowedAaids()).getRegistrationRequest(userName));
		Dash.getInstance().stats.put(Constants.LAST_REG_REQ,
				registrationRequest);
		Dash.getInstance().history.add(registrationRequest);
		return registrationRequest;
	}

	// CODE_MODIFIED_BY_JASMIRA
	@RequestMapping(value = "/public/regRequest", params = { "username",
			"appId" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody List<RegistrationRequest> getRegReqForAppId(
			@RequestParam("username") String userName,
			@RequestParam("appId") String appId) throws JSONException,
			IllegalAccessException {
		List<RegistrationRequest> registrationRequest = getRegisReqPublic(userName);
		utilService.setAppId(appId, registrationRequest.get(0).header);
		return registrationRequest;
	}

	// CODE_MODIFIED_BY_JASMIRA
	@RequestMapping(value = "/public/uaf/facets", method = RequestMethod.GET, produces = "application/json")
	// produces="fido.trusted-apps+json"
	public @ResponseBody static Facets facets() {
		String timestamp = new Date().toString();
		Dash.getInstance().stats.put(Constants.LAST_REG_REQ, timestamp);
		String[] trustedIds = { "https://www.head2toes.org",
				"android:apk-key-hash:Df+2X53Z0UscvUu6obxC3rIfFyk",
				"android:apk-key-hash:bE0f1WtRJrZv/C0y9CM73bAUqiI",
				"https://openidconnect.ebay.com",
				"www.fidopersistent.pslbank.com",
				"www.fidopersistent.pslbank.iosmobile.com" };
		Facets facets = new Facets();
		TrustedFacets trustedFacet = new TrustedFacets();
		trustedFacet.setVersion(new Version(1, 0));
		trustedFacet.setIds(trustedIds);
		facets.setTrustedFacet(trustedFacet);
		return facets;
	}

	// NEW_CODE_ADDED_BY_JASMIRA
	@RequestMapping(value = "/public/regResponseStatus", params = { "rpaccountid" }, method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN)
	public @ResponseBody String getRegResponseStatus(
			@RequestParam("rpaccountid") int rpAccountId) {
		System.out
				.println("\n\nPayload received for get Registration Response: "
						+ rpAccountId);

		String responseStatus = null;

		// Check if Registration Status is true
		if (registrationService.getIsRegisteredUser(rpAccountId)) {
			responseStatus = Constants.SUCCESS_RESPONSE_STATUS;
		} else {
			responseStatus = Constants.FAILURE_RESPONSE_STATUS;
		}
		return responseStatus;
	}

	// NEW_CODE_ADDED_BY_JASMIRA
	@RequestMapping(value = "/public/getAuthResponseStatus", params = { "rpaccountid" }, method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN)
	public @ResponseBody String authResponseStatus(
			@RequestParam("rpaccountid") int rpAccountId)
			throws DataRecordNotFoundException {
		System.out
				.println("\n\nPayload received to check the AUTH flags in DB; RP Account ID: "
						+ rpAccountId);
		String responseStatus = null;

		// Get auth related flags from DB based on accountid
		Map<String, Boolean> authFlags = new HashMap<String, Boolean>();
		authFlags = registrationService.getAuthFlagsforAccountId(rpAccountId);

		// check if "auth_in_progress" field is set to true
		if (authFlags.get("auth_in_progress").equals(false)) {
			responseStatus = Constants.FAILURE_RESPONSE_STATUS;
		} else {
			if (authFlags.get("authstats").equals(false)) {
				responseStatus = Constants.FAILURE_RESPONSE_STATUS;
			} else {
				responseStatus = Constants.SUCCESS_RESPONSE_STATUS;
				// on success, reset the value of auth_in_progress and authstats
				// back to false
				registrationService.resetAuthFlags(rpAccountId);
			}
		}
		return responseStatus;
	}

	// NEW_CODE_ADDED_BY_JASMIRA
	@RequestMapping(value = "/public/clearAuthResponse", method = RequestMethod.GET, produces = {
			"text/plain", "application/xml", "application/json" })
	public String clearLastAuthResponse() {
		System.out
				.println("\nRequest received to clear last authentication response reply.");
		Dash.getInstance().stats.put(Constants.LAST_AUTH_RES_REPLY, null);
		return Constants.SUCCESS_RESPONSE_STATUS;
	}

	// CODE_MODIFIED_BY_JASMIRA
	@RequestMapping(value = "/public/regResponse", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	public @ResponseBody List<RegistrationRecord> processRegResponse(
			@RequestBody List<RegistrationResponse> registrationResponse)
			throws IOException, JSONException, DataRecordNotFoundException,
			DuplicatePublicKeyException, DuplicateAAIDKeyIdException {
		System.out.println("\n\nPayload received for Registration Response: "
				+ registrationResponse);
		List<RegistrationRecord> registrationRecord = null;
		PublicKeyDetails publicKeyDetails = new PublicKeyDetails();

		Dash.getInstance().stats.put(Constants.LAST_REG_RES,
				registrationResponse);
		Dash.getInstance().history.add(registrationResponse);

		registrationRecord = new ProcessResponse()
				.processRegResponse(registrationResponse.get(0));
		int eachAssertionSucessfulCount = 0;
		String deviceId = null;
		if (registrationRecord != null) {
			for (int i = 0; i < registrationRecord.size(); i++) {
				if (registrationRecord.get(i).getStatus()
						.equalsIgnoreCase("SUCCESS")) {

					// Increment counter for each successful assertion
					// validation;
					eachAssertionSucessfulCount++;

					/* Get deviceId for passed accountId as Username */
					deviceId = registrationService
							.getDeviceIdForAccountId(Integer
									.parseInt(registrationRecord.get(i).username));

					publicKeyDetails.setRpAccountId(Integer
							.parseInt(registrationRecord.get(i).username));
					publicKeyDetails
							.setPublicKey(registrationRecord.get(i).PublicKey);
					publicKeyDetails
							.setAaid_keyid(registrationRecord.get(i).authenticator
									.toString());
					publicKeyDetails.setDeviceId(deviceId);

					// Store in Key_Info Table.
					registrationService.addPublicKeyDetails(publicKeyDetails);
				}
			}
		} else {
			registrationRecord = new ArrayList<RegistrationRecord>();
			RegistrationRecord registrationRecordEmpty = new RegistrationRecord();
			registrationRecordEmpty.setStatus("Error: Empty/Null result");
			registrationRecord.add(registrationRecordEmpty);
		}

		// Update Registration status for deviceId - AccountId combo in
		// registration DB
		if (eachAssertionSucessfulCount == registrationRecord.size()) {
			registrationService.updateAuthenticatorRegStatus(registrationRecord
					.get(0).username);
		}

		// Check all Data in devicedetails table.
		System.out.println("\nAll data in DeviceDetails Table: ");
		deviceService.showAllDeviceData();
		// Check all data in registration table.
		System.out.println("\nAll data in RegistrationDB Table: ");
		registrationService.showAllRegistrationData();
		// check all Data in key_info table.
		System.out.println("\nAll Data in Key_info Table: ");
		registrationService.showAllPublicKeyDetails();

		return registrationRecord;
	}

	// NEW_CODE_ADDED_BY_JASMIRA
	/**
	 * New API for Adding new Vendor.
	 * 
	 * @param vendorDetails
	 * @return
	 * @throws DuplicateVendorException
	 * @throws IOException
	 * @throws JSONException
	 */
	@RequestMapping(value = "/public/rpRegRequest", produces = { "text/plain",
			"application/xml", "application/json" }, consumes = { "text/plain",
			"application/xml", "application/json" }, method = RequestMethod.POST)
	public @ResponseBody String addVendor(
			@RequestBody VendorDetails vendorDetails)
			throws DuplicateVendorException {

		System.out.println("Payload received for RP Reg Request: "
				+ vendorDetails.toString());
		String response = null;
		// Generate OTP for vendor registration confirmation.
		OTPDetails otpDetails = otpGenerationService.generateOTPwithoutSMS();
		if (otpDetails != null
				&& !StringUtils.isEmpty(otpDetails.getGeneratedOTP())) {
			String generatedOTP = otpDetails.getGeneratedOTP();
			Date otpGenerationDate = otpDetails.getOTPGenerationDate();

			vendorDetails.setOtp(generatedOTP);
			vendorDetails.setOtpCreationDate(otpGenerationDate);

			vendorService.saveVendorDetails(vendorDetails);
			System.out
					.println("\nListening for Response ****************************************");
			response = responseService
					.constructJSONForRPRegRequest(generatedOTP);
		}
		return response;
	}

	// NEW_CODE_ADDED_BY_JASMIRA
	/**
	 * New API to get all details for Vendor (relying party) as a response to
	 * POST call after push notifications.
	 * 
	 * @param payload
	 * @return
	 * @throws DataRecordNotFoundException
	 * @throws IOException
	 * @throws JSONException
	 */

	@RequestMapping(value = "/public/getMakeCredentialDetails", method = RequestMethod.POST, produces = { "application/json" }, consumes = { "application/json" })
	public @ResponseBody String showVendorDetails(
			@RequestBody VendorDetails vendorDetails)
			throws DataRecordNotFoundException {
		System.out.println("\nPayload received for MakeCredentials: "
				+ vendorDetails.getAccountid());

		int accountId = vendorDetails.getAccountid();

		VendorDetails vendorDetailsFromDB = vendorService
				.getVendorDetails(accountId);

		String response = responseService
				.constructJSONForMakeCredential(vendorDetailsFromDB);

		return response;
	}

	// NEW_CODE_ADDED_BY_JASMIRA
	/**
	 * New API to verify the QR contents scanned by the mobile device.
	 * 
	 * @param vendorDetails
	 * @return
	 * @throws DataRecordNotFoundException
	 */
	@RequestMapping(value = "/public/verifyQRContents", consumes = "application/json", method = RequestMethod.POST)
	public @ResponseBody String verifyQRContents(
			@RequestBody VendorDetails vendorDetails)
			throws DataRecordNotFoundException {
		System.out.println("\nPayload received to verify QR code: "
				+ vendorDetails.toString());
		String response = null;
		boolean isQRVerified = qrVerificationService
				.isQRVerified(vendorDetails);
		if (isQRVerified) {
			vendorService.updateVendorRegStatusToTrue(vendorDetails);
			registrationService.insertRPDetailsInRegDB(vendorDetails);
			response = responseService
					.constructJSONForQRVerification(isQRVerified);
		} else {
			System.out.println("\nQR Data Incorrect. Please Re-Scan!");
			response = responseService
					.constructJSONForQRVerification(isQRVerified);
		}
		return response;
	}

	// CODE_MODIFIED_BY_JASMIRA
	@RequestMapping(value = "/public/deregRequest", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN, consumes = MediaType.APPLICATION_JSON)
	public @ResponseBody String deregRequestPublic(
			@RequestBody List<DeregistrationRequest> deregistrationRequest) {

		return new DeregRequestProcessor().process(deregistrationRequest);
	}

	// CODE_MODIFIED_BY_JASMIRA
	@RequestMapping(value = "/public/authRequest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody List<AuthenticationRequest> getAuthReq()
			throws JsonGenerationException, JsonMappingException, IOException,
			JSONException, IllegalAccessException {

		// gson.toJson(getAuthReqObj());
		// return mapper.writeValueAsString(utilService.getAuthReqObj());
		return utilService.getAuthReqObj();
	}

	// NEW_CODE_ADDED_BY_JASMIRA
	@RequestMapping(value = "/public/authRequest/customPolicy", params = {
			"deviceid", "accountid" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody List<AuthenticationRequest> getAuthReq(
			@RequestParam("deviceid") String deviceId,
			@RequestParam("accountid") int accountId)
			throws JsonGenerationException, JsonMappingException, IOException,
			JSONException, IllegalAccessException {
		System.out.println("Device Id: " + deviceId + "\nAccount Id: "
				+ accountId);
		List<AuthenticationRequest> authReqObj = utilService
				.getAuthReqResponse(deviceId, accountId);

		// gson.toJson(authReqObj);
		// return mapper.writeValueAsString(authReqObj);
		return authReqObj;
	}

	// CODE_MODIFIED_BY_JASMIRA
	@RequestMapping(value = "/public/authRequest", params = { "appId" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody List<AuthenticationRequest> getAuthForAppIdReq(
			@RequestParam("appId") String appId)
			throws JsonGenerationException, JsonMappingException, IOException,
			JSONException, IllegalAccessException {
		List<AuthenticationRequest> authReqObj = utilService.getAuthReqObj();
		utilService.setAppId(appId, authReqObj.get(0).header);

		// gson.toJson(authReqObj);
		// return mapper.writeValueAsString(authReqObj);
		return authReqObj;
	}

	// CODE_MODIFIED_BY_JASMIRA
	@RequestMapping(value = "/public/authRequest", params = { "appId",
			"trxContent" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody List<AuthenticationRequest> getAuthTrxReq(
			@RequestParam("appId") String appId,
			@RequestParam("trxContent") String trxContent)
			throws JsonGenerationException, JsonMappingException, IOException,
			JSONException, IllegalAccessException {
		List<AuthenticationRequest> authReqObj = utilService.getAuthReqObj();
		utilService.setAppId(appId, authReqObj.get(0).header);
		utilService.setTransaction(trxContent, authReqObj);

		// return gson.toJson(authReqObj);
		// return mapper.writeValueAsString(authReqObj);
		return authReqObj;
	}

	// NEW_CODE_ADDED_BY_JASMIRA
	@RequestMapping(value = "/public/authRequest/customPolicy", params = {
			"deviceid", "accountid", "appId", "trxContent" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody List<AuthenticationRequest> getAuthTrxReq(
			@RequestParam("deviceid") String deviceId,
			@RequestParam("accountid") int accountId,
			@RequestParam("appId") String appId,
			@RequestParam("trxContent") String trxContent)
			throws JsonGenerationException, JsonMappingException, IOException,
			JSONException, IllegalAccessException {
		System.out.println("Device Id: " + deviceId + "\nAccount Id: "
				+ accountId + "\nApp Id: " + appId + "\nTransaction content: "
				+ trxContent);
		List<AuthenticationRequest> authReqObj = utilService
				.getAuthReqResponse(deviceId, accountId);
		utilService.setAppId(appId, authReqObj.get(0).header);
		utilService.setTransaction(trxContent, authReqObj);

		// return gson.toJson(authReqObj);
		// return mapper.writeValueAsString(authReqObj);
		return authReqObj;
	}

	// CODE_MODIFIED_BY_JASMIRA
	@RequestMapping(value = "/public/authResponse", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	public @ResponseBody List<AuthenticatorRecord> processAuthResponse(
			@RequestBody List<AuthenticationResponse> authenticationResponse)
			throws DataRecordNotFoundException {

		System.out.println("\n\nPayload from POST request: "
				+ authenticationResponse);

		Dash.getInstance().stats.put(Constants.LAST_AUTH_RES,
				authenticationResponse);
		Dash.getInstance().history.add(authenticationResponse);

		List<AuthenticatorRecord> authenticatorRecord = new ProcessResponse()
				.processAuthResponse(authenticationResponse.get(0));
		Dash.getInstance().stats.put(Constants.LAST_AUTH_RES_REPLY,
				authenticatorRecord);

		// Get the status field from Auth Response
		String status = authenticatorRecord.get(0).getStatus();

		// Based on status value, set the authstats flag in DB
		if (status.equalsIgnoreCase("SUCCESS")) {
			// update authstats filed in DB to true
			registrationService.updateAuthStatusInDB(authenticatorRecord.get(0)
					.toString());
		}
		return authenticatorRecord;
	}

	// NEW_CODE_ADDED_BY_JASMIRA
	@RequestMapping(value = "/public/pushNotifyAuthentication", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN, consumes = MediaType.APPLICATION_JSON)
	public @ResponseBody String pushNotifyToAuthenticate(
			@RequestBody VendorDetails vendorDetails)
			throws DataRecordNotFoundException {
		System.out
				.println("\n\nPayload received for Push notify Authentication: "
						+ vendorDetails.toString());

		final int accountId = vendorDetails.getAccountid();
		String vendorname = vendorDetails.getVendorName();

		String deviceId = registrationService
				.getDeviceIdForAccountId(accountId);
		Device device = deviceService.getDeviceFordeviceId(deviceId);

		VendorDetails vendorDetailsFromDB = vendorService
				.getVendorDetails(accountId);

		// Send push notification to IOS device to indicate FIDO Authentication
		SendPushNotification pushData = new SendPushNotification();
		pushData.setContent("Provide approval for login attempt by user: "
				+ vendorDetailsFromDB.getUserName() + " into " + vendorname
				+ " website");
		pushData.setOperation(PushOperation.FIDO_Autheticate);
		pushData.setToken(device.getDevicetoken());
		pushNotificationService.sendPushNotifications(pushData);

		// Set auth_in_progress field in Device details DB to true to mark the
		// beginning of authentication.
		registrationService.markAuthenticationInProgressToTrue(accountId);

		// Get auth related flags from registration DB based on accountid
		Map<String, Boolean> authFlags = new HashMap<String, Boolean>();
		authFlags = registrationService.getAuthFlagsforAccountId(accountId);

		// Create timer.
		Timer timer = new Timer();
		if (authFlags.get("authstats").equals(false)) {
			// Schedule timer for 2 mins, and set the auth_in_progress field in
			// DB to false after this timer expires.
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					// Reset the value of auth_in_progress and authstats back to
					// false after 2 mins.
					registrationService.resetAuthFlags(accountId);
				}
			}, 2 * 60 * 1000);
		}
		System.out
				.println("Device Push Notified to go ahead with Authentication Process.");
		return Constants.SUCCESS_RESPONSE_STATUS;
	}

	// NEW_CODE_ADDED_BY_JASMIRA
	/**
	 * API that perform simple Logout operation. It drops all DB data and allow
	 * users to start afresh (For testing purpose)
	 * 
	 * @return
	 */
	@RequestMapping(value = "/public/dologout", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody LogoutResponse logout() {
		LogoutResponse logoutResponse = new LogoutResponse();

		boolean logoutStatus = logOutService.removeAllRecords();

		if (logoutStatus == true) {
			logoutResponse.setMessage("User is successfully logged out...");
			logoutResponse.setStatus(logoutStatus);
		} else {
			logoutResponse.setMessage("Logout Failed!");
			logoutResponse.setStatus(logoutStatus);
		}
		return logoutResponse;
	}

	// NEW_CODE_ADDED_BY_JASMIRA
	/**
	 * New API for Adding new Vendor for App registration.
	 * 
	 * @param vendorDetails
	 * @return
	 * @throws DuplicateVendorException
	 * @throws IOException
	 * @throws JSONException
	 */
	@RequestMapping(value = "/public/appRegRequest", produces = { "text/plain",
			"application/xml", "application/json" }, consumes = { "text/plain",
			"application/xml", "application/json" }, method = RequestMethod.POST)
	public @ResponseBody String addAPPVendor(
			@RequestBody VendorDetails vendorDetails)
			throws DuplicateVendorException {
		System.out.println("Payload received for APP Reg Request: "
				+ vendorDetails.toString());
		String response = null;

		// Generate OTP for vendor registration confirmation.
		OTPDetails otpDetails = otpGenerationService.generateOTPwithoutSMS();
		if (otpDetails != null
				&& !StringUtils.isEmpty(otpDetails.getGeneratedOTP())) {
			String generatedOTP = otpDetails.getGeneratedOTP();
			Date otpGenerationDate = otpDetails.getOTPGenerationDate();

			vendorDetails.setOtp(generatedOTP);
			vendorDetails.setOtpCreationDate(otpGenerationDate);

			vendorService.saveVendorDetails(vendorDetails);
			System.out
					.println("\nListening for Response ****************************************");
			response = responseService
					.constructJSONForAPPRegRequest(generatedOTP);
		}
		return response;
	}

	// NEW_CODE_ADDED_BY_JASMIRA
	/**
	 * New API to enable Push notification permission to the FIDO server from
	 * mobile client at the first launch of the mobile app after download.
	 * 
	 * @param payload
	 * @return
	 */
	@RequestMapping(value = "/public/enablePushNotifications", produces = {
			"text/plain", "application/xml", "application/json" }, consumes = {
			"text/plain", "application/xml", "application/json" }, method = RequestMethod.POST)
	public @ResponseBody String enablePushNotifications(
			@RequestBody Device device) {
		System.out.println("\nPayload received to enable Push Notifications: "
				+ device.toString());
		String response = null;

		// Store device token and device id
		boolean deviceDataAdded = deviceService.addDevice(device);

		response = responseService
				.constructJSONForPushNotifiedDevice(deviceDataAdded);
		return response;
	}

	// NEW_CODE_ADDED_BY_JASMIRA
	/**
	 * Method that accepts transaction details from RP Website and
	 * processes/stores it.
	 * 
	 * @param payload
	 * @return
	 * @throws DataRecordNotFoundException
	 */
	@RequestMapping(value = "/public/sendTransactions", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN, consumes = { "application/json" })
	public @ResponseBody String getTransactionDetails(
			@RequestBody TransactionDetails transactionDetails)
			throws DataRecordNotFoundException {
		System.out
				.println("\n\nPayload received from RP for processing transaction: "
						+ transactionDetails.toString());

		final int accountId = transactionDetails.getAccountId();
		// Content content = transactionDetails.getContent();
		String amountTransfered = transactionDetails.getAmount_transferred();
		String username = transactionDetails.getUsername();
		String to = transactionDetails.getTo();

		transactionService.saveTransaction(transactionDetails);

		// Get device token using the account_id
		String deviceId = registrationService
				.getDeviceIdForAccountId(accountId);
		Device device = deviceService.getDeviceFordeviceId(deviceId);

		SendPushNotification sendPushNotification = new SendPushNotification();
		sendPushNotification.setToken(device.getDevicetoken());
		sendPushNotification.setOperation(PushOperation.FIDO_Transaction);
		sendPushNotification.setContent("Approve Transaction of Rs. "
				+ amountTransfered + " from account with username: " + username
				+ " to: " + to);

		// Send push notification to device
		pushNotificationService.sendPushNotifications(sendPushNotification);

		// Set auth_in_progress field in Device details DB to true to mark the
		// beginning of authentication.
		registrationService.markAuthenticationInProgressToTrue(accountId);

		// Get auth related flags from DB based on accountid
		Map<String, Boolean> authFlags = new HashMap<String, Boolean>();
		authFlags = registrationService.getAuthFlagsforAccountId(accountId);

		// Create timer.
		Timer timer = new Timer();
		if (authFlags.get("authstats").equals(false)) {
			// Schedule timer for 2 mins, and set the auth_in_progress field in
			// DB
			// to false after this timer expires.
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					// Reset the value of auth_in_progress and authstats back to
					// false after 2 mins.
					registrationService.resetAuthFlags(accountId);
				}
			}, 2 * 60 * 1000);
		}

		System.out
				.println("Device Push Notified to Authenticate the Transaction.");
		return Constants.SUCCESS_RESPONSE_STATUS;
	}

	// NEW_CODE_ADDED_BY_JASMIRA
	/**
	 * Method that processes push notification acknowledgment from device and
	 * returns more details about the user.
	 * 
	 * @param payload
	 * @return
	 * @throws DataRecordNotFoundException
	 */
	@RequestMapping(value = "/public/processPushNotificationAck", method = RequestMethod.POST, produces = { "application/json" }, consumes = { "application/json" })
	public @ResponseBody String processNotificationAcknowledgment(
			@RequestBody TransactionDetails transactionDetails)
			throws DataRecordNotFoundException {
		String response = null;
		System.out
				.println("\n\nPayload received for processing the push notification acknowledgment: "
						+ transactionDetails.getAccountId());

		// Get appId and content details using the rpAccountId from transaction
		// table.
		TransactionDetails transactionFromDB = transactionService
				.findTransaction(transactionDetails.getAccountId());

		response = responseService
				.constructJSONForPushNotifyAck(transactionFromDB);
		return response;
	}

	// NEW_CODE_ADDED_BY_JASMIRA
	/**
	 * Method to Notify FIDO server the authenticators enforced onto the user
	 * during login based on the context passed (flexible Authentucation).
	 * 
	 * @param payload
	 * @return
	 */
	@RequestMapping(value = "/public/notifyAuthenticators", method = RequestMethod.POST, produces = { "text/plain" }, consumes = {
			"text/plain", "application/xml", "application/json" })
	public @ResponseBody String notifyAuthenticatorsToFIDOServer(
			@RequestBody Registration registrationDetails) {
		System.out
				.println("\n\nPayload received for notifying authenticators to FIDO server: \n RP AccountID: "
						+ registrationDetails.getRpAccountId()
						+ "\nContext: "
						+ registrationDetails.getContext()
						+ "\nAuthenticators: "
						+ /* registrationDetails.getAuthenticators().toString() */registrationDetails
								.getAaids());

		registrationService
				.updateUserContextAndAuthenticators(registrationDetails);
		return Constants.NOTIFIED;
	}

	// NEW_CODE_ADDED_BY_JASMIRA
	/**
	 * Method to form policy based on the enforced AAIDs stored /set up for user
	 * at the time of login based on the context passed (Flexible
	 * authentication).
	 * 
	 * @param accountId
	 * @return
	 */
	@RequestMapping(value = "/public/authRequest/flexiblePolicy", params = { "accountid" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody List<AuthenticationRequest> getAuthReq(
			@RequestParam("accountid") int accountId) {
		System.out.println("Account Id: " + accountId);
		List<AuthenticationRequest> authReqObj = utilService
				.getAuthReqResponse(accountId);
		// return gson.toJson(authReqObj);
		return authReqObj;
	}

	// NEW_CODE_ADDED_BY_JASMIRA
	/**
	 * Method that decrypts the encrypted contexts and verifies them.
	 * 
	 * @param payload
	 * @return
	 * @throws Exception
	 * @throws UnsupportedEncodingException
	 * @throws SignatureException
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	/*
	 * @POST
	 * 
	 * @Path("/public/validateContexts")
	 * 
	 * @Consumes({ "text/plain", "application/xml", "application/json" })
	 * 
	 * @Produces({ "text/plain", "application/xml", "application/json" }) public
	 * String validateEncryptedContexts(String payload) throws
	 * InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException,
	 * SignatureException, UnsupportedEncodingException, Exception {
	 * System.out.println("\n\nPayload received for Validating Contexts: " +
	 * payload);
	 * 
	 * String response = VERIFIED; ArrayList<Device> storeUnverifiedContexts =
	 * new ArrayList<Device>(); Gson gson = new Gson(); Device fromJson =
	 * gson.fromJson(payload, Device.class); String rpAccountId =
	 * fromJson.getRpaccountid(); ContextDetails[] contextDetails =
	 * fromJson.getContextDetails();
	 * 
	 * boolean[] allContextsVerified = new boolean[contextDetails.length];
	 * 
	 * //Get public key for accountId and aaid passed in the input JSON. for(int
	 * i=0;i<contextDetails.length;i++){ String aaid =
	 * contextDetails[i].getAaid();
	 * 
	 * String pubKey =
	 * StorageImpl.getInstance().getPubKeyforAccountIdAndAAID(rpAccountId,
	 * aaid); allContextsVerified[i] =
	 * AuthenticationResponseProcessing.verifyContextSignatures
	 * (contextDetails[i].getSignedData(), contextDetails[i].getSignature(),
	 * pubKey);
	 * 
	 * if(allContextsVerified[i] == false){ Device device = new Device();
	 * device.setRpaccountid(rpAccountId);
	 * device.setContextDetails(contextDetails);
	 * 
	 * storeUnverifiedContexts.add(device); } }
	 * 
	 * //Print out context data that was not verified for logging purpose.
	 * /*System.out.println(
	 * "Printing out the Context details that were not verified (if any): ");
	 * for(int m=0; m<contextList.length; m++){
	 * System.out.println(contextList[m]); }
	 */

	// If any one context is not verified, return response as NOT_VERIFIED
	/*
	 * for(int j=0;j<allContextsVerified.length;j++){ if(allContextsVerified[j]
	 * == false){ response = NOT_VERIFIED; } }
	 * 
	 * return response; }
	 */

	// NEW_CODE_ADDED_BY_JASMIRA
	/**
	 * Method that returns signed context using FIDO servers private key.
	 * 
	 * @param payload
	 * @return
	 * @throws Exception
	 * @throws UnsupportedEncodingException
	 * @throws SignatureException
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	@RequestMapping(value = "/public/getSignedContext", method = RequestMethod.POST, produces = {
			"text/plain", "application/xml", "application/json" }, consumes = {
			"text/plain", "application/xml", "application/json" })
	public @ResponseBody String createSignedContexts(
			@RequestBody Registration registrationDetails)
			throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchProviderException, SignatureException,
			UnsupportedEncodingException, Exception {
		System.out
				.println("\n\nPayload received for creating signed contexts: "
						+ registrationDetails.getRpAccountId());
		String response = null;

		PrivateKey privateKey = BasicSignAndVerify.getPrivateKey();
		// Get Context from table in DB based on accountId
		String context = registrationService
				.getContextForAccountID(registrationDetails.getRpAccountId());
		String signedContext = BasicSignAndVerify.sign(context, privateKey);

		response = responseService.constructJSONForSignedContext(signedContext);
		return response;
	}

	// NEW_CODE_ADDED_BY_JASMIRA
	/**
	 * API that push notifies APP about the user authenticating to the APP.
	 * 
	 * @param vendorDetails
	 * @return
	 * @throws DataRecordNotFoundException
	 */
	@RequestMapping(value = "/public/notifyAppAuthentication", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN, consumes = MediaType.APPLICATION_JSON)
	public @ResponseBody String notifyAppAuthentication(
			@RequestBody VendorDetails vendorDetails)
			throws DataRecordNotFoundException {
		System.out
				.println("\n\nPayload received for Notifying inter app authentication: "
						+ vendorDetails.getAccountid());
		String response = null;

		int accountId = vendorDetails.getAccountid();
		VendorDetails vendorDetailsFromDB = vendorService
				.getVendorDetails(accountId);

		// Send message (same as you would get on push notification while
		// authenticating through RP desktop website) as response.
		response = responseService
				.constructOutputForNotifyAppAuth(vendorDetailsFromDB);
		return response;
	}
}
