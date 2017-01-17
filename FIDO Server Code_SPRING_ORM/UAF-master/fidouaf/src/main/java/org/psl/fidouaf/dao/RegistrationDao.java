package org.psl.fidouaf.dao;

import java.util.Map;

import org.psl.fidouaf.core.entity.RegistrationRecord;
import org.psl.fidouaf.entity.PublicKeyDetails;
import org.psl.fidouaf.entity.Registration;
import org.psl.fidouaf.entity.VendorDetails;
import org.psl.fidouaf.exceptions.DataRecordNotFoundException;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationDao {

	public boolean getIsRegisteredUser(int rpAccountId);

	public Map<String, Boolean> getAuthFlagsforAccountId(int rpAccountId);

	public void resetAuthFlags(int accountId);

	public String getDeviceIdForAccountId(int rpAccountId);

	public String addPublicKeyDetails(PublicKeyDetails publicKeyDetails);

	public void updateAuthenticatorRegStatus(String rpAccountId);

	public void showAllRegistrationData();

	public void showAllPublicKeyDetails();

	public void markAuthenticationInProgressToTrue(int accountId);

	public void addRPDetails(VendorDetails vendorDetails);

	public boolean updateAuthStatusInDB(String aaid_Keyid);

	public void updateUserContextAndAuthenticators(
			Registration registrationDetails);

	public String getContextForAccountID(int rpAccountId);

	public RegistrationRecord readRegistrationRecordfromDB(String aaid_keyid);

	public Registration findRegistration(int rpAccountId)
			throws DataRecordNotFoundException;
}
