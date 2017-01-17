package org.psl.fidouaf.service.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.psl.fidouaf.controllers.FidoUafController;
import org.psl.fidouaf.core.constants.Constants;
import org.psl.fidouaf.core.entity.AuthenticationRequest;
import org.psl.fidouaf.core.entity.OperationHeader;
import org.psl.fidouaf.core.entity.Transaction;
import org.psl.fidouaf.dao.RegistrationDao;
import org.psl.fidouaf.dao.UtilServicesDao;
import org.psl.fidouaf.facets.Facets;
import org.psl.fidouaf.res.util.FetchRequest;
import org.psl.fidouaf.service.UtilService;
import org.psl.fidouaf.stats.Dash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UtilServiceImpl implements UtilService {

	@Autowired
	private UtilServicesDao utilServicesDao;

	@Autowired
	private RegistrationDao registrationDao;

	/**
	 * Method that gets Hard-coded APP IDs for different APPs.
	 */
	public String getAppId() {
		return Constants.APP_ID;
	}

	/**
	 * Method that gets default AAIDs supported in FIDO server. (Hard-coded
	 * AAIDs).
	 * 
	 * @return AAIDs string object
	 */
	public String[] getAllowedAaids() {
		String[] ret = { "EBA0#0001", "0015#0001", "0012#0002", "0010#0001",
				"4e4e#0001", "5143#0001", "0011#0701", "0013#0001",
				"0014#0000", "0014#0001", "53EC#C002", "DAB8#8001",
				"DAB8#0011", "DAB8#8011", "5143#0111", "5143#0120",
				"4746#F816", "53EC#3801", "TCH0#0001", "PIN0#0001", "FCE0#0001" };
		return ret;
	}

	/**
	 * Method that sets the APP ID in the RegRequest output.
	 * 
	 * @param appId
	 * @param header
	 */
	public void setAppId(String appId, OperationHeader header) {
		if (appId == null || appId.isEmpty()) {
			return;
		}
		//String decodedAppId = new String(Base64.decodeBase64(appId));
		Facets facets = FidoUafController.facets();
		if (facets != null && facets.getTrustedFacet() != null
				&& facets.getTrustedFacet().getIds() != null) {

			String[] ids = facets.getTrustedFacet().getIds();
			for (int i = 0; i < ids.length; i++) {

				if (/*decodedAppId*/appId.equals(ids[i])) {
					header.appID = appId; //decodedAppId;
					break;
				}
			}
		}
	}

	// NEW_CODE_ADDED_BY_JASMIRA
	/**
	 * Method that gets allowed AAIDs for given DeviceId and Account Id.
	 * 
	 * @param deviceId
	 * @param accountId
	 * @return AAIDs string object
	 */
	public String[] getAllowedAaids(String deviceId, int rpAccountId) {
		String[] ret = null;
		ret = utilServicesDao
				.getAAIDforDeviceIdAccountId(deviceId, rpAccountId);
		System.out.println("Allowed AAIDs for user are: "
				+ Arrays.toString(ret));
		return ret;
	}

	// NEW_CODE_ADDED_BY_JASMIRA
	/**
	 * Method that gets allowed AAIDs for given AccountId which are enforced at
	 * the time of login in based on user's context passed (Flexible
	 * authentication).
	 * 
	 * @param deviceId
	 * @return
	 */
	public String[] getAllowedAaids(int accountId) {
		String[] ret = null;
		ret = utilServicesDao.getAAIDsEnforcedForAccountId(accountId);
		System.out.println("Allowed AAIDs for user are: "
				+ Arrays.toString(ret));
		return ret;
	}

	// NEW_CODE_ADDED_BY_JASMIRA
	/**
	 * Method to get authrequest object with policy having only those AAIDs user
	 * registered at the time of registration.
	 * 
	 * @return
	 */
	public List<AuthenticationRequest> getAuthReqResponse(String deviceId,
			int accountId) {
		List<AuthenticationRequest> authRequest = new ArrayList<AuthenticationRequest>();
		authRequest.add(new FetchRequest(getAppId(), getAllowedAaids(deviceId,
				accountId)).getAuthenticationRequest());
		Dash.getInstance().stats.put(Constants.LAST_AUTH_REQ, authRequest);
		Dash.getInstance().history.add(authRequest);
		return authRequest;
	}

	/**
	 * Method that sets the transaction fields in the authentication Repsonse
	 * JSON.
	 * 
	 * @param trxContent
	 * @param authReqObj
	 */
	public void setTransaction(String trxContent,
			List<AuthenticationRequest> authReqObj) {
		authReqObj.get(0).transaction = new Transaction[1];
		Transaction t = new Transaction();
		t.content = trxContent;
		t.contentType = MediaType.TEXT_PLAIN;
		authReqObj.get(0).transaction[0] = t;
	}

	/**
	 * Method that gets default JSON response for authRequest API with
	 * hard-coded AAIDS.
	 * 
	 * @return
	 */
	public List<AuthenticationRequest> getAuthReqObj() {
		List<AuthenticationRequest> authRequest = new ArrayList<AuthenticationRequest>();
		authRequest.add(new FetchRequest(getAppId(), getAllowedAaids())
				.getAuthenticationRequest());
		Dash.getInstance().stats.put(Constants.LAST_AUTH_REQ, authRequest);
		Dash.getInstance().history.add(authRequest);
		return authRequest;
	}

	/**
	 * Method that converts any class Object to JSON Object
	 */
	public String toJSON(Object object) throws JSONException,
			IllegalAccessException {
		Class<? extends Object> c = object.getClass();
		JSONObject jsonObject = new JSONObject();
		for (Field field : c.getDeclaredFields()) {
			field.setAccessible(true);
			String name = field.getName();
			String value = String.valueOf(field.get(object));
			jsonObject.put(name, value);
		}
		System.out.println(jsonObject.toString());
		return jsonObject.toString();
	}

	/**
	 * Method that converts List of any class Objects to JSON Array Objects
	 */
	public String toJSON(List<Object> list) throws JSONException,
			IllegalAccessException {
		JSONArray jsonArray = new JSONArray();
		for (Object i : list) {
			String jstr = toJSON(i);
			JSONObject jsonObject = new JSONObject(jstr);
			jsonArray.put(jsonObject);
		}
		return jsonArray.toString();
	}

	/**
	 * Method that removes record from all tables in DB for given AAID_KEYID
	 * Key.
	 * 
	 * @param key
	 */
	public void removeAllRecordsForAAIDKEYID(String aaid_keyid) {
		utilServicesDao.removeAllRecordsForAAIDKEYID(aaid_keyid);
	}

	/**
	 * Method to get authrequest object with policy having only those AAIDs that
	 * are enforced on userbased on his context passed during login (Flexible
	 * Authentication).
	 * 
	 * @return
	 */
	public List<AuthenticationRequest> getAuthReqResponse(int accountId) {
		List<AuthenticationRequest> authRequest = new ArrayList<AuthenticationRequest>();
		authRequest
				.add(new FetchRequest(getAppId(), getAllowedAaids(accountId))
						.getAuthenticationRequest());
		Dash.getInstance().stats.put(Constants.LAST_AUTH_REQ, authRequest);
		Dash.getInstance().history.add(authRequest);
		return authRequest;
	}

}
