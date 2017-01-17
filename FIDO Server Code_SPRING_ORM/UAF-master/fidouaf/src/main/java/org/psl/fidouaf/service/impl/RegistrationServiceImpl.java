package org.psl.fidouaf.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.psl.fidouaf.core.constants.Constants;
import org.psl.fidouaf.core.entity.RegistrationRecord;
import org.psl.fidouaf.dao.DeviceDao;
import org.psl.fidouaf.dao.RegistrationDao;
import org.psl.fidouaf.entity.Device;
import org.psl.fidouaf.entity.PublicKeyDetails;
import org.psl.fidouaf.entity.Registration;
import org.psl.fidouaf.entity.VendorDetails;
import org.psl.fidouaf.exceptions.DataRecordNotFoundException;
import org.psl.fidouaf.exceptions.DuplicateAAIDKeyIdException;
import org.psl.fidouaf.exceptions.DuplicatePublicKeyException;
import org.psl.fidouaf.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationServiceImpl implements RegistrationService {

	@Autowired
	private RegistrationDao registrationDao;

	@Autowired
	private DeviceDao deviceDao;

	// NEW_CODE_ADDED_BY_JASMIRA
	/**
	 * Method that checks if a user is FIDO Registered or not based on RP
	 * Account Id.
	 * 
	 * @param rpaccountid
	 * @return
	 */
	public boolean getIsRegisteredUser(int rpAccountId) {
		boolean isRegistered = registrationDao.getIsRegisteredUser(rpAccountId);
		return isRegistered;
	}

	/**
	 * Method that gets the Auth operation related flags from DB based on the RP
	 * Account Id.
	 * 
	 * @throws DataRecordNotFoundException
	 */
	public Map<String, Boolean> getAuthFlagsforAccountId(int rpAccountId)
			throws DataRecordNotFoundException {
		Map<String, Boolean> authFlags = new HashMap<String, Boolean>();
		authFlags = registrationDao.getAuthFlagsforAccountId(rpAccountId);
		if (authFlags.get("auth_in_progress").equals(null)
				|| authFlags.get("authstats").equals(null)) {
			throw new DataRecordNotFoundException(
					"Auth flags are empty due to user unavailable with the given RP Account ID in DB.");
		} else {
			return authFlags;
		}
	}

	/**
	 * Method that resets the FIDO Auth. operation related flags based on RP
	 * Account Id.
	 */
	public void resetAuthFlags(int rpAccountId) {
		registrationDao.resetAuthFlags(rpAccountId);
	}

	/**
	 * Method that gets the DeviceId from DB for given RP Account Id.
	 * 
	 * @throws DataRecordNotFoundException
	 */
	public String getDeviceIdForAccountId(int rpAccountId)
			throws DataRecordNotFoundException {
		String deviceId = null;
		deviceId = registrationDao.getDeviceIdForAccountId(rpAccountId);
		if (deviceId.equalsIgnoreCase(null)) {
			throw new DataRecordNotFoundException(
					"Device Id is not found due to user unavailable with the given RP Account ID in DB.");
		} else {
			return deviceId;
		}
	}

	/**
	 * Stores the RP Account ID and DeviceId mapping in Database.
	 */
	public void addPublicKeyDetails(PublicKeyDetails publicKeyDetails)
			throws DuplicatePublicKeyException, DuplicateAAIDKeyIdException {
		String isDataAdded = registrationDao
				.addPublicKeyDetails(publicKeyDetails);
		if (isDataAdded.equalsIgnoreCase(Constants.DUPLICATE_PUBLIC_KEY)) {
			throw new DuplicatePublicKeyException(
					"DUPLICATE RECORD: Public Key already exists with given DeviceId and RP Account Id");
		} else if (isDataAdded.equalsIgnoreCase(Constants.DUPLICATE_AAID_KEYID)) {
			throw new DuplicateAAIDKeyIdException(
					"DUPLICATE RECORD: AAID#KEYID already exists with given DeviceId and RP Account Id");
		} else if (isDataAdded.equalsIgnoreCase(Constants.INSERT_SUCCESSFULL)) {
			System.out.println("Data was successfully inserted in Database");
		}
	}

	/**
	 * Method that updates Registration status field in Registration DB
	 */
	public void updateAuthenticatorRegStatus(String rpAccountId) {
		registrationDao.updateAuthenticatorRegStatus(rpAccountId);

	}

	/**
	 * Method that shows all records in Registration Table in DB.
	 */
	public void showAllRegistrationData() {
		registrationDao.showAllRegistrationData();

	}

	/**
	 * Method that shows all records in Key_Info Table in DB.
	 */
	public void showAllPublicKeyDetails() {
		registrationDao.showAllPublicKeyDetails();
	}

	/**
	 * Method that marks Auth_In_Progress flag to true.
	 */
	public void markAuthenticationInProgressToTrue(int accountId) {
		registrationDao.markAuthenticationInProgressToTrue(accountId);
	}

	/**
	 * Method that inserts Vendor details into Registration Table to map the
	 * device(user) to the Vendor with which it has an account.
	 * 
	 * @throws DataRecordNotFoundException
	 */
	public void insertRPDetailsInRegDB(VendorDetails vendorDetails)
			throws DataRecordNotFoundException {
		Device device = deviceDao.findDevice(vendorDetails.getDeviceId());
		if (device != null && !StringUtils.isBlank(device.getDeviceid())) {
			System.out.println("DeviceId is registered in Database.");
			registrationDao.addRPDetails(vendorDetails);
		} else {
			throw new DataRecordNotFoundException(
					"Device Id not found (Registered) In Database.");
		}
	}

	/**
	 * Method to update authstats field in registration table to true to mark
	 * the success of auth response API.
	 * 
	 * @param aaid_Keyid
	 * @throws DataRecordNotFoundException
	 */
	public void updateAuthStatusInDB(String aaid_Keyid)
			throws DataRecordNotFoundException {
		boolean isUpdateSuccessfull = registrationDao
				.updateAuthStatusInDB(aaid_Keyid);
		if (!isUpdateSuccessfull) {
			throw new DataRecordNotFoundException(
					"Authentication Status Updation failed due to record not being found for given AAID#KEYID: "
							+ aaid_Keyid);
		}
	}

	/**
	 * Method that sets user context and authenticators enforced during
	 * authentication (Flexible Authentication)
	 * 
	 * @param registrationDetails
	 */
	public void updateUserContextAndAuthenticators(
			Registration registrationDetails) {
		registrationDao.updateUserContextAndAuthenticators(registrationDetails);
	}

	/**
	 * Method that gets context for given RP Account ID from registration table.
	 * 
	 * @throws DataRecordNotFoundException
	 */
	public String getContextForAccountID(int rpAccountId)
			throws DataRecordNotFoundException {
		String context = registrationDao.getContextForAccountID(rpAccountId);
		if (context == null) {
			throw new DataRecordNotFoundException(
					"Context details not found due to unavailability of the given RP account Id in Database. "
							+ rpAccountId);
		} else {
			return context;
		}
	}

	/**
	 * Method that reads registration record details from DB for given
	 * AAID#KEYID.
	 * 
	 * @throws DataRecordNotFoundException
	 */
	public RegistrationRecord readRegistrationRecordfromDB(String aaid_keyid)
			throws DataRecordNotFoundException {
		RegistrationRecord registrationRecord = registrationDao
				.readRegistrationRecordfromDB(aaid_keyid);
		if (registrationRecord.getUsername() == null) {
			throw new DataRecordNotFoundException(
					"Registration record not found due to unavailability of given AAID#KEYID: "
							+ aaid_keyid + " in Database.");
		} else {
			return registrationRecord;
		}
	}

	/**
	 * Method that finds registration details based on RP account ID.
	 */
	public Registration findRegistration(int rpAccountId)
			throws DataRecordNotFoundException {
		Registration registrationDetails = registrationDao.findRegistration(rpAccountId);
		if (registrationDetails == null) {
			throw new DataRecordNotFoundException(
					"Registration record not found due to unavailability of given RP Account Id: "
							+ rpAccountId + " in Database.");
		} else {
			return registrationDetails;
		}
	}
}
