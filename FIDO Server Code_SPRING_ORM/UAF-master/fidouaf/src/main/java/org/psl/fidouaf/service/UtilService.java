package org.psl.fidouaf.service;

import java.util.List;

import org.json.JSONException;
import org.psl.fidouaf.core.entity.AuthenticationRequest;
import org.psl.fidouaf.core.entity.OperationHeader;
import org.springframework.stereotype.Service;

@Service
public interface UtilService {
	public String getAppId();

	public String[] getAllowedAaids();

	public void setAppId(String appId, OperationHeader header);

	public String[] getAllowedAaids(String deviceId, int rpAccountId);

	public List<AuthenticationRequest> getAuthReqResponse(String deviceId,
			int accountId);

	public void setTransaction(String trxContent,
			List<AuthenticationRequest> authReqObj);

	public List<AuthenticationRequest> getAuthReqObj();

	public String toJSON(Object object) throws JSONException,
			IllegalAccessException;

	public String toJSON(List<Object> list) throws JSONException,
			IllegalAccessException;

	public void removeAllRecordsForAAIDKEYID(String aaid_keyid);

	public List<AuthenticationRequest> getAuthReqResponse(int accountId);

	public String[] getAllowedAaids(int accountId);
}
