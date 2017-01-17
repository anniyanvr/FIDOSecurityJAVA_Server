package org.psl.fidouaf.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.psl.fidouaf.core.constants.Constants;
import org.psl.fidouaf.core.entity.RegistrationRecord;
import org.psl.fidouaf.dao.RegistrationDao;
import org.psl.fidouaf.entity.PublicKeyDetails;
import org.psl.fidouaf.entity.Registration;
import org.psl.fidouaf.entity.VendorDetails;
import org.psl.fidouaf.exceptions.DataRecordNotFoundException;
import org.psl.fidouaf.res.util.ConnectionUtil;
import org.springframework.stereotype.Repository;

@Repository
public class RegistrationDaoImpl implements RegistrationDao {

	/**
	 * Method that gets the FIDO registration status from Database based on RP
	 * Account ID.
	 * 
	 * @param rpAccountId
	 * @return Registration status as a boolean value
	 */
	public boolean getIsRegisteredUser(int rpAccountId) {
		boolean regStatus = false;

		Session session = ConnectionUtil.OpenSession();
		Registration registrationDetails = (Registration) session.get(
				Registration.class, rpAccountId);

		// Query query =
		// session.createQuery("from Registration where rpaccountid=:rpaccountid");
		// query.setParameter("rpaccountid", rpAccountId);
		// Registration registrationDetails = (Registration)
		// query.uniqueResult();

		if (registrationDetails != null) {
			System.out
					.println("\n User present with the given RP account Id in Registration table");
			regStatus = registrationDetails.getRegStats();
		} else {
			System.out
					.println("\n User not present with the given RP account Id in Registration table");
		}
		return regStatus;
	}

	/**
	 * Method that gets the Auth. operation flags from Database based on RP
	 * Account ID.
	 * 
	 * @param rpAccountId
	 * @return Map of auth operation flag values.
	 */
	public Map<String, Boolean> getAuthFlagsforAccountId(int rpAccountId) {
		Map<String, Boolean> authFlags = new HashMap<String, Boolean>();

		Session session = ConnectionUtil.OpenSession();
		Registration registrationDetails = (Registration) session.get(
				Registration.class, rpAccountId);

		if (registrationDetails != null) {
			System.out
					.println("\n User present with the given RP account Id in Registration table");
			authFlags.put("auth_in_progress",
					registrationDetails.getAuthInProgress());
			authFlags.put("authstats", registrationDetails.getAuthStatus());
		} else {
			System.out
					.println("\n User not present with the given RP account Id in Registration table");
			authFlags.put("auth_in_progress", null);
			authFlags.put("authstats", null);
		}

		return authFlags;
	}

	/**
	 * Method that resets the FIDO Auth. operation related flags in Database.
	 */
	public void resetAuthFlags(int rpAccountId) {
		Session session = ConnectionUtil.OpenSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			Registration registrationDetails = (Registration) session.get(
					Registration.class, rpAccountId);
			registrationDetails.setAuthInProgress(false);
			registrationDetails.setAuthStatus(false);
			session.update(registrationDetails);
			transaction.commit();
		} catch (Exception exception) {
			if (transaction != null) {
				transaction.rollback();
				// log the exception
			}
		} finally {
			session.close();
		}
	}

	/**
	 * Method that gets DeviceId for given RP Account Id.
	 */
	public String getDeviceIdForAccountId(int rpAccountId) {
		String deviceId = null;
		Session session = ConnectionUtil.OpenSession();
		Registration registrationDetails = (Registration) session.get(
				Registration.class, rpAccountId);

		if (registrationDetails != null) {
			System.out
					.println("\n User present with the given RP account Id in Registration table");
			deviceId = registrationDetails.getDeviceId();
		} else {
			System.out
					.println("\n User not present with the given RP account Id in Registration table");
		}
		return deviceId;
	}

	/**
	 * Stores the RP Account ID and DeviceId mapping in Database.
	 */
	public String addPublicKeyDetails(PublicKeyDetails publicKeyDetails) {

		boolean isDuplicateAAID_KEYID = false, isDuplicatePubKey = false;
		List<PublicKeyDetails> publicKeyDetailsFromDB = new ArrayList<PublicKeyDetails>();
		String isDataAdded = null;

		// Get the public key, DeviceId and AAID#KEYID for the given RP
		// AccountId
		Session session = ConnectionUtil.OpenSession();
		publicKeyDetailsFromDB.add((PublicKeyDetails) session.get(
				PublicKeyDetails.class, publicKeyDetails.getRpAccountId()));

		if (publicKeyDetailsFromDB != null) { // this means a record/records
												// exists in the DB with given
												// RP Account Id.
			for (int i = 0; i < publicKeyDetailsFromDB.size(); i++) {
				// Check if deviceId matches with the one to be inserted
				// freshly.
				if (publicKeyDetailsFromDB.get(i).getDeviceId()
						.equals(publicKeyDetails.getDeviceId())) {
					System.out.println("Device Id Matches!");

					// check if publicKey matches with the one to be inserted
					// freshly.
					if (publicKeyDetailsFromDB.get(i).getPublicKey()
							.equals(publicKeyDetails.getPublicKey())) {
						System.out
								.println("Same Public key exists with passed DeviceId and AccountId!");
						isDuplicatePubKey = true;
						break;
					}
					// check if aaid_keyid matches with the one to be inserted
					// freshly.
					if (publicKeyDetailsFromDB.get(i).getAaid_keyid()
							.equals(publicKeyDetails.getAaid_keyid())) {
						System.out
								.println("Same AAID_KEYID combo exists with passed DeviceId and AccountId");
						isDuplicateAAID_KEYID = true;
						break;
					}
				}
			}
		}
		if (isDuplicatePubKey) {
			isDataAdded = Constants.DUPLICATE_PUBLIC_KEY;
		} else if (isDuplicateAAID_KEYID) {
			isDataAdded = Constants.DUPLICATE_AAID_KEYID;
		} else {
			Transaction transaction = null;
			try {
				transaction = session.beginTransaction();
				session.save(publicKeyDetails);
				transaction.commit();
				isDataAdded = Constants.INSERT_SUCCESSFULL;
			} catch (Exception exception) {
				if (transaction != null) {
					transaction.rollback();
					// log the exception
				}
			} finally {
				session.close();
			}
		}
		return isDataAdded;
	}

	/**
	 * Method that updates Registration status field in Registration DB
	 */
	public void updateAuthenticatorRegStatus(String rpAccountId) {
		Session session = ConnectionUtil.OpenSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			Registration registrationDetails = (Registration) session.get(
					Registration.class, rpAccountId);
			registrationDetails.setRegStats(true);
			session.update(registrationDetails);
			transaction.commit();
		} catch (Exception exception) {
			if (transaction != null) {
				transaction.rollback();
				// log the exception
			}
		} finally {
			session.close();
		}
	}

	/**
	 * Method that shows all records in Registration Table in DB.
	 */
	@SuppressWarnings("unchecked")
	public void showAllRegistrationData() {
		Session session = ConnectionUtil.OpenSession();
		Transaction transaction = null;
		;
		try {
			transaction = session.beginTransaction();
			List<Registration> deviceRegistrations = (List<Registration>) session
					.createQuery("FROM registrationdb").list();
			for (Iterator<Registration> iterator = deviceRegistrations
					.iterator(); iterator.hasNext();) {
				Registration deviceRegistration = iterator.next();
				System.out.print("Device ID: "
						+ deviceRegistration.getDeviceId());
				System.out.print("RP Account Name: "
						+ deviceRegistration.getRpDisplayName());
				System.out.print("Registration Status: "
						+ deviceRegistration.getRegStats());
				System.out.print("RP Account ID: "
						+ deviceRegistration.getRpAccountId());
				System.out.print("Authentication In Progress?: "
						+ deviceRegistration.getAuthInProgress());
				System.out.print("Authentication Status: "
						+ deviceRegistration.getAuthStatus());
				System.out.print("Context: " + deviceRegistration.getContext());
				System.out
						.print("Authenticators Enforced: "
								+ /* deviceRegistration.getAuthenticators() */deviceRegistration
										.getAaids() + "\n");
			}
			transaction.commit();
		} catch (HibernateException e) {
			if (transaction != null)
				transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	/**
	 * Method that shows all records in Key_Info Table in DB.
	 */
	@SuppressWarnings("unchecked")
	public void showAllPublicKeyDetails() {
		Session session = ConnectionUtil.OpenSession();
		Transaction transaction = null;
		;
		try {
			transaction = session.beginTransaction();
			List<PublicKeyDetails> publicKeyDetails = (List<PublicKeyDetails>) session
					.createQuery("FROM key_info").list();
			for (Iterator<PublicKeyDetails> iterator = publicKeyDetails
					.iterator(); iterator.hasNext();) {
				PublicKeyDetails publicKeyDetail = iterator.next();
				System.out.print("Device ID: " + publicKeyDetail.getDeviceId());
				System.out.print("RP Account ID: "
						+ publicKeyDetail.getRpAccountId());
				System.out.print("Public Key: "
						+ publicKeyDetail.getPublicKey());
				System.out.print("AAID#KEYID: "
						+ publicKeyDetail.getAaid_keyid() + "\n");
			}
			transaction.commit();
		} catch (HibernateException e) {
			if (transaction != null)
				transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	/**
	 * Method that marks Auth_In_Progress flag to true.
	 */
	public void markAuthenticationInProgressToTrue(int accountId) {
		Session session = ConnectionUtil.OpenSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			Registration registrationDetails = (Registration) session.get(
					Registration.class, accountId);
			registrationDetails.setAuthInProgress(true);
			session.update(registrationDetails);
			transaction.commit();
		} catch (Exception exception) {
			if (transaction != null) {
				transaction.rollback();
				// log the exception
			}
		} finally {
			session.close();
		}
	}

	/**
	 * Method that adds new Registration details in DB.
	 */
	public void addRPDetails(VendorDetails vendorDetails) {
		System.out
				.println("\nInserting New record in Registration table for given Vendor-Device Entry.");

		Registration registrationDetails = new Registration();
		registrationDetails.setDeviceId(vendorDetails.getDeviceId());
		registrationDetails.setRpDisplayName(vendorDetails.getVendorName());
		registrationDetails.setRegStats(false);
		registrationDetails.setRpAccountId(vendorDetails.getAccountid());
		registrationDetails.setAuthInProgress(false);
		registrationDetails.setAuthStatus(false);

		Session session = ConnectionUtil.OpenSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			session.save(registrationDetails);
			transaction.commit();
		} catch (Exception exception) {
			if (transaction != null) {
				transaction.rollback();
				// log the exception
			}
		} finally {
			session.close();
		}
	}

	/**
	 * Method to update authstats field in registration table to true to mark
	 * the success of auth response API.
	 * 
	 * @param aaid_Keyid
	 */
	public boolean updateAuthStatusInDB(String aaid_Keyid) {
		int rpAccountId = 0;
		boolean isUpdateSuccessful = false;
		Session session = ConnectionUtil.OpenSession();
		PublicKeyDetails publicKeyDetails = (PublicKeyDetails) session.get(
				PublicKeyDetails.class, aaid_Keyid);

		if (publicKeyDetails != null) {
			rpAccountId = publicKeyDetails.getRpAccountId();
		} else {
			System.out
					.println("No Key Information was found for the Given AAID#KEYID.");
		}

		if (rpAccountId != 0) {
			Transaction transaction = null;
			try {
				transaction = session.beginTransaction();
				Registration registrationDetails = (Registration) session.get(
						Registration.class, rpAccountId);
				registrationDetails.setAuthStatus(true);
				session.update(registrationDetails);
				transaction.commit();
				isUpdateSuccessful = true;
			} catch (Exception exception) {
				if (transaction != null) {
					transaction.rollback();
					// log the exception
				}
			} finally {
				session.close();
			}
		} else {
			System.out
					.println("RP Account Id not found due to unavailability of given AAID#KEYID in Database.");
		}
		return isUpdateSuccessful;
	}

	/**
	 * Method that sets user context and authenticators enforced during
	 * authentication (Flexible Authentication)
	 * 
	 * @param registrationDetails
	 */
	public void updateUserContextAndAuthenticators(
			Registration registrationDetails) {
		Session session = ConnectionUtil.OpenSession();

		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			Registration registrationDetailsFromDB = (Registration) session
					.get(Registration.class,
							registrationDetails.getRpAccountId());
			registrationDetailsFromDB.setContext(registrationDetails
					.getContext());
			registrationDetailsFromDB.setAaids(registrationDetails.getAaids());
			session.update(registrationDetails);
			transaction.commit();
		} catch (Exception exception) {
			if (transaction != null) {
				transaction.rollback();
				// log the exception
			}
		} finally {
			session.close();
		}
	}

	/**
	 * Method that gets context for given RP Account ID from registration table.
	 */
	public String getContextForAccountID(int rpAccountId) {
		String context = null;
		Session session = ConnectionUtil.OpenSession();
		Registration registrationDetails = (Registration) session.get(
				Registration.class, rpAccountId);

		if (registrationDetails != null) {
			System.out
					.println("\n User present with the given RP account Id in Registration table");
			context = registrationDetails.getContext();
		} else {
			System.out
					.println("\n User not present with the given RP account Id in Registration table");
		}
		return context;
	}

	/**
	 * Method that reads registration record details from DB for given
	 * AAID#KEYID.
	 */
	public RegistrationRecord readRegistrationRecordfromDB(String aaid_keyid) {
		RegistrationRecord registrationRecord = new RegistrationRecord();

		Session session = ConnectionUtil.OpenSession();
		PublicKeyDetails publicKeyDetails = (PublicKeyDetails) session.get(
				PublicKeyDetails.class, aaid_keyid);

		if (publicKeyDetails != null) {
			registrationRecord.username = String.valueOf(publicKeyDetails
					.getRpAccountId());
			registrationRecord.PublicKey = publicKeyDetails.getPublicKey();
			registrationRecord.deviceId = publicKeyDetails.getDeviceId();
		} else {
			System.out
					.println("No Key Information was found for the Given AAID#KEYID.");
		}
		return registrationRecord;
	}

	/**
	 * Method that finds registration details based on RP account ID.
	 */
	public Registration findRegistration(int rpAccountId)
			throws DataRecordNotFoundException {
		Session session = ConnectionUtil.OpenSession();
		Registration registrationDetails = (Registration) session.get(
				Registration.class, rpAccountId);
		return registrationDetails;
	}
}
