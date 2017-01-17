package org.psl.fidouaf.service;

import java.util.Map;

import org.psl.fidouaf.core.entity.RegistrationRecord;
import org.psl.fidouaf.entity.PublicKeyDetails;
import org.psl.fidouaf.entity.Registration;
import org.psl.fidouaf.entity.VendorDetails;
import org.psl.fidouaf.exceptions.DataRecordNotFoundException;
import org.psl.fidouaf.exceptions.DuplicateAAIDKeyIdException;
import org.psl.fidouaf.exceptions.DuplicatePublicKeyException;
import org.springframework.stereotype.Service;

@Service
public interface RegistrationService {
	public boolean getIsRegisteredUser(int rpAccountid);

	public Map<String, Boolean> getAuthFlagsforAccountId(int rpAccountId)
			throws DataRecordNotFoundException;

	public void resetAuthFlags(int rpAccountId);

	public String getDeviceIdForAccountId(int rpAccountId)
			throws DataRecordNotFoundException;

	public void addPublicKeyDetails(PublicKeyDetails publicKeyDetails)
			throws DuplicatePublicKeyException, DuplicateAAIDKeyIdException;

	public void updateAuthenticatorRegStatus(String rpAccountId);

	public void showAllRegistrationData();

	public void showAllPublicKeyDetails();

	public void markAuthenticationInProgressToTrue(int accountId);

	public void insertRPDetailsInRegDB(VendorDetails vendorDetails)
			throws DataRecordNotFoundException;

	public void updateAuthStatusInDB(String aaid_Keyid)
			throws DataRecordNotFoundException;

	public void updateUserContextAndAuthenticators(
			Registration registrationDetails);

	public String getContextForAccountID(int rpAccountId)
			throws DataRecordNotFoundException;

	public Registration findRegistration(int rpAccountId)
			throws DataRecordNotFoundException;

	public RegistrationRecord readRegistrationRecordfromDB(String string)
			throws DataRecordNotFoundException;
}
