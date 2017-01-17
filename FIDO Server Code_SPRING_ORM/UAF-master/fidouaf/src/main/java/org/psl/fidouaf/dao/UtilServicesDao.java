package org.psl.fidouaf.dao;

import org.springframework.stereotype.Repository;

@Repository
public interface UtilServicesDao {
	public String[] getAAIDforDeviceIdAccountId(String deviceId,
			int rpAccountId);

	public void removeAllRecordsForAAIDKEYID(String aaid_keyid);

	public String[] getAAIDsEnforcedForAccountId(int rpAccountId);
}
