package org.psl.fidouaf.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.psl.fidouaf.core.constants.Constants;
import org.psl.fidouaf.entity.PushOperation;
import org.psl.fidouaf.entity.SendPushNotification;
import org.psl.fidouaf.service.PushNotificationService;
import org.springframework.stereotype.Service;

@Service
public class PushNotificationServiceImpl implements PushNotificationService {

	/**
	 * Method that send push notifications to Mobile devices.
	 */
	public void sendPushNotifications(SendPushNotification pushData) {

		/************** FIREBASE Implementation **********************/
		if (pushData.getOperation().equals(PushOperation.FIDO_Autheticate)) {
			final String REGISTRATION_ID = pushData.getToken();

			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(Constants.URL);
			JSONObject mainData = new JSONObject();
			try {
				JSONObject data = new JSONObject();
				data.putOpt("title", "Approval");
				data.putOpt("text", pushData.getContent());
				mainData.put("notification", data);
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

			post.addHeader("Authorization", "key=" + Constants.API_KEY);
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

		if (pushData.getOperation().equals(PushOperation.FIDO_Transaction)) {
			final String REGISTRATION_ID = pushData.getToken();

			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(Constants.URL);
			JSONObject mainData = new JSONObject();
			try {
				JSONObject data = new JSONObject();
				data.putOpt("title", "Approval");
				data.putOpt("text", pushData.getContent());
				mainData.put("notification", data);
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

			post.addHeader("Authorization", "key=" + Constants.API_KEY);
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

		/*
		 * if (pushData.getOperation().equals(PushOperation.FIDO_Registration))
		 * { final String REGISTRATION_ID = pushData.getToken();
		 * 
		 * HttpClient client = new DefaultHttpClient(); HttpPost post = new
		 * HttpPost(url); JSONObject mainData = new JSONObject(); try {
		 * JSONObject data = new JSONObject(); data.putOpt("title", "Approval");
		 * data.putOpt("text", "Associate " +pushData.get("Vendor")+
		 * " website account with PSL FIDO Services.");
		 * mainData.put("notification", data); mainData.put("to",
		 * REGISTRATION_ID); System.out.println("\nJson data = " +
		 * mainData.toString()); } catch (JSONException e) {
		 * e.printStackTrace(); }
		 * 
		 * StringEntity se; try { se = new StringEntity(mainData.toString());
		 * post.setEntity(se); } catch (UnsupportedEncodingException e) {
		 * e.printStackTrace(); }
		 * 
		 * post.addHeader("Authorization", "key=" + API_KEY);
		 * post.addHeader("Content-Type", "application/json");
		 * 
		 * System.out.println("POST Headers: "); Header[] headers =
		 * post.getAllHeaders(); for (int i = 0; i < headers.length; i++) {
		 * System.out.println(headers[i].toString()); } HttpResponse response;
		 * try { response = client.execute(post);
		 * System.out.println("\nresponse code =" +
		 * Integer.toString(response.getStatusLine() .getStatusCode()));
		 * BufferedReader rd = new BufferedReader(new InputStreamReader(
		 * response.getEntity().getContent())); StringBuffer result = new
		 * StringBuffer(); String line = ""; while ((line = rd.readLine()) !=
		 * null) { result.append(line); } System.out.println("\nresponse is: " +
		 * result.toString()); } catch (ClientProtocolException e) {
		 * e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
		 * }
		 */

		/*
		 * if (pushData.getOperation().equals(PushOperation.USE_FIDO)) { final
		 * String REGISTRATION_ID = pushData.getToken();
		 * 
		 * HttpClient client = new DefaultHttpClient(); HttpPost post = new
		 * HttpPost(url); JSONObject mainData = new JSONObject(); try {
		 * JSONObject data = new JSONObject(); data.putOpt("title", "Approval");
		 * data.putOpt("text", "PSL FIDO service user "
		 * +pushData.get("displayname")+ " requesting you to Authenticate " +
		 * pushData.get("Vendor") + " website!!!"); mainData.put("notification",
		 * data); mainData.put("to", REGISTRATION_ID);
		 * System.out.println("\nJson data = " + mainData.toString()); } catch
		 * (JSONException e) { e.printStackTrace(); }
		 * 
		 * StringEntity se; try { se = new StringEntity(mainData.toString());
		 * post.setEntity(se); } catch (UnsupportedEncodingException e) {
		 * e.printStackTrace(); }
		 * 
		 * post.addHeader("Authorization", "key=" + API_KEY);
		 * post.addHeader("Content-Type", "application/json");
		 * 
		 * System.out.println("POST Headers: "); Header[] headers =
		 * post.getAllHeaders(); for (int i = 0; i < headers.length; i++) {
		 * System.out.println(headers[i].toString()); } HttpResponse response;
		 * try { response = client.execute(post);
		 * System.out.println("\nresponse code =" +
		 * Integer.toString(response.getStatusLine() .getStatusCode()));
		 * BufferedReader rd = new BufferedReader(new InputStreamReader(
		 * response.getEntity().getContent())); StringBuffer result = new
		 * StringBuffer(); String line = ""; while ((line = rd.readLine()) !=
		 * null) { result.append(line); } System.out.println("\nresponse is: " +
		 * result.toString()); } catch (ClientProtocolException e) {
		 * e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
		 * }
		 */
		/************** FIREBASE Implementation **********************/
	}

}
