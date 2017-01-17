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

package org.ebayopensource.fidouaf.res;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jettison.json.JSONException;
import org.ebayopensource.fido.uaf.crypto.BasicSignAndVerify;
import org.ebayopensource.fido.uaf.msg.AuthenticationRequest;
import org.ebayopensource.fido.uaf.msg.AuthenticationResponse;
import org.ebayopensource.fido.uaf.msg.Authenticators;
import org.ebayopensource.fido.uaf.msg.Content;
import org.ebayopensource.fido.uaf.msg.Device;
import org.ebayopensource.fido.uaf.msg.Login;
import org.ebayopensource.fido.uaf.msg.Operation;
import org.ebayopensource.fido.uaf.msg.OperationHeader;
import org.ebayopensource.fido.uaf.msg.RegistrationRequest;
import org.ebayopensource.fido.uaf.msg.RegistrationResponse;
import org.ebayopensource.fido.uaf.msg.Transaction;
import org.ebayopensource.fido.uaf.msg.TransactionDetails;
import org.ebayopensource.fido.uaf.msg.Vendor;
import org.ebayopensource.fido.uaf.msg.Version;
import org.ebayopensource.fido.uaf.storage.AuthenticatorRecord;
import org.ebayopensource.fido.uaf.storage.DuplicateKeyException;
import org.ebayopensource.fido.uaf.storage.RegistrationRecord;
import org.ebayopensource.fido.uaf.storage.SystemErrorException;
import org.ebayopensource.fidouaf.RPserver.msg.GetUAFRequest;
import org.ebayopensource.fidouaf.RPserver.msg.ReturnUAFAuthenticationRequest;
import org.ebayopensource.fidouaf.RPserver.msg.ReturnUAFDeregistrationRequest;
import org.ebayopensource.fidouaf.RPserver.msg.ReturnUAFRegistrationRequest;
import org.ebayopensource.fidouaf.RPserver.msg.ServerResponse;
import org.ebayopensource.fidouaf.facets.Facets;
import org.ebayopensource.fidouaf.facets.TrustedFacets;
import org.ebayopensource.fidouaf.res.util.DeregRequestProcessor;
import org.ebayopensource.fidouaf.res.util.FetchRequest;
import org.ebayopensource.fidouaf.res.util.ProcessLogin;
import org.ebayopensource.fidouaf.res.util.ProcessResponse;
import org.ebayopensource.fidouaf.res.util.StorageImpl;
import org.ebayopensource.fidouaf.stats.Dash;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/v1")
public class FidoUafResource {

	private static final String NOTIFIED = "NOTIFIED";
	private static final String ERROR_IN_UPDATE = "error_in_update";
	private static final String SUCCESS = "SUCCESS";
	private static final String NOT_VERIFIED = "NOT_VERIFIED";
	private static final String VERIFIED = "VERIFIED";
	Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	@GET
	@Path("/stats")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStats() {
		return gson.toJson(Dash.getInstance().stats);
	}

	@GET
	@Path("/history")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Object> getHistory() {
		return Dash.getInstance().history;
	}

	@GET
	@Path("/registrations")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, RegistrationRecord> getDbDump() {
		return StorageImpl.getInstance().dbDump();
	}

	@GET
	@Path("/public/regRequest/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public RegistrationRequest[] getRegisReqPublic(
			@PathParam("username") String username) {
		RegistrationRequest[] regReq = new RegistrationRequest[1];
		regReq[0] = new FetchRequest(getAppId(), getAllowedAaids())
				.getRegistrationRequest(username);
		Dash.getInstance().stats.put(Dash.LAST_REG_REQ, regReq);
		Dash.getInstance().history.add(regReq);
		return regReq;
	}

	@GET
	@Path("/public/regRequest/{username}/{appId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getRegReqForAppId(@PathParam("username") String username,
			@PathParam("appId") String appId) {
		RegistrationRequest[] regReq = getRegisReqPublic(username);
		setAppId(appId, regReq[0].header);
		return gson.toJson(regReq);
	}

	@GET
	@Path("/public/regRequest")
	@Produces(MediaType.APPLICATION_JSON)
	public RegistrationRequest[] postRegisReqPublic(String username) {
		RegistrationRequest[] regReq = new RegistrationRequest[1];
		regReq[0] = new FetchRequest(getAppId(), getAllowedAaids())
				.getRegistrationRequest(username);
		Dash.getInstance().stats.put(Dash.LAST_REG_REQ, regReq);
		Dash.getInstance().history.add(regReq);
		return regReq;
	}

	private String[] getAllowedAaids() {
		String[] ret = { "EBA0#0001", "0015#0001", "0012#0002", "0010#0001",
				"4e4e#0001", "5143#0001", "0011#0701", "0013#0001",
				"0014#0000", "0014#0001", "53EC#C002", "DAB8#8001",
				"DAB8#0011", "DAB8#8011", "5143#0111", "5143#0120",
				"4746#F816", "53EC#3801", "TCH0#0001", "PIN0#0001", "FCE0#0001" };
		return ret;
	}

	// New code added here
	/**
	 * Method that gets allowed AAIDs for given DeviceId.
	 * 
	 * @param deviceId
	 * @return
	 */
	private String[] getAllowedAaids(String deviceId, String accountId) {
		String[] ret = null;
		ret = StorageImpl.getInstance().getAAIDforDeviceIdAccountId(deviceId,
				accountId);
		System.out.println("Allowed AAIDs for user are: " + ret);
		return ret;
	}

	// New code added here
	/**
	 * Method that gets allowed AAIDs for given AccountId which are enforced at
	 * the time of login in based on user's context passed (Flexible
	 * authentication).
	 * 
	 * @param deviceId
	 * @return
	 */
	private String[] getAllowedAaids(String accountId) {
		String[] ret = null;
		ret = StorageImpl.getInstance().getAAIDsEnforcedForAccountId(accountId);
		System.out.println("Allowed AAIDs for user are: " + Arrays.toString(ret));
		return ret;
	}

	@GET
	@Path("/public/uaf/facets")
	@Produces("application/fido.trusted-apps+json")
	public Facets facets() {
		String timestamp = new Date().toString();
		Dash.getInstance().stats.put(Dash.LAST_REG_REQ, timestamp);
		String[] trustedIds = { "https://www.head2toes.org",
				"android:apk-key-hash:Df+2X53Z0UscvUu6obxC3rIfFyk",
				"android:apk-key-hash:bE0f1WtRJrZv/C0y9CM73bAUqiI",
				"https://openidconnect.ebay.com",
				"www.fidopersistent.pslbank.com",
				"www.fidopersistent.pslbank.iosmobile.com" };
		Facets facets = new Facets();
		facets.trustedFacets = new TrustedFacets[1];
		TrustedFacets trusted = new TrustedFacets();
		trusted.version = new Version(1, 0);
		trusted.ids = trustedIds;
		facets.trustedFacets[0] = trusted;
		return facets;
	}

	private String getAppId() {
		return "https://www.head2toes.org/fidouaf/v1/public/uaf/facets";
	}

	// New code added here
	@POST
	@Path("/public/regResponseStatus")
	@Consumes({ "text/plain", "application/xml", "application/json" })
	@Produces({ "text/plain" })
	public String getRegResponseStatus(String payload) {
		System.out
				.println("\n\nPayload received for get Registration Response: "
						+ payload);
		Gson gson = new Gson();
		// Login fromJson = gson.fromJson(payload, Login.class);
		// String uname = fromJson.getUsername();
		Device fromJson = gson.fromJson(payload, Device.class);
		// String phoneNumber = fromJson.getPhoneNumber();
		String rpAccountId = fromJson.getRpaccountid();
		String responseStatus = null;

		// Check if Registration Status is true
		if (StorageImpl.getInstance().getIsRegisteredUser(
		/* phoneNumber */rpAccountId)) {
			responseStatus = "SUCCESS";
		} else {
			responseStatus = "FAILURE";
		}
		return responseStatus;
	}

	// New code added here
	/*@POST
	@Path("/public/authResponseStatus")
	@Consumes({ "text/plain", "application/xml", "application/json" })
	@Produces({ "text/plain", "application/xml", "application/json" })
	public String getAuthResponseStatus(String payload) {
		System.out
				.println("\n\nPayload received for get Authentication Response: "
						+ payload);
		Gson gson = new Gson();
		/*
		 * Login fromJson = gson.fromJson(payload, Login.class); String uname =
		 * fromJson.getUsername();
		 */
		/*Vendor fromJson = gson.fromJson(payload, Vendor.class);
		String userDisplayName = fromJson.getDisplayName();
		String accountId = fromJson.getAccountId();

		// System.out.println("Phone Number to be authenticated: " + uname);
		System.out.println("Polling authentication status for user: "
				+ userDisplayName + " with AccountId: " + accountId);

		String responseStatus = null;
		AuthenticatorRecord[] authRecord = (AuthenticatorRecord[]) Dash
				.getInstance().stats.get(Dash.LAST_AUTH_RES_REPLY);
		if (authRecord == null) {
			responseStatus = "FAILURE";
		} else {
			if (authRecord[0].status.equalsIgnoreCase("SUCCESS")) {
				responseStatus = "SUCCESS";
			} else {
				responseStatus = "FAILURE";
			}
		}
		return responseStatus;

	}*/

	// New code added here
	@GET
	@Path("/public/clearAuthResponse")
	@Produces({ "text/plain", "application/xml", "application/json" })
	public String clearLastAuthResponse() {
		System.out
				.println("\nRequest received to clear last authentication response reply.");
		Dash.getInstance().stats.put(Dash.LAST_AUTH_RES_REPLY, null);
		return "SUCCESS";
	}

	// New code added here
	@POST
	@Path("/public/pushNotifyAuthentication")
	@Consumes({ "text/plain", "application/xml", "application/json" })
	@Produces({ "text/plain", "application/xml", "application/json" })
	public String pushNotifyToAuthenticate(String payload) {
		System.out
				.println("\n\nPayload received for Push notify Authentication: "
						+ payload);
		Gson gson = new Gson();
		Vendor fromJson = gson.fromJson(payload, Vendor.class);
		// String phone = fromJson.getPhoneNumber();
		final String accountId = fromJson.getAccountId();
		String vendorname = fromJson.getRpDisplayName();
		Map<String, String> retrievedUserData = new HashMap<String, String>();
		retrievedUserData = StorageImpl.getInstance()
				.getDevicetokenForAccountId(/* phone */accountId);

		String userDisplayName = StorageImpl.getInstance()
				.getUserDisplaynameFromVendorName(vendorname, accountId);

		// Send push notification to IOS device to indicate FIDO Authentication
		Map<String, String> dataToPushNotify = new HashMap<String, String>();
		dataToPushNotify.put("Vendorname", vendorname);
		dataToPushNotify.put("displayname", userDisplayName);
		dataToPushNotify.put("Token", retrievedUserData.get("devicetoken"));
		dataToPushNotify.put("operation", "FIDO Autheticate");
		new ProcessLogin().sendPushNotificationsToIOS(dataToPushNotify);

		// Set auth_in_progress field in Device details DB to true to mark the
		// beginning of authentication.
		StorageImpl.getInstance().markAuthenticationInProgressInDB(accountId);

		// Get auth related flags from registration DB based on accountid
		Map<String, Boolean> authFlags = new HashMap<String, Boolean>();
		authFlags = StorageImpl.getInstance().getAuthFlagsforAccountId(
				accountId);

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
					StorageImpl.getInstance().resetAuthFlagsInRegDB(accountId);
				}
			}, 2 * 60 * 1000);
		}
		System.out
				.println("Device Push Notified to go ahead with Authentication Process.");
		return "Success";
	}

	@POST
	@Path("/public/regResponse")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RegistrationRecord[] processRegResponse(String payload)
			throws IOException, JSONException {
		System.out.println("\n\nPayload received for Registration Response: "
				+ payload);
		RegistrationRecord[] result = null;
		Gson gson = new Gson();

		RegistrationResponse[] fromJson = gson.fromJson(payload,
				RegistrationResponse[].class);
		Dash.getInstance().stats.put(Dash.LAST_REG_RES, fromJson);
		Dash.getInstance().history.add(fromJson);

		RegistrationResponse registrationResponse = fromJson[0];
		result = new ProcessResponse().processRegResponse(registrationResponse);
		int eachAssertionSucessfulCount = 0;
		String deviceId = null;
		
		if (result != null) {
			for (int i = 0; i < result.length; i++) {
				if (result[i].status.equals("SUCCESS")) {
					
					/* Get deviceId for passed accountId as Username */
					deviceId = StorageImpl.getInstance()
							.getDeviceIdForAccountId(result[i].username);
					// try {
					// Increment counter for each successful assertion validation;
					eachAssertionSucessfulCount++;
					// StorageImpl.getInstance().store(result);
					Map<String, String> dataToStore = new HashMap<String, String>();
					dataToStore.put("AccountId", result[i].username);
					dataToStore.put("Publickey", result[i].PublicKey);
					dataToStore.put("aaid_keyid",
							result[i].authenticator.toString());
					dataToStore.put("DeviceId", deviceId);
					// StorageImpl.getInstance().storeInDatabase(dataToStore);

					// Store in Key_Info Table.
					StorageImpl.getInstance().storeInKeyInfoDB(dataToStore);

					// StorageImpl.getInstance().displayAllRecords();
					/*
					 * } catch (DuplicateKeyException e) { result = new
					 * RegistrationRecord[1]; result[0] = new
					 * RegistrationRecord(); result[0].status =
					 * "Error: Duplicate Key"; } catch (SystemErrorException e1)
					 * { result = new RegistrationRecord[1]; result[0] = new
					 * RegistrationRecord(); result[0].status =
					 * "Error: Data couldn't be stored in DB"; }
					 */
				}
			}
		} else {
			result = new RegistrationRecord[1];
			result[0] = new RegistrationRecord();
			result[0].status = "Error: Empty/Null result";
		}

		// Update Registration status for each deviceId - AccountId combo in reg
		// DB
		if (eachAssertionSucessfulCount == result.length) {
			StorageImpl.getInstance().updateRegStatsInRegDB(result[0].username,
					deviceId);
		}

		// Check all Data in devicedetails table.
		System.out.println("\nAll data in DeviceDetails Table: ");
		StorageImpl.getInstance().showDeviceDBRecords();
		// Check all data in registration table.
		System.out.println("\nAll data in RegistrationDB Table: ");
		StorageImpl.getInstance().showRegistrationDBRecords();
		// check all Data in key_info table.
		System.out.println("\nAll Data in Key_info Table: ");
		StorageImpl.getInstance().showKeyInfoDBRecords();

		return result;
	}

	// New code added here..........
	/**
	 * new Registration Response API for IOS requests
	 * 
	 * @param payload
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	@POST
	@Path("/public/ios/regResponse")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RegistrationRecord[] processIOSRegResponse(String payload)
			throws IOException, JSONException {
		System.out
				.println("\n\nPayload received for Registration Response (IOS/RSA): "
						+ payload);
		RegistrationRecord[] result = null;
		Gson gson = new Gson();

		RegistrationResponse[] fromJson = gson.fromJson(payload,
				RegistrationResponse[].class);
		Dash.getInstance().stats.put(Dash.LAST_REG_RES, fromJson);
		Dash.getInstance().history.add(fromJson);

		RegistrationResponse registrationResponse = fromJson[0];
		result = new ProcessResponse()
				.processIOSRegResponse(registrationResponse);
		if (result[0].status.equals("SUCCESS")) {
			try {
				StorageImpl.getInstance().store(result);
				Map<String, String> dataToStore = new HashMap<String, String>();
				dataToStore.put("Username", result[0].username);
				dataToStore.put("Publickey", result[0].PublicKey);
				dataToStore.put("aaid_keyid",
						result[0].authenticator.toString());
				dataToStore.put("DeviceId", result[0].deviceId);
				StorageImpl.getInstance().storeInDatabase(dataToStore);

				// mark user as REGISTERED in userLogin table.
				Map<String, String> loginDataToStore = new HashMap<String, String>();
				loginDataToStore.put("Username", result[0].username);
				loginDataToStore.put("regStats", "true");
				StorageImpl.getInstance().storeInLogin(loginDataToStore);

				// Check all Data in userDetails table.
				System.out.println("\n\n\nAll data in Database: ");
				StorageImpl.getInstance().displayAllRecords();
			} catch (DuplicateKeyException e) {
				result = new RegistrationRecord[1];
				result[0] = new RegistrationRecord();
				result[0].status = "Error: Duplicate Key";
			} catch (SystemErrorException e1) {
				result = new RegistrationRecord[1];
				result[0] = new RegistrationRecord();
				result[0].status = "Error: Data couldn't be stored in DB";
			}
		}
		return result;
	}

	// New code added here.........
	/**
	 * New API for Signup
	 * 
	 * @param payload
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */

	/*
	 * @POST
	 * 
	 * @Path("/public/dosignup")
	 * 
	 * @Consumes(MediaType.APPLICATION_JSON)
	 * 
	 * @Produces(MediaType.APPLICATION_JSON) public String signup(String
	 * payload) { System.out.println("\n\nPayload received for Signup request: "
	 * + payload); Gson gson = new Gson(); Login fromJson =
	 * gson.fromJson(payload, Login.class); String uname =
	 * fromJson.getUsername(); //String pwd = fromJson.getPassword(); String
	 * response = null; String message = null; Map<String, String> dataMap = new
	 * HashMap<String, String>(); dataMap.put("Username", uname);
	 * dataMap.put("Password", pwd); try { message =
	 * StorageImpl.getInstance().storeInLogin(dataMap); response = new
	 * ProcessLogin().constructJSONForSignUp(message); } catch (IOException e) {
	 * e.printStackTrace(); } return response; }
	 */

	// New Code added here.........
	/**
	 * New API for Login
	 * 
	 * @param payload
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */

	/*
	 * @POST
	 * 
	 * @Path("/public/dologin")
	 * 
	 * @Consumes(MediaType.APPLICATION_JSON)
	 * 
	 * @Produces(MediaType.APPLICATION_JSON) public String login(String payload)
	 * { System.out.println("\n\nPayload received for doLogin: " + payload);
	 * Gson gson = new Gson(); Login fromJson = gson.fromJson(payload,
	 * Login.class); String uname = fromJson.getUsername(); //String pwd =
	 * fromJson.getPassword(); String response = null; String message = null;
	 * boolean loginStatus = false; if (new
	 * ProcessLogin().checkCredentials(uname, pwd)) { loginStatus = true;
	 * message = "User Logged in successfully."; response = new
	 * ProcessLogin().constructJSON(message, loginStatus);
	 * 
	 * // Get device token for username from Reg DB to send push notifications.
	 * String deviceToken =
	 * StorageImpl.getInstance().getDevicetokenForUsername(uname);
	 * 
	 * // Send push notification to IOS device to indicate FIDO Authentication
	 * Map<String, String> dataToPushNotify = new HashMap<String, String>();
	 * dataToPushNotify.put("Username", uname); dataToPushNotify.put("Token",
	 * deviceToken); dataToPushNotify.put("operation", "FIDO Autheticate"); new
	 * ProcessLogin().sendPushNotificationsToIOS(dataToPushNotify); } else {
	 * message = "Invalid Username or Password."; response = new
	 * ProcessLogin().constructJSON(message, loginStatus); } return response; }
	 */

	// New Code added here.........
	/**
	 * New API for Login Request
	 * 
	 * @param payload
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	@POST
	@Path("/public/loginRequest")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String loginReq(String payload) {
		System.out
				.println("\n\nPayload received for Login Request: " + payload);
		String response;
		Gson gson = new Gson();
		Login fromJson = gson.fromJson(payload, Login.class);
		String uname = fromJson.getUsername();

		Map<String, String> otpDetails = new HashMap<String, String>();

		// Generate OTP
		otpDetails = new ProcessLogin().generateOTP(uname);
		String generatedOTP = otpDetails.get("Generated OTP");
		String dateGeneratedOTP = otpDetails.get("Date");

		// created a map to collect all data together and send it to
		// Registration DB for
		// storage.
		Map<String, String> dataToStoreInRegDB = new HashMap<String, String>();
		dataToStoreInRegDB.put("PhoneNumber", uname);
		dataToStoreInRegDB.put("OTP", generatedOTP);
		dataToStoreInRegDB.put("Creation Date", dateGeneratedOTP);

		// Store username and OTP in Registration Database.
		// String otpForPreRegisteredUser =
		StorageImpl.getInstance().storeInRegDB(dataToStoreInRegDB);

		/*
		 * if (otpForPreRegisteredUser != null) {
		 * System.out.println("\nPre- Registered OTP Stored for user: " +
		 * otpForPreRegisteredUser); response = new ProcessLogin()
		 * .constructJSON(otpForPreRegisteredUser); // new
		 * ProcessLogin().sendSMS(uname, otpForPreRegisteredUser); } else {
		 */
		System.out.println("\nOTP Generated at Login Request: " + generatedOTP);
		response = new ProcessLogin().constructJSON(generatedOTP);
		// new ProcessLogin().sendSMS(uname, generatedOTP);
		// }
		return response;
	}

	// New Code added here.........
	/**
	 * New API for Login confirmation/ response
	 * 
	 * @param payload
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	@POST
	@Path("/public/loginResponse")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String loginConfirm(String payload) {
		System.out.println("\n\nPayload received for Login response: "
				+ payload);
		String response = null;
		boolean registrationStatus = false;
		boolean loginStatus = false;
		Gson gson = new Gson();
		Login fromJson = gson.fromJson(payload, Login.class);
		String uname = fromJson.getUsername();
		String otpReceived = fromJson.getOtp();

		// Generate Current Time in Milliseconds
		long presentTime = System.currentTimeMillis();

		// Fetch OTP and creation date for OTP stored for user in Registration
		// table.
		Map<String, String> otpStoredDetails = new HashMap<String, String>();
		otpStoredDetails = StorageImpl.getInstance().getOtpDetailsForUsername(
				uname);
		String otpStored = otpStoredDetails.get("OTP");

		// Convert string type to date format.
		Date otpCreationDate = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			otpCreationDate = formatter.parse(otpStoredDetails
					.get("Creation Date"));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// Calculate delay
		long interval = otpCreationDate.getTime() - presentTime;
		long timeOutInterval = Long.parseLong("180000");

		if (timeOutInterval < interval) {
			System.out.println("\nOTP Expired. Try Login again.");
		} else {
			if (otpReceived.equals(otpStored)) {
				System.out.println("\nOTP Matched!!! Authorized USER!");
				// if OTP matched, mark user's login status as TRUE.
				StorageImpl.getInstance().updateRegDBLogin(uname);
				loginStatus = true;
				response = new ProcessLogin().constructJSON(loginStatus,
						registrationStatus);
			} else {
				System.out.println("\nOTP Mis-match!!! UnAuthorized USER!");
				response = new ProcessLogin().constructJSON(loginStatus,
						registrationStatus);
			}
		}
		return response;
	}

	// New Code added here.........
	/**
	 * New API for Logout (Drops all DB Tables data. Used in case of Crashes).
	 * 
	 * @return response
	 */
	@GET
	@Path("/public/dologout/{deviceid}")
	@Produces(MediaType.APPLICATION_JSON)
	public String logout(@PathParam("deviceid") String deviceId) {

		System.out.println("Device ID received for Logout: " + deviceId);
		String response = "";
		boolean loginStatus = false;
		String message;

		// Gson gson = new Gson();
		// //Login fromJson = gson.fromJson(payload, Login.class);
		// Device fromJson = gson.fromJson(payload, Device.class);
		// //String phonenumber = fromJson.getPhoneNumber();
		// String deviceId = fromJson.getDeviceid();

		loginStatus = StorageImpl.getInstance()
				.removeVendorAndUserDetailsFromDB(/* phonenumber *//* deviceId */);

		if (loginStatus == true) {
			message = "User is successfully logged out...";
			response = new ProcessLogin().constructJSON(message, loginStatus);
		} else {
			// message = "Phone Number is incorrect. Logout Failed!";
			message = "Logout Failed!";
			response = new ProcessLogin().constructJSON(message, loginStatus);
		}

		return response;
	}

	// New Code added here.........
	/**
	 * New API for Add new Vendor.
	 * 
	 * @param payload
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	@POST
	@Path("/public/rpRegRequest")
	@Consumes({ "text/plain", "application/xml", "application/json" })
	@Produces({ "text/plain", "application/xml", "application/json" })
	public String addVendor(String payload) {
		System.out.println("\n\nPayload received for RP Registration Request: "
				+ payload);
		String response = null;

		Gson gson = new Gson();
		Vendor fromJson = gson.fromJson(payload, Vendor.class);
		String vendorName = fromJson.getRpDisplayName();
		String username = fromJson.getDisplayName();
		String email = fromJson.getEmail();
		String accountId = fromJson.getAccountId();

		// Might need to store this authenticator details in DB under vendordb
		// table.
		// Authenticators authenticators = fromJson.getAuthenticators();
		// System.out.println("\nAAIDs received at the time of RP Registration request");
		// String[] aaids = authenticators.getAaid();
		/*
		 * for (int i=0; i<aaids.length; i++){ System.out.println(aaids[i]); }
		 */
		// String phoneNumber = fromJson.getPhoneNumber();

		/************ No Push notification during RP registration in FIDO V0.2 *************/
		// Get device token for username from Reg DB to send push notifications.
		/*
		 * Map<String, String> retrievedUserData = new HashMap<String,
		 * String>(); retrievedUserData = StorageImpl.getInstance()
		 * .getDevicetokenForUsername(phoneNumber);
		 * 
		 * // Send push notification to IOS device with data received from RP //
		 * asking for RP registration Map<String, String> dataToPushNotify = new
		 * HashMap<String, String>(); dataToPushNotify.put("Vendor",
		 * vendorName); dataToPushNotify.put("Token",
		 * retrievedUserData.get("devicetoken"));
		 * dataToPushNotify.put("operation", "Register PSL FIDO"); new
		 * ProcessLogin().sendPushNotificationsToIOS(dataToPushNotify);
		 */

		// Generate OTP for vendor registration confirmation.
		Map<String, String> otpDetails = new HashMap<String, String>();
		otpDetails = new ProcessLogin().generateOTPwithoutSMS();
		String generatedOTP = otpDetails.get("Generated OTP");
		String dateGeneratedOTP = otpDetails.get("Date");

		// created a map to collect all data together and send it to DB for
		// storage.
		Map<String, String> dataToStore = new HashMap<String, String>();
		dataToStore.put("Vendor", vendorName);
		dataToStore.put("Username", username);
		// dataToStore.put("Phone", phoneNumber);
		dataToStore.put("email", email);
		dataToStore.put("accountid", accountId);
		dataToStore.put("OTP", generatedOTP);
		dataToStore.put("Date", dateGeneratedOTP);

		StorageImpl.getInstance().storeInVendorDB(dataToStore);

		System.out
				.println("\nListening for Response ****************************************");
		response = new ProcessLogin().constructJSON(generatedOTP);
		return response;
	}

	/********************** INTER_APP Flexible Authentication AppRegRequest API *************************/
	// New Code added here.........
	/**
	 * New API for Add new Vendor for App.
	 * 
	 * @param payload
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	@POST
	@Path("/public/appRegRequest")
	@Consumes({ "text/plain", "application/xml", "application/json" })
	@Produces({ "text/plain", "application/xml", "application/json" })
	public String addAPPVendor(String payload) {
		System.out.println("\n\nPayload received for App Registration Request: "
				+ payload);
		String response = null;

		Gson gson = new Gson();
		Vendor fromJson = gson.fromJson(payload, Vendor.class);
		String vendorName = fromJson.getRpDisplayName();
		String username = fromJson.getDisplayName();
		String email = fromJson.getEmail();
		String accountId = fromJson.getAccountId();

		// Might need to store this authenticator details in DB under vendordb
		// table.
		/*Authenticators authenticators = fromJson.getAuthenticators();
		System.out
				.println("\nAAIDs received at the time of APP Registration request");
		String[] aaids = authenticators.getAaid();

		for (int i = 0; i < aaids.length; i++) {
			System.out.println(aaids[i]);
		}*/

		// Generate OTP for vendor registration confirmation.
		Map<String, String> otpDetails = new HashMap<String, String>();
		otpDetails = new ProcessLogin().generateOTPwithoutSMS();
		String generatedOTP = otpDetails.get("Generated OTP");
		String dateGeneratedOTP = otpDetails.get("Date");

		// created a map to collect all data together and send it to DB for
		// storage.
		Map<String, String> dataToStore = new HashMap<String, String>();
		dataToStore.put("Vendor", vendorName);
		dataToStore.put("Username", username);
		dataToStore.put("email", email);
		dataToStore.put("accountid", accountId);
		dataToStore.put("OTP", generatedOTP);
		dataToStore.put("Date", dateGeneratedOTP);

		StorageImpl.getInstance().storeInVendorDB(dataToStore);

		System.out
				.println("\nListening for Response ****************************************");
		response = new ProcessLogin().constructJSON(generatedOTP);
		return response;
	}

	/********************** INTER_APP Flexible Authentication AppRegRequest API *************************/

	// New Code added here.........
	/**
	 * New API to confirm Add new Vendor operation.
	 * 
	 * @param payload
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	/*
	 * @POST
	 * 
	 * @Path("/public/rpRegResponse")
	 * 
	 * @Consumes(MediaType.APPLICATION_JSON)
	 * 
	 * @Produces(MediaType.APPLICATION_JSON) public String
	 * addVendorConfirm(String payload) { System.out
	 * .println("\n\nPayload received for RP Registration Response: " +
	 * payload); String response = null; boolean vendorRegStats = false; Gson
	 * gson = new Gson(); Vendor fromJson = gson.fromJson(payload,
	 * Vendor.class); String vendorName = fromJson.getRpDisplayName(); String
	 * phoneNumber = fromJson.getPhoneNumber(); String otpReceived =
	 * fromJson.getOtp();
	 * 
	 * // Generate Current Time in Milliseconds long presentTime =
	 * System.currentTimeMillis();
	 * 
	 * // Fetch OTP and creation date for OTP stored for user in Vendor table.
	 * Map<String, String> otpStoredDetails = new HashMap<String, String>();
	 * otpStoredDetails = StorageImpl.getInstance().getOtpForVendor(phoneNumber,
	 * vendorName); String otpStored = otpStoredDetails.get("OTP");
	 * 
	 * // Convert string type to date format. Date otpCreationDate = null;
	 * SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 * try { otpCreationDate = formatter.parse(otpStoredDetails
	 * .get("Creation Date")); } catch (ParseException e) { e.printStackTrace();
	 * }
	 * 
	 * // Calculate delay long interval = otpCreationDate.getTime() -
	 * presentTime; long timeOutInterval = Long.parseLong("180000"); if
	 * (timeOutInterval < interval) {
	 * System.out.println("\nOTP Expired. Try Login again."); } else { // Check
	 * OTP Matching. if (otpReceived.equals(otpStored)) { System.out
	 * .println("\nOTP Matched!!! New Vendor Registered successfully.");
	 * StorageImpl.getInstance().updateVendorRegStatus(phoneNumber,vendorName);
	 * // Get Phone number for given vendor and user // String phoneNumber = //
	 * StorageImpl.getInstance().getPhoneForVendorAndUser(username, //
	 * vendorName);
	 * 
	 * // Update vendor details in devicedetails table using RP account
	 * id(foreign key)
	 * StorageImpl.getInstance().updateVendorInRegDB(phoneNumber,vendorName);
	 * vendorRegStats = true; response = new ProcessLogin()
	 * .constructJSONForUsePSLFIDORequest(vendorName, vendorRegStats); } else {
	 * System.out .println("\nOTP Mis-match!!! Vendor Registration failed.");
	 * response = new ProcessLogin()
	 * .constructJSONForUsePSLFIDORequest(vendorName, vendorRegStats); } }
	 * return response; }
	 */

	// New Code added here.........
	/**
	 * New API to get all details for Vendor (relying party) as a response to
	 * POST call after push notifications.
	 * 
	 * @param payload
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	@POST
	@Path("/public/getMakeCredentialDetails")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String showVendorDetails(String payload) {
		System.out
				.println("\nPayload received for MakeCredentials: " + payload);
		String response = null;
		Gson gson = new Gson();
		Vendor fromJson = gson.fromJson(payload, Vendor.class);
		/*
		 * String vendorName = fromJson.getRpDisplayName(); String phonenumber =
		 * fromJson.getPhoneNumber();
		 */

		String accountId = fromJson.getAccountId();

		Map<String, String> responseData = new HashMap<String, String>();
		responseData = StorageImpl.getInstance().getVendorDetails(/*
																 * phonenumber,
																 * vendorName
																 */accountId);
		response = new ProcessLogin()
				.constructJSONForPushNotifyReply(responseData);

		return response;
	}

	// New Code added here........
	/**
	 * New API to Grant Push notification permission to the FIDO server from
	 * mobile client.
	 * 
	 * @param payload
	 * @return
	 */
	/*@POST
	@Path("/public/grantPushNotifications")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String grantPushNotifications(String payload) {
		System.out.println("\nPayload received for grantPush Notifications: "
				+ payload);
		String response = null;
		boolean deviceTokenUpdated = false;
		Gson gson = new Gson();
		Login fromJson = gson.fromJson(payload, Login.class);
		String uname = fromJson.getUsername();
		String devicetoken = fromJson.getDeviceToken();

		// Store device token against the username in registration table.
		StorageImpl.getInstance().storeTokenInRegDB(uname, devicetoken);
		deviceTokenUpdated = true;

		response = new ProcessLogin().constructJSONGrantPushServices(uname,
				deviceTokenUpdated);
		return response;
	}*/

	// New Code added here.........
	/**
	 * New API to allow vendor to use PSL FIDO server for FIDO services.
	 * 
	 * @param payload
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	/*
	 * @POST
	 * 
	 * @Path("/public/fidoServerRequest")
	 * 
	 * @Consumes(MediaType.APPLICATION_JSON)
	 * 
	 * @Produces(MediaType.APPLICATION_JSON) public String useFidoServer(String
	 * payload) throws IOException {
	 * System.out.println("\n\nVendor details passed to Use PSL FIDO Server: " +
	 * payload); String response = null; boolean isVendorRegistered = false;
	 * Gson gson = new Gson(); Vendor fromJson = gson.fromJson(payload,
	 * Vendor.class); String vendorName = fromJson.getRpDisplayName(); //
	 * fromJson.getAccount().getRpDisplayName(); String phonenumber =
	 * fromJson.getPhoneNumber(); // fromJson.getAccount().getDisplayName();
	 * 
	 * // Check if vendor is registered for given vendor_name and phone_number
	 * // in vendor table. isVendorRegistered =
	 * StorageImpl.getInstance().checkVendorRegistration( vendorName,
	 * phonenumber);
	 * 
	 * if (isVendorRegistered) { System.out.println("\nThe Given vendor: " +
	 * vendorName + " is registered to use PSL FIDO services.");
	 * 
	 * // Get device token for username from Reg DB to send push //
	 * notifications. Map<String, String> retrievedUserData = new
	 * HashMap<String, String>(); retrievedUserData = StorageImpl.getInstance()
	 * .getDevicetokenForUsername(phonenumber);
	 * 
	 * String userDisplayName = StorageImpl.getInstance()
	 * .getUserDisplaynameFromVendorName(vendorName);
	 * 
	 * // Send Push notifications to mobile Map<String, String> dataToBePushed =
	 * new HashMap<String, String>(); dataToBePushed.put("Vendor", vendorName);
	 * dataToBePushed.put("displayname", userDisplayName);
	 * dataToBePushed.put("Token", retrievedUserData.get("devicetoken"));
	 * dataToBePushed.put("operation", "Use PSL Fido"); new
	 * ProcessLogin().sendPushNotificationsToIOS(dataToBePushed); // wait for
	 * auth response response = new
	 * ProcessLogin().constructJSONForUsePSLFIDORequest( vendorName,
	 * isVendorRegistered); } else { System.out.println("\nThe Given vendor: " +
	 * vendorName + " is not registered to use PSL FIDO services."); response =
	 * new ProcessLogin().constructJSONForUsePSLFIDORequest( vendorName,
	 * isVendorRegistered); } return response; }
	 */

	/*
	 * BEGIN*************************************** Version 0.2 - No login, OTP
	 * services **************************************
	 */

	// New Code added here........
	/**
	 * New API to enable Push notification permission to the FIDO server from
	 * mobile client at the first launch of the mobile app after download.
	 * 
	 * @param payload
	 * @return
	 */
	@POST
	@Path("/public/enablePushNotifications")
	@Consumes({ "text/plain", "application/xml", "application/json" })
	@Produces({ "text/plain", "application/xml", "application/json" })
	public String enablePushNotifications(String payload) {
		System.out.println("\nPayload received to enable Push Notifications: "
				+ payload);
		String response = null;
		boolean deviceDataUpdated = false;
		Gson gson = new Gson();
		Device fromJson = gson.fromJson(payload, Device.class);
		String deviceid = fromJson.getDeviceid();
		String devicetoken = fromJson.getDevicetoken();
		// String phonenumber = fromJson.getPhoneNumber();

		// Store device token, device id and phone number upon first allow push
		// notification request.
		Map<String, String> dataToStore = new HashMap<String, String>();
		dataToStore.put("deviceid", deviceid);
		dataToStore.put("devicetoken", devicetoken);
		// dataToStore.put("phone", phonenumber);
		deviceDataUpdated = StorageImpl.getInstance()
				.storePushNotifyEnabledDevice(dataToStore);

		response = new ProcessLogin()
				.constructJSONForPushNotifiedDevice(deviceDataUpdated);
		return response;
	}

	// New Code added here........
	/**
	 * New API to enable Push notification permission to the FIDO server from
	 * mobile client at the first launch of the mobile app after download.
	 * 
	 * @param payload
	 * @return
	 */
	@POST
	@Path("/public/verifyQRContents")
	@Consumes({ "text/plain", "application/xml", "application/json" })
	@Produces({ "text/plain", "application/xml", "application/json" })
	public String verifyQRContents(String payload) {
		System.out.println("\nPayload received to verify QR code: " + payload);
		String response = null;
		boolean isQRVerified = false;
		Gson gson = new Gson();
		Vendor fromJson = gson.fromJson(payload, Vendor.class);
		String rpdisplayName = fromJson.getRpDisplayName();
		String displayname = fromJson.getDisplayName();
		String email = fromJson.getEmail();
		String otp = fromJson.getOtp();
		String accountId = fromJson.getAccountId();
		String deviceId = fromJson.getDeviceId();

		Map<String, String> storedDBData = StorageImpl.getInstance()
				.getRPDetailsForAccountId(accountId);
		if ((rpdisplayName.equals(storedDBData.get("RPDisplayName")))
				&& (displayname.equals(storedDBData.get("DisplayName")))
				&& (email.equals(storedDBData.get("Email")))
				&& (otp.equals(storedDBData.get("OTP")))
				&& (accountId.equals(storedDBData.get("AccountId")))) {
			System.out.println("\nQR Data Verified successfully.");
			isQRVerified = true;

			System.out
					.println("\nUpdating Vendor Registration status in Vendor table");
			StorageImpl.getInstance().updateVendorRegStatusInVendorDB(
					rpdisplayName, accountId);

			System.out
					.println("\nInserting RP Name and RP AccountId for given DeviceId in registration DB.");
			Map<String, String> dataToUpdate = new HashMap<String, String>();
			dataToUpdate.put("DeviceId", deviceId);
			dataToUpdate.put("RPDisplayName", rpdisplayName);
			dataToUpdate.put("AccountId", accountId);

			StorageImpl.getInstance().insertRPDetailsInRegDB(dataToUpdate);

			response = new ProcessLogin()
					.constructJSONForQRVerification(isQRVerified);
		} else {
			System.out.println("\nQR Data Incorrect. Please Re-Scan!");
			response = new ProcessLogin()
					.constructJSONForQRVerification(isQRVerified);
		}
		return response;
	}

	/**
	 * Method that accepts transaction details from RP and processes it.
	 * 
	 * @param payload
	 * @return
	 */
	// New code added here
	@POST
	@Path("/public/sendTransactions")
	@Consumes({ "text/plain", "application/xml", "application/json" })
	@Produces({ "text/plain", "application/xml", "application/json" })
	public String getTransactionDetails(String payload) {
		System.out
				.println("\n\nPayload received from RP for processing transaction: "
						+ payload);
		Gson gson = new Gson();
		TransactionDetails fromJson = gson.fromJson(payload,
				TransactionDetails.class);
		final String accountId = fromJson.getAccountId();
		Content contentObj = fromJson.getContent();
		
		String appId = fromJson.getAppId();

		// convert content object to json string.
		String content = gson.toJson(contentObj);

		// Store transaction details in transaction DB.
		Map<String, String> dataToStore = new HashMap<String, String>();
		dataToStore.put("accountid", accountId);
		dataToStore.put("content", content);
		dataToStore.put("appid", appId);
		StorageImpl.getInstance().storeInTransactionDB(dataToStore);

		// Get device token using the account_id
		Map<String, String> dataforPushNotifications = new HashMap<String, String>();
		dataforPushNotifications = StorageImpl.getInstance()
				.getDevicetokenForAccountId(accountId);

		Map<String, String> dataToPushNotify = new HashMap<String, String>();
		dataToPushNotify.put("Token",
				dataforPushNotifications.get("devicetoken"));
		dataToPushNotify.put("amount", contentObj.getAmount_transferred());
		dataToPushNotify.put("username", contentObj.getUsername());
		dataToPushNotify.put("to", contentObj.getTo());
		dataToPushNotify.put("operation", "FIDO Transaction");

		// Send push notification to device, informing about the transaction
		// happening.
		new ProcessLogin().sendPushNotificationsToIOS(dataToPushNotify);

		// Set auth_in_progress field in Device details DB to true to mark the
		// beginning of authentication.
		StorageImpl.getInstance().markAuthenticationInProgressInDB(accountId);

		// Get auth related flags from DB based on accountid
		Map<String, Boolean> authFlags = new HashMap<String, Boolean>();
		authFlags = StorageImpl.getInstance().getAuthFlagsforAccountId(
				accountId);

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
					StorageImpl.getInstance().resetAuthFlagsInRegDB(accountId);
				}
			}, 2 * 60 * 1000);
		}

		System.out
				.println("Device Push Notified to Authenticate the Transaction.");
		return "SUCCESS";
	}

	/**
	 * Method that processes push notification acknowledgment from device and
	 * returns more details about the user.
	 * 
	 * @param payload
	 * @return
	 */
	// New code added here
	@POST
	@Path("/public/processPushNotificationAck")
	@Consumes({ "text/plain", "application/xml", "application/json" })
	@Produces({ "text/plain", "application/xml", "application/json" })
	public String processNotificationAcknowledgment(String payload) {
		System.out
				.println("\n\nPayload received for processing the push notification acknowledgment: "
						+ payload);
		String response = null;
		Gson gson = new Gson();
		Device fromJson = gson.fromJson(payload, Device.class);
		String rpAccountId = fromJson.getRpaccountid();

		// Get appId and content details using the rpAccountId
		Map<String, String> responseData = new HashMap<String, String>();
		responseData = StorageImpl.getInstance().getTransactionUsingAccountId(
				rpAccountId);

		// Convert Content JSON string stored in DB into Content Object to
		// extract the fields in it.
		Content contentJson = gson.fromJson(responseData.get("content"),
				Content.class);
		String amount = contentJson.getAmount_transferred();
		String username = contentJson.getUsername();
		String to = contentJson.getTo();

		// Finally store the actual content string to be show to user as output.
		responseData.put("contentString", "Approve Transaction of Rs. "
				+ amount + " from account with username: " + username + " to: "
				+ to);
		response = new ProcessLogin()
				.constructJSONForMoreTransactionDetails(responseData);

		return response;
	}

	/*
	 * END*************************************** Version 0.2 - No login, OTP
	 * services **************************************
	 */

	/*
	 * BEGIN*************************************** Flexible Authentication APIs
	 * **********************************************
	 */

	// New coded Added here.
	/**
	 * Method to Notify FIDO server the authenticators enforced onto the user
	 * during login based on the context passed (flexible Authentucation).
	 * 
	 * @param payload
	 * @return
	 */
	@POST
	@Path("/public/notifyAuthenticators")
	@Consumes({ "text/plain", "application/xml", "application/json" })
	@Produces({ "text/plain", "application/xml", "application/json" })
	public String notifyAuthenticatorsToFIDOServer(String payload) {
		System.out
				.println("\n\nPayload received for notifying authenticators to FIDO server: "
						+ payload);
		Gson gson = new Gson();
		Device fromJson = gson.fromJson(payload, Device.class);
		String rpAccountId = fromJson.getRpaccountid();
		String context = fromJson.getContext();
		Authenticators[] authenticators = fromJson.getAuthenticators();
		
		String aaids[] = new String [authenticators.length];
		for(int i=0;i<authenticators.length;i++){
			aaids[i] = authenticators[i].getAaid();
		}
		 
		// update context and authenticators enforced on the given accountId in
		// DB.
		Map<String, String> responseData = new HashMap<String, String>();
		responseData.put("accountId", rpAccountId);
		responseData.put("context", context);
		responseData.put("authenticators", Arrays.toString(aaids));

		int isUpdateSuccess = StorageImpl.getInstance()
				.updateUserContextAndAuthenticators(responseData);

		if (isUpdateSuccess != 0) {
			return NOTIFIED;
		} else {
			return ERROR_IN_UPDATE;
		}
	}

	// New coded Added here.
	/**
	 * Method to form policy based on the enforced AAIDs stored /set up for user
	 * at the time of login based on the context passed (Flexible
	 * authentication).
	 * 
	 * @param accountId
	 * @return
	 */
	@GET
	@Path("/public/authRequest/flexiblePolicy/{accountid}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAuthReq(@PathParam("accountid") String accountId) {
		System.out.println("Account Id: " + accountId);
		AuthenticationRequest[] authReqObj = getAuthReqResponse(accountId);
		return gson.toJson(authReqObj);
	}

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
	/*@POST
	@Path("/public/validateContexts")
	@Consumes({ "text/plain", "application/xml", "application/json" })
	@Produces({ "text/plain", "application/xml", "application/json" })
	public String validateEncryptedContexts(String payload)
			throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchProviderException, SignatureException,
			UnsupportedEncodingException, Exception {
		System.out.println("\n\nPayload received for Validating Contexts: "
				+ payload);
		
		String response = VERIFIED;
		ArrayList<Device> storeUnverifiedContexts = new ArrayList<Device>();
		Gson gson = new Gson();
		Device fromJson = gson.fromJson(payload, Device.class);
		String rpAccountId = fromJson.getRpaccountid();
		ContextDetails[] contextDetails = fromJson.getContextDetails();
		
		boolean[] allContextsVerified = new boolean[contextDetails.length];
		
		//Get public key for accountId and aaid passed in the input JSON.
		for(int i=0;i<contextDetails.length;i++){
			String aaid = contextDetails[i].getAaid();
			
			String pubKey = StorageImpl.getInstance().getPubKeyforAccountIdAndAAID(rpAccountId, aaid);
			allContextsVerified[i] = AuthenticationResponseProcessing.verifyContextSignatures(contextDetails[i].getSignedData(), contextDetails[i].getSignature(), pubKey);	
			
			if(allContextsVerified[i] == false){
				Device device = new Device();
				device.setRpaccountid(rpAccountId);
				device.setContextDetails(contextDetails);
				
				storeUnverifiedContexts.add(device); 
			}
		}
		
		//Print out context data that was not verified for logging purpose.
		/*System.out.println("Printing out the Context details that were not verified (if any): ");
		for(int m=0; m<contextList.length; m++){
			System.out.println(contextList[m]);
		}*/
		
		//If any one context is not verified, return response as NOT_VERIFIED
		/*for(int j=0;j<allContextsVerified.length;j++){
			if(allContextsVerified[j] == false){
				response = NOT_VERIFIED;
			}
		}
		
		return response;
	}*/
	
	
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
	@POST
	@Path("/public/getSignedContext")
	@Consumes({ "text/plain", "application/xml", "application/json" })
	@Produces({ "text/plain", "application/xml", "application/json" })
	public String createSignedContexts(String payload)
			throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchProviderException, SignatureException,
			UnsupportedEncodingException, Exception {
		System.out.println("\n\nPayload received for creating signed contexts: "
				+ payload);
		
		String response = null;
		
		Gson gson = new Gson();
		Device fromJson = gson.fromJson(payload, Device.class);
		String rpAccountId = fromJson.getRpaccountid();
		
		PrivateKey privateKey = BasicSignAndVerify.getPrivateKey();
		
		//Get Context from table in DB based on accountId
		String context = StorageImpl.getInstance().getContextForAccountID(rpAccountId);
		
		String signedContext = BasicSignAndVerify.sign(context,privateKey);
		
		response = new ProcessLogin().constructJSONForSignedContext(signedContext);
		return response;
	}

	/*
	 * END*************************************** Flexible Authentication APIs
	 * **********************************************
	 */

	@POST
	@Path("/public/deregRequest")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deregRequestPublic(String payload) {

		return new DeregRequestProcessor().process(payload);
	}

	@GET
	@Path("/public/authRequest")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAuthReq() {
		return gson.toJson(getAuthReqObj());
	}

	/****************************************** MULTIPLE ASSERTIONS PROCESSING **************************************/

	// New coded Added here.
	@GET
	@Path("/public/authRequest/customPolicy/{deviceid}/{accountid}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAuthReq(@PathParam("deviceid") String deviceId,
			@PathParam("accountid") String accountId) {
		System.out.println("Device Id: " + deviceId + "\nAccount Id: "
				+ accountId);
		AuthenticationRequest[] authReqObj = getAuthReqResponse(deviceId,
				accountId);
		return gson.toJson(authReqObj);
	}

	// New coded Added here.
	@GET
	@Path("/public/authRequest/customPolicy/{deviceid}/{accountid}/{appId}/{trxContent}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAuthTrxReq(@PathParam("deviceid") String deviceId,
			@PathParam("accountid") String accountId,
			@PathParam("appId") String appId,
			@PathParam("trxContent") String trxContent) {
		System.out.println("Device Id: " + deviceId + "\nAccount Id: "
				+ accountId + "\nApp Id: " + appId + "\nTransaction content: "
				+ trxContent);
		AuthenticationRequest[] authReqObj = getAuthReqResponse(deviceId,
				accountId);
		setAppId(appId, authReqObj[0].header);
		setTransaction(trxContent, authReqObj);

		return gson.toJson(authReqObj);
	}

	/****************************************** MULTIPLE ASSERTIONS PROCESSING *************************************/

	@GET
	@Path("/public/authRequest/{appId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAuthForAppIdReq(@PathParam("appId") String appId) {
		AuthenticationRequest[] authReqObj = getAuthReqObj();
		setAppId(appId, authReqObj[0].header);

		return gson.toJson(authReqObj);
	}

	private void setAppId(String appId, OperationHeader header) {
		if (appId == null || appId.isEmpty()) {
			return;
		}
		String decodedAppId = new String(Base64.decodeBase64(appId));
		Facets facets = facets();
		if (facets == null || facets.trustedFacets == null
				|| facets.trustedFacets.length == 0
				|| facets.trustedFacets[0] == null
				|| facets.trustedFacets[0].ids == null) {
			return;
		}
		String[] ids = facets.trustedFacets[0].ids;
		for (int i = 0; i < ids.length; i++) {

			if (decodedAppId.equals(ids[i])) {
				header.appID = decodedAppId;
				break;
			}
		}
	}

	@GET
	@Path("/public/authRequest/{appId}/{trxContent}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAuthTrxReq(@PathParam("appId") String appId,
			@PathParam("trxContent") String trxContent) {
		AuthenticationRequest[] authReqObj = getAuthReqObj();
		setAppId(appId, authReqObj[0].header);
		setTransaction(trxContent, authReqObj);

		return gson.toJson(authReqObj);
	}

	private void setTransaction(String trxContent,
			AuthenticationRequest[] authReqObj) {
		authReqObj[0].transaction = new Transaction[1];
		Transaction t = new Transaction();
		t.content = trxContent;
		t.contentType = MediaType.TEXT_PLAIN;
		authReqObj[0].transaction[0] = t;
	}

	public AuthenticationRequest[] getAuthReqObj() {
		AuthenticationRequest[] ret = new AuthenticationRequest[1];
		ret[0] = new FetchRequest(getAppId(), getAllowedAaids())
				.getAuthenticationRequest();
		Dash.getInstance().stats.put(Dash.LAST_AUTH_REQ, ret);
		Dash.getInstance().history.add(ret);
		return ret;
	}

	// New code added here
	/**
	 * Method to get authrequest object with policy having only those AAIDs user
	 * registered at the time of registration.
	 * 
	 * @return
	 */
	public AuthenticationRequest[] getAuthReqResponse(String deviceId,
			String accountId) {
		AuthenticationRequest[] ret = new AuthenticationRequest[1];
		ret[0] = new FetchRequest(getAppId(), getAllowedAaids(deviceId,
				accountId)).getAuthenticationRequest();
		Dash.getInstance().stats.put(Dash.LAST_AUTH_REQ, ret);
		Dash.getInstance().history.add(ret);
		return ret;
	}

	// New code added here
	/**
	 * Method to get authrequest object with policy having only those AAIDs that
	 * are enforced on userbased on his context passed during login (Flexible
	 * Authentication).
	 * 
	 * @return
	 */
	public AuthenticationRequest[] getAuthReqResponse(String accountId) {
		AuthenticationRequest[] ret = new AuthenticationRequest[1];
		ret[0] = new FetchRequest(getAppId(), getAllowedAaids(accountId))
				.getAuthenticationRequest();
		Dash.getInstance().stats.put(Dash.LAST_AUTH_REQ, ret);
		Dash.getInstance().history.add(ret);
		return ret;
	}

	@POST
	@Path("/public/authResponse")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AuthenticatorRecord[] processAuthResponse(String payload) {
		System.out.println("\n\nPayload from POST request: " + payload);
		Dash.getInstance().stats.put(Dash.LAST_AUTH_RES, payload);
		Gson gson = new Gson();
		AuthenticationResponse[] authResp = gson.fromJson(payload,
				AuthenticationResponse[].class);
		Dash.getInstance().stats.put(Dash.LAST_AUTH_RES, authResp);
		Dash.getInstance().history.add(authResp);
		AuthenticatorRecord[] result = new ProcessResponse()
				.processAuthResponse(authResp[0]);
		Dash.getInstance().stats.put(Dash.LAST_AUTH_RES_REPLY, result);

		// Get the status field from Auth Response
		String status = result[0].getStatus();

		// Based on status value, set the authstats flag in DB
		if (status.equalsIgnoreCase("SUCCESS")) {
			// update authstats filed in DB to true
			StorageImpl.getInstance()
					.updateAuthStatusInDB(result[0].toString());
		}
		return result;
	}

	@POST
	@Path("/public/uafRegRequest")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ReturnUAFRegistrationRequest GetUAFRegistrationRequest(String payload) {
		RegistrationRequest[] result = getRegisReqPublic("iafuser01");
		ReturnUAFRegistrationRequest uafReq = null;
		if (result != null) {
			uafReq = new ReturnUAFRegistrationRequest();
			uafReq.statusCode = 1200;
			uafReq.uafRequest = result;
			uafReq.op = Operation.Reg;
			uafReq.lifetimeMillis = 5 * 60 * 1000;
		}
		return uafReq;
	}

	@POST
	@Path("/public/uafAuthRequest")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ReturnUAFAuthenticationRequest GetUAFAuthenticationRequest(
			String payload) {
		AuthenticationRequest[] result = getAuthReqObj();
		ReturnUAFAuthenticationRequest uafReq = null;
		if (result != null) {
			uafReq = new ReturnUAFAuthenticationRequest();
			uafReq.statusCode = 1200;
			uafReq.uafRequest = result;
			uafReq.op = Operation.Auth;
			uafReq.lifetimeMillis = 5 * 60 * 1000;
		}
		return uafReq;
	}

	@POST
	@Path("/public/uafDeregRequest")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ReturnUAFDeregistrationRequest GetUAFDeregistrationRequest(
			String payload) {
		String result = deregRequestPublic(payload);
		ReturnUAFDeregistrationRequest uafReq = new ReturnUAFDeregistrationRequest();
		if (result.equalsIgnoreCase("Success")) {
			uafReq.statusCode = 1200;
		} else if (result
				.equalsIgnoreCase("Failure: Problem in deleting record from local DB")) {
			uafReq.statusCode = 1404;
		} else if (result
				.equalsIgnoreCase("Failure: problem processing deregistration request")) {
			uafReq.statusCode = 1491;
		} else {
			uafReq.statusCode = 1500;

		}
		uafReq.uafRequest = null;
		uafReq.op = Operation.Dereg;
		uafReq.lifetimeMillis = 0;
		return uafReq;
	}

	@POST
	@Path("/public/uafAuthResponse")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ServerResponse UAFAuthResponse(String payload) {
		String findOp = payload;
		findOp = findOp.substring(findOp.indexOf("op") + 6,
				findOp.indexOf(",", findOp.indexOf("op")) - 1);
		System.out.println("findOp=" + findOp);

		AuthenticatorRecord[] result = processAuthResponse(payload);
		ServerResponse servResp = new ServerResponse();
		if (result[0].status.equals("SUCCESS")) {
			servResp.statusCode = 1200;
			servResp.Description = "OK. Operation completed";
		} else if (result[0].status.equals("FAILED_SIGNATURE_NOT_VALID")
				|| result[0].status.equals("FAILED_SIGNATURE_VERIFICATION")
				|| result[0].status.equals("FAILED_ASSERTION_VERIFICATION")) {
			servResp.statusCode = 1496;
			servResp.Description = result[0].status;
		} else if (result[0].status.equals("INVALID_SERVER_DATA_EXPIRED")
				|| result[0].status
						.equals("INVALID_SERVER_DATA_SIGNATURE_NO_MATCH")
				|| result[0].status.equals("INVALID_SERVER_DATA_CHECK_FAILED")) {
			servResp.statusCode = 1491;
			servResp.Description = result[0].status;
		} else {
			servResp.statusCode = 1500;
			servResp.Description = result[0].status;
		}

		return servResp;
	}

	@POST
	@Path("/public/uafRegResponse")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ServerResponse UAFRegResponse(String payload) throws IOException,
			JSONException {
		String findOp = payload;
		findOp = findOp.substring(findOp.indexOf("op") + 6,
				findOp.indexOf(",", findOp.indexOf("op")) - 1);
		System.out.println("findOp=" + findOp);

		RegistrationRecord[] result = processRegResponse(payload);
		ServerResponse servResp = new ServerResponse();
		if (result[0].status.equals("SUCCESS")) {
			servResp.statusCode = 1200;
			servResp.Description = "OK. Operation completed";
		} else if (result[0].status.equals("ASSERTIONS_CHECK_FAILED")) {
			servResp.statusCode = 1496;
			servResp.Description = result[0].status;
		} else if (result[0].status.equals("INVALID_SERVER_DATA_EXPIRED")
				|| result[0].status
						.equals("INVALID_SERVER_DATA_SIGNATURE_NO_MATCH")
				|| result[0].status.equals("INVALID_SERVER_DATA_CHECK_FAILED")) {
			servResp.statusCode = 1491;
			servResp.Description = result[0].status;
		} else {
			servResp.statusCode = 1500;
			servResp.Description = result[0].status;
		}

		return servResp;
	}

	@POST
	@Path("/public/uafRequest")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String GetUAFRequest(String payload) {
		String uafReq = null;
		Gson gson = new Gson();
		GetUAFRequest req = gson.fromJson(payload, GetUAFRequest.class);

		if (req.op.name().equals("Reg")) {
			RegistrationRequest[] result = getRegisReqPublic("iafuser01");
			ReturnUAFRegistrationRequest uafRegReq = null;
			if (result != null) {
				uafRegReq = new ReturnUAFRegistrationRequest();
				uafRegReq.statusCode = 1200;
				uafRegReq.uafRequest = result;
				uafRegReq.op = Operation.Reg;
				uafRegReq.lifetimeMillis = 5 * 60 * 1000;
			}
			uafReq = gson.toJson(uafRegReq);
		} else if (req.op.name().equals("Auth")) {
			AuthenticationRequest[] result = getAuthReqObj();
			ReturnUAFAuthenticationRequest uafAuthReq = null;
			if (result != null) {
				uafAuthReq = new ReturnUAFAuthenticationRequest();
				uafAuthReq.statusCode = 1200;
				uafAuthReq.uafRequest = result;
				uafAuthReq.op = Operation.Auth;
				uafAuthReq.lifetimeMillis = 5 * 60 * 1000;
			}
			uafReq = gson.toJson(uafAuthReq);
		} else if (req.op.name().equals("Dereg")) {
			String result = deregRequestPublic(payload);
			ReturnUAFDeregistrationRequest uafDeregReq = new ReturnUAFDeregistrationRequest();
			if (result.equalsIgnoreCase("Success")) {
				uafDeregReq.statusCode = 1200;
			} else if (result
					.equalsIgnoreCase("Failure: Problem in deleting record from local DB")) {
				uafDeregReq.statusCode = 1404;
			} else if (result
					.equalsIgnoreCase("Failure: problem processing deregistration request")) {
				uafDeregReq.statusCode = 1491;
			} else {
				uafDeregReq.statusCode = 1500;

			}
			uafDeregReq.uafRequest = null;
			uafDeregReq.op = Operation.Dereg;
			uafDeregReq.lifetimeMillis = 0;
			uafReq = gson.toJson(uafDeregReq);
		}
		return uafReq;
	}

	@POST
	@Path("/public/uafResponse")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ServerResponse UAFResponse(String payload) throws IOException,
			JSONException {
		String findOp = payload;
		findOp = findOp.substring(findOp.indexOf("op") + 6,
				findOp.indexOf(",", findOp.indexOf("op")) - 1);
		System.out.println("findOp=" + findOp);

		ServerResponse servResp = new ServerResponse();

		if (findOp.equals("Reg")) {
			RegistrationRecord[] result = processRegResponse(payload);

			if (result[0].status.equals("SUCCESS")) {
				servResp.statusCode = 1200;
				servResp.Description = "OK. Operation completed";
			} else if (result[0].status.equals("ASSERTIONS_CHECK_FAILED")) {
				servResp.statusCode = 1496;
				servResp.Description = result[0].status;
			} else if (result[0].status.equals("INVALID_SERVER_DATA_EXPIRED")
					|| result[0].status
							.equals("INVALID_SERVER_DATA_SIGNATURE_NO_MATCH")
					|| result[0].status
							.equals("INVALID_SERVER_DATA_CHECK_FAILED")) {
				servResp.statusCode = 1491;
				servResp.Description = result[0].status;
			} else {
				servResp.statusCode = 1500;
				servResp.Description = result[0].status;
			}
		} else if (findOp.equals("Auth")) {
			AuthenticatorRecord[] result = processAuthResponse(payload);

			if (result[0].status.equals("SUCCESS")) {
				servResp.statusCode = 1200;
				servResp.Description = "OK. Operation completed";
			} else if (result[0].status.equals("FAILED_SIGNATURE_NOT_VALID")
					|| result[0].status.equals("FAILED_SIGNATURE_VERIFICATION")
					|| result[0].status.equals("FAILED_ASSERTION_VERIFICATION")) {
				servResp.statusCode = 1496;
				servResp.Description = result[0].status;
			} else if (result[0].status.equals("INVALID_SERVER_DATA_EXPIRED")
					|| result[0].status
							.equals("INVALID_SERVER_DATA_SIGNATURE_NO_MATCH")
					|| result[0].status
							.equals("INVALID_SERVER_DATA_CHECK_FAILED")) {
				servResp.statusCode = 1491;
				servResp.Description = result[0].status;
			} else {
				servResp.statusCode = 1500;
				servResp.Description = result[0].status;
			}
		}
		return servResp;
	}

	/********************** INTER APP FIDO PROCESSES APIs ****************************/
	// New code added here
	@POST
	@Path("/public/notifyAppAuthentication")
	@Consumes({ "text/plain", "application/xml", "application/json" })
	@Produces({ "text/plain", "application/xml", "application/json" })
	public String notifyAppAuthentication(String payload) {
		System.out
				.println("\n\nPayload received for Notifying inter app authentication: "
						+ payload);
		Gson gson = new Gson();
		String response = null;
		Vendor fromJson = gson.fromJson(payload, Vendor.class);

		String accountId = fromJson.getAccountId();
		String vendorname = fromJson.getRpDisplayName();

		String userDisplayName = StorageImpl.getInstance()
				.getUserDisplaynameFromVendorName(vendorname, accountId);

		// Send message (same as you would get on push notification while
		// authenticating through RP desktop website) as response.

		response = "Provide approval for login attempt by user: "
				+ userDisplayName + " into: " + vendorname + " app.";
		return response;
	}

	/********************** INTER APP FIDO PROCESSES APIs ****************************/

	/********************** NEW RP WEBSITE API TO POLL FOR AUTH RESPONSE STATUS ****************************/
	// New code added here
	@POST
	@Path("/public/getAuthResponseStatus")
	@Consumes({ "text/plain", "application/xml", "application/json" })
	@Produces({ "text/plain" })
	public String authResponseStatus(String payload) {
		System.out
				.println("\n\nPayload received to check the AUTH flags in DB: "
						+ payload);
		Gson gson = new Gson();
		String response = null;
		Device fromJson = gson.fromJson(payload, Device.class);

		String accountId = fromJson.getRpaccountid();

		// Get auth related flags from DB based on accountid
		Map<String, Boolean> authFlags = new HashMap<String, Boolean>();
		authFlags = StorageImpl.getInstance().getAuthFlagsforAccountId(
				accountId);

		// check if "auth_in_progress" field is set to true
		if (authFlags.get("auth_in_progress").equals(false)) {
			response = "FAILURE";
		} else {
			if (authFlags.get("authstats").equals(false)) {
				response = "FAILURE";
			} else {
				response = "SUCCESS";
				// on success, reset the value of auth_in_progress and
				// authstats back to false
				StorageImpl.getInstance().resetAuthFlagsInRegDB(accountId);
			}
		}
		return response;
	}

	/********************** NEW RP WEBSITE API TO POLL FOR AUTH RESPONSE STATUS ****************************/
}
