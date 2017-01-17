package org.ebayopensource.fidouaf.res.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class ProcessLogin {

	/**
	 * Method to check whether the entered credential is valid
	 * 
	 * @param uname
	 * @param pwd
	 * @return
	 */
	public boolean checkCredentials(String uname, String pwd) {
		boolean result = false;
		if (isNotNull(uname) && isNotNull(pwd)) {
			try {
				result = StorageImpl.getInstance().checkLogin(uname, pwd);
			} catch (Exception e) {
				result = false;
			}
		} else {
			result = false;
		}

		return result;
	}

	/**
	 * Method to check if user is pre-registered.
	 * 
	 * @param uname
	 * @return
	 */
	public boolean checkIfRegistered(String uname) {
		boolean result = false;
		if (isNotNull(uname)) {
			try {
				result = StorageImpl.getInstance().checkUserRegDB(uname);
			} catch (Exception e) {
				result = false;
			}
		} else {
			result = false;
		}

		return result;
	}

	/**
	 * Null check Method
	 * 
	 * @param txt
	 * @return
	 */
	public static boolean isNotNull(String txt) {
		return txt != null && txt.trim().length() >= 0 ? true : false;
	}

	/**
	 * Method to construct JSON
	 * 
	 * @param tag
	 * @param status
	 * @return
	 */
	public String constructJSON(boolean loginStats, boolean regStats) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("Login Status", new Boolean(loginStats));
			obj.put("Registration Status", new Boolean(regStats));
		} catch (JSONException e) {
		}
		return obj.toString();
	}

	/**
	 * Method to construct JSON
	 * 
	 * @param otp
	 * @return otp generated
	 */
	public String constructJSON(String otp) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("registrationResponse", otp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj.toString();
	}

	/**
	 * Method to construct JSON for Signup process.
	 * 
	 * @param otp
	 * @return otp generated
	 */
	public String constructJSONForSignUp(String msg) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("Message", msg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj.toString();
	}

	/**
	 * Method to construct JSON with Error Msg
	 * 
	 * @param tag
	 * @param status
	 * @param err_msg
	 * @return
	 */
	public String constructJSON(String tag, boolean status, String err_msg) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("tag", tag);
			obj.put("Login Status", new Boolean(status));
			obj.put("error_msg", err_msg);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}
		return obj.toString();
	}

	public String constructJSON(String message, boolean loginStatus) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("Message", message);
			obj.put("Login Status", new Boolean(loginStatus));
		} catch (JSONException e) {
		}
		return obj.toString();
	}

	/**
	 * Returns use PSL Fido requests' response.
	 * 
	 * @param message
	 * @param loginStatus
	 * @return response
	 */
	public String constructJSONForUsePSLFIDORequest(String vendor,
			boolean vendorRegStatus) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("Vendor Name", vendor);
			obj.put("Registration Status", new Boolean(vendorRegStatus));
		} catch (JSONException e) {
		}
		return obj.toString();
	}

	/**
	 * Returns extra details about the vendor.
	 * 
	 * @param vendorRegStats
	 * 
	 * @param message
	 * @param loginStatus
	 * @return response
	 */
	public String constructJSONForPushNotifyReply(
			Map<String, String> responseData) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("rpDisplayName", responseData.get("Vendor"));
			obj.put("displayName", responseData.get("User"));
			//obj.put("phoneNumber", responseData.get("Phone"));
			obj.put("email", responseData.get("Email"));
			obj.put("accountID", responseData.get("AccountId"));
		} catch (JSONException e) {
		}
		return obj.toString();
	}
	
	/**
	 * Returns extra details about the transaction.
	 * 
	 * @param vendorRegStats
	 * 
	 * @param message
	 * @param loginStatus
	 * @return response
	 */
	public String constructJSONForMoreTransactionDetails(
			Map<String, String> responseData) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("appId", responseData.get("appid"));
			obj.put("content", responseData.get("contentString"));
		} catch (JSONException e) {
		}
		return obj.toString();
	}

	/**
	 * Method that generates OTP number and sends sms to phone
	 * 
	 * @return generated OTP string
	 */
	public Map<String, String> generateOTP(String phonenumber) {
		Map<String, String> otpDetails = new HashMap<String, String>();

		/*
		 * int size = 6; StringBuilder generatedToken = new StringBuilder(); try
		 * { SecureRandom number = SecureRandom.getInstance("SHA1PRNG"); //
		 * Generate 20 integers 0..20 for (int i = 0; i < size; i++) {
		 * generatedToken.append(number.nextInt(9)); } } catch
		 * (NoSuchAlgorithmException e) { e.printStackTrace(); }
		 */

		// MSG91 code

		String baseUrl = "https://sendotp.msg91.com/api"; // Your application
															// key
		String applicationKey = "_4OdBezKFip3ej121YnoKedVyRANQLPhyGdQpvS984--bXTd0gDhnOBDPFjOvFS8oc7W3FrjS_f4p_HCpYywCJFzNLvqEIUSU-bbDntprNBfQPwSHp4HqzlabTtSiYydev1M_L25G1rISPs2mHEM9A==";

		// Country Code
		String countryCode = "91";

		// Mobile Number
		String mobileNumber = phonenumber;

		String otp = null, output = null;

		// send OTP message on mobile number
		try {
			Client client = Client.create();
			String Url = baseUrl + "/generateOTP";
			WebResource webResource = client.resource(Url);

			HashMap<String, String> requestBodyMap = new HashMap<String, String>();
			requestBodyMap.put("countryCode", countryCode);
			requestBodyMap.put("mobileNumber", mobileNumber);
			requestBodyMap.put("getGeneratedOTP", "true");
			JSONObject requestBodyJsonObject = new JSONObject(requestBodyMap);
			String input = requestBodyJsonObject.toString();

			ClientResponse response = webResource
					.type(MediaType.APPLICATION_JSON)
					.header("application-Key", applicationKey)
					.post(ClientResponse.class, input);

			output = response.getEntity(String.class);

			// convert string to JSON object and extract one time password from
			// it to store in DB.
			JSONObject jsonObject = new JSONObject(output.toString());
			JSONObject json1 = new JSONObject(jsonObject.get("response")
					.toString());
			otp = json1.get("oneTimePassword").toString();
			System.out.println("\n One Time Password: " + otp);

		} catch (Exception e) {
			e.printStackTrace();
		}

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String creationDate = dateFormat.format(date);
		otpDetails.put("Generated OTP", otp);
		// otpDetails.put("Generated OTP", generatedToken.toString());
		otpDetails.put("Date", creationDate);
		return otpDetails;
	}

	/**
	 * Method that generates OTP number. No SMS is sent
	 * 
	 * @return generated OTP string
	 */
	public Map<String, String> generateOTPwithoutSMS() {
		Map<String, String> otpDetails = new HashMap<String, String>();

		int size = 6;

		StringBuilder generatedToken = new StringBuilder();
		try {
			SecureRandom number = SecureRandom.getInstance("SHA1PRNG");
			// Generate 20 integers 0..20
			for (int i = 0; i < size; i++) {
				generatedToken.append(number.nextInt(9));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String creationDate = dateFormat.format(date);
		otpDetails.put("Generated OTP", generatedToken.toString());
		otpDetails.put("Date", creationDate);
		return otpDetails;
	}

	/**
	 * Method to send SMS to mobile device.
	 */
	public void sendSMS(String phoneNumber, String OTP) {
		// Twilio code
		// Find your Account Sid and Token at twilio.com/user/account
		/*
		 * String ACCOUNT_SID = "AC7a6d1b6780a14ea1ece4eb89362c57be"; String
		 * AUTH_TOKEN = "6dc2052ac37fe614f53aa8dd4800b91f"; String fromNumber =
		 * "+15005550006"; try { TwilioRestClient client = new
		 * TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN); Account account =
		 * client.getAccount();
		 * 
		 * SmsFactory messageFactory = account.getSmsFactory(); Map<String,
		 * String> params = new HashMap <String, String>();
		 * //ArrayList<NameValuePair>(); params.put("To", phoneNumber);
		 * //.add(new BasicNameValuePair("To", "+14159352345")); // Replace with
		 * a valid phone number for your account. params.put("From",fromNumber);
		 * //.add(new BasicNameValuePair("From", "+14158141829")); // Replace
		 * with a valid phone number for your account. params.put("Body", OTP);
		 * //.add(new BasicNameValuePair("Body", "Where's Wallace?"));
		 * 
		 * Sms sms = messageFactory.create(params);
		 * System.out.println("\n SMS sent is: " +sms); } catch
		 * (TwilioRestException e) { e.printStackTrace(); }
		 */

		// MSG91 code
		// Your authentication key
		String authkey = "111293AzNu5bkf571f29b5";
		// Multiple mobiles numbers separated by comma
		String mobiles = phoneNumber;
		// Sender ID,While using route4 sender id should be 6 characters long.
		String senderId = "MSGIND";
		// Your message to send, Add URL encoding here.
		String message = OTP;
		// define route
		String route = "default";

		// Prepare Url
		URLConnection myURLConnection = null;
		URL myURL = null;
		BufferedReader reader = null;

		// encoding message
		String encoded_message = null;
		try {
			encoded_message = URLEncoder.encode(message, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		// Send SMS API
		String mainUrl = "http://api.msg91.com/api/sendhttp.php?";

		// Prepare parameter string
		StringBuilder sbPostData = new StringBuilder(mainUrl);
		sbPostData.append("authkey=" + authkey);
		sbPostData.append("&mobiles=" + mobiles);
		sbPostData.append("&message=" + encoded_message);
		sbPostData.append("&route=" + route);
		sbPostData.append("&sender=" + senderId);

		// final string
		mainUrl = sbPostData.toString();
		try {
			// prepare connection
			myURL = new URL(mainUrl);
			myURLConnection = myURL.openConnection();
			myURLConnection.connect();
			reader = new BufferedReader(new InputStreamReader(
					myURLConnection.getInputStream()));
			// reading response
			String response;
			while ((response = reader.readLine()) != null)
				// print response
				System.out.println(response);

			// finally close connection
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Send Push notifications to IOS device.
	 */
	public void sendPushNotificationsToIOS(Map<String, String> pushData) {

		/************** Old APNS Implementation **********************/
		/*
		 * String certificatePwd = "iosFido2016"; //String token5 =
		 * "b4cf1d8081f97300fc07c26feb180e0fcfb9f24ed3263c44156c8132f36a5ba7";
		 * //String token6 =
		 * "1651fdc19bf8823fa28aa349e31d35a1aba1649d0bbc6f1d9ad768023de3aa96";
		 * 
		 * // Setup the connection. ApnsService service = null;
		 * ApnsServiceBuilder serviceBuilder = APNS .newService() .withCert(
		 * "C:/APNS Certificate/PSLfidoAuthorizeUAFPushNotification.p12",
		 * certificatePwd).withSandboxDestination(); service =
		 * serviceBuilder.build();
		 * 
		 * // Create and send the message as per what operation is performed. if
		 * (pushData.get("operation").equals("Use PSL Fido")) { PayloadBuilder
		 * payloadBuilder = APNS.newPayload(); payloadBuilder
		 * .alertBody("PSL FIDO server requesting you to Authenticate: " +
		 * pushData.get("Vendor") + "!!!"); // payloadBuilder.badge(45);
		 * payloadBuilder.sound("default"); String payload =
		 * payloadBuilder.build();
		 * 
		 * service.push(pushData.get("Token"), payload);
		 * 
		 * System.out
		 * .println("\nPush Notification sent to Iphone Device successfully..."
		 * ); }
		 * 
		 * if (pushData.get("operation").equals("Register PSL FIDO")) {
		 * PayloadBuilder payloadBuilder = APNS.newPayload();
		 * payloadBuilder.alertBody("Associate: " + pushData.get("Vendor") +
		 * " account with FIDO"); // payloadBuilder.badge(45);
		 * payloadBuilder.sound("default"); String payload =
		 * payloadBuilder.build();
		 * 
		 * service.push(pushData.get("Token"), payload);
		 * 
		 * System.out
		 * .println("\nPush Notification sent to Iphone Device successfully..."
		 * );
		 * 
		 * }
		 * 
		 * if (pushData.get("operation").equals("FIDO Autheticate")) {
		 * PayloadBuilder payloadBuilder = APNS.newPayload();
		 * payloadBuilder.alertBody("Provide approval for logging into: " +
		 * pushData.get("Vendorname") + " website."); // //
		 * payloadBuilder.badge(45); payloadBuilder.sound("default"); String
		 * payload = payloadBuilder.build();
		 * 
		 * service.push(pushData.get("Token"), payload);
		 * 
		 * System.out
		 * .println("\nPush Notification sent to Iphone Device successfully..."
		 * );
		 * 
		 * }
		 * 
		 * // To query the feedback service for inactive devices: Map<String,
		 * Date> inactiveDevices = service.getInactiveDevices(); for (String
		 * deviceToken : inactiveDevices.keySet()) { Date inactiveAsOf =
		 * inactiveDevices.get(deviceToken);
		 * System.out.println("\nInActive Device as of: " + inactiveAsOf); }
		 */

		/************** Old APNS Implementation **********************/

		
		/************** FIREBASE Implementation **********************/

		final String API_KEY = "AIzaSyAKxN6RAkSXjvpLK2j8zASsKo6Yub_wSH8";
		final String url = "https://fcm.googleapis.com/fcm/send";

		if (pushData.get("operation").equals("FIDO Autheticate")) {		
			final String REGISTRATION_ID = pushData.get("Token");
			
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			JSONObject mainData = new JSONObject();
			try {
				JSONObject data = new JSONObject();
				data.putOpt("title", "Approval");
				data.putOpt("text", "Provide approval for login attempt by user: " +pushData.get("displayname")+ " into " +pushData.get("Vendorname")+ " website");
				//JSONArray regIds = new JSONArray();
				//regIds.put(REGISTRATION_ID);
				mainData.put("notification", data);
				//mainData.put("to", regIds);
				mainData.put("to", REGISTRATION_ID);
				System.out.println("\nJson data = " + mainData.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}

			StringEntity se;
			try {
				se = new StringEntity(mainData.toString());
				post.setEntity(se);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			post.addHeader("Authorization", "key=" + API_KEY);
			post.addHeader("Content-Type", "application/json");

			System.out.println("POST Headers: ");
			Header[] headers = post.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				System.out.println(headers[i].toString());
			}
			HttpResponse response;
			try {
				response = client.execute(post);
				System.out.println("\nresponse code ="
						+ Integer.toString(response.getStatusLine()
								.getStatusCode()));
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));
				StringBuffer result = new StringBuffer();
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
				System.out.println("\nresponse is: " + result.toString());
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (pushData.get("operation").equals("FIDO Transaction")) {		
			final String REGISTRATION_ID = pushData.get("Token");
			
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			JSONObject mainData = new JSONObject();
			try {
				JSONObject data = new JSONObject();
				data.putOpt("title", "Approval");
				data.putOpt("text", "Approve Transaction of Rs. " +pushData.get("amount")+ " from account with username: " +pushData.get("username")+ " to: " +pushData.get("to"));
				//JSONArray regIds = new JSONArray();
				//regIds.put(REGISTRATION_ID);
				mainData.put("notification", data);
				//mainData.put("to", regIds);
				mainData.put("to", REGISTRATION_ID);
				System.out.println("\nJson data = " + mainData.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}

			StringEntity se;
			try {
				se = new StringEntity(mainData.toString());
				post.setEntity(se);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			post.addHeader("Authorization", "key=" + API_KEY);
			post.addHeader("Content-Type", "application/json");

			System.out.println("POST Headers: ");
			Header[] headers = post.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				System.out.println(headers[i].toString());
			}
			HttpResponse response;
			try {
				response = client.execute(post);
				System.out.println("\nresponse code ="
						+ Integer.toString(response.getStatusLine()
								.getStatusCode()));
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));
				StringBuffer result = new StringBuffer();
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
				System.out.println("\nresponse is: " + result.toString());
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (pushData.get("operation").equals("Register PSL FIDO")) {
			final String REGISTRATION_ID = pushData.get("Token");
			
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			JSONObject mainData = new JSONObject();
			try {
				JSONObject data = new JSONObject();
				data.putOpt("title", "Approval");
				data.putOpt("text", "Associate " +pushData.get("Vendor")+ " website account with PSL FIDO Services.");
				//JSONArray regIds = new JSONArray();
				//regIds.put(REGISTRATION_ID);
				mainData.put("notification", data);
				//mainData.put("to", regIds);
				mainData.put("to", REGISTRATION_ID);
				System.out.println("\nJson data = " + mainData.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}

			StringEntity se;
			try {
				se = new StringEntity(mainData.toString());
				post.setEntity(se);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			post.addHeader("Authorization", "key=" + API_KEY);
			post.addHeader("Content-Type", "application/json");

			System.out.println("POST Headers: ");
			Header[] headers = post.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				System.out.println(headers[i].toString());
			}
			HttpResponse response;
			try {
				response = client.execute(post);
				System.out.println("\nresponse code ="
						+ Integer.toString(response.getStatusLine()
								.getStatusCode()));
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));
				StringBuffer result = new StringBuffer();
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
				System.out.println("\nresponse is: " + result.toString());
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (pushData.get("operation").equals("Use PSL Fido")) {
			final String REGISTRATION_ID = pushData.get("Token");
			
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			JSONObject mainData = new JSONObject();
			try {
				JSONObject data = new JSONObject();
				data.putOpt("title", "Approval");
				data.putOpt("text",
						"PSL FIDO service user " +pushData.get("displayname")+ " requesting you to Authenticate "
								+ pushData.get("Vendor") + " website!!!");
				//JSONArray regIds = new JSONArray();
				//regIds.put(REGISTRATION_ID);
				mainData.put("notification", data);
				//mainData.put("to", regIds);
				mainData.put("to", REGISTRATION_ID);
				System.out.println("\nJson data = " + mainData.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}

			StringEntity se;
			try {
				se = new StringEntity(mainData.toString());
				post.setEntity(se);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			post.addHeader("Authorization", "key=" + API_KEY);
			post.addHeader("Content-Type", "application/json");

			System.out.println("POST Headers: ");
			Header[] headers = post.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				System.out.println(headers[i].toString());
			}
			HttpResponse response;
			try {
				response = client.execute(post);
				System.out.println("\nresponse code ="
						+ Integer.toString(response.getStatusLine()
								.getStatusCode()));
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));
				StringBuffer result = new StringBuffer();
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
				System.out.println("\nresponse is: " + result.toString());
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		/************** FIREBASE Implementation **********************/
	}

	/**
	 * Constructs response JSON for grant push notifications API.
	 * 
	 * @param uname
	 * @param deviceTokenUpdated
	 * @return
	 */
	public String constructJSONGrantPushServices(String uname,
			boolean deviceTokenUpdated) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("username", uname);
			obj.put("deviceTokenUpdateStatus", new Boolean(deviceTokenUpdated));
		} catch (JSONException e) {
		}
		return obj.toString();
	}

	/**************************************** Phase 2 - No login, OTP services ***************************************/

	public String constructJSONForPushNotifiedDevice(boolean deviceDataUpdated) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("deviceDataUpdateStatus", new Boolean(deviceDataUpdated));
		} catch (JSONException e) {
		}
		return obj.toString();
	}
	
	public String constructJSONForQRVerification(boolean isQRVerified) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("QRCodeVerification", new Boolean(isQRVerified));
		} catch (JSONException e) {
		}
		return obj.toString();
	}

	/**
	 * Method that send an output JSON with signed context for getSignedContext API
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

	/*****************************************************************************************************************/
}
