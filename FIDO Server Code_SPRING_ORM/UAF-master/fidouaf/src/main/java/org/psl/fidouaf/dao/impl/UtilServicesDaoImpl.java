package org.psl.fidouaf.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.psl.fidouaf.dao.UtilServicesDao;
import org.psl.fidouaf.entity.Device;
import org.psl.fidouaf.entity.PublicKeyDetails;
import org.psl.fidouaf.entity.Registration;
import org.psl.fidouaf.entity.VendorDetails;
import org.psl.fidouaf.res.util.ConnectionUtil;
import org.springframework.stereotype.Repository;

@Repository
public class UtilServicesDaoImpl implements UtilServicesDao {

	/**
	 * Method that gets AAIDs from DB for Given RP Account ID and Device Id.
	 */
	@SuppressWarnings({ "unchecked" })
	public String[] getAAIDforDeviceIdAccountId(String deviceId, int rpAccountId) {
		String[] aaids = null;
		String aaid = null;
		List<String> aaidsFound = new ArrayList<String>();
		// List<String> aaidsFromDB = new ArrayList<String>();

		Session session = ConnectionUtil.OpenSession();

		// Criteria criteriaQuery =
		// session.createCriteria(PublicKeyDetails.class);
		// criteriaQuery.createAlias("publickeydetails", "pubKeyDetails");
		// Criterion deviceIdCondition = Restrictions.eq("deviceid", deviceId);
		// Criterion rpAccountIdCondition =
		// Restrictions.eq("rpaccountid",rpAccountId);

		// To get records matching with AND conditions
		// LogicalExpression andExp =
		// Restrictions.and(deviceIdCondition,rpAccountIdCondition);
		// criteriaQuery.add(andExp);

		/*
		 * FUTURE REFERENCE:
		 * https://www.tutorialspoint.com/hibernate/hibernate_criteria_queries
		 * .htm
		 */

		// To get records matching with OR conditions
		// LogicalExpression orExp = Restrictions.or(salary, name);
		// cr.add( orExp );

		// List<PublicKeyDetails> publicKeyDetails = criteriaQuery.list();
		// for (int j = 0; j < publicKeyDetails.size(); j++) {
		// aaidsFromDB.add(publicKeyDetails.get(j).getAaid_keyid());
		// }

		/*
		 * Example using SQLQuery class. REFERENCE:
		 * http://stackoverflow.com/questions
		 * /13126457/hibernate-retrieve-results-from-database-based-on-condition
		 */

		String dbQuery = "SELECT aaid_keyid FROM key_info WHERE deviceid='"
				+ deviceId + "' and rpaccountid= " + rpAccountId;
		SQLQuery query = session.createSQLQuery(dbQuery);
		// query.setString("deviceid", deviceId);
		// query.setString("rpaccountid", rpAccountId);
		List<String> aaidsFromDB = query.list();

		for (int i = 0; i < aaidsFromDB.size(); i++) {
			String[] parts = aaidsFromDB.get(i).split("#");
			String part1 = parts[0];
			String part2 = parts[1];
			aaid = part1 + "#" + part2;
			aaidsFound.add(aaid);
		}

		// Convert ArrayList to String[]
		aaids = aaidsFound.toArray(new String[aaidsFound.size()]);
		return aaids;
	}

	/**
	 * Method that removes record from all tables in DB for given AAID_KEYID
	 * Key.
	 * 
	 * @param key
	 */
	public void removeAllRecordsForAAIDKEYID(String aaid_keyid) {
		String deviceId = null;
		int rpAccountId = 0;

		Session session = ConnectionUtil.OpenSession();

		PublicKeyDetails publicKeyDetails = (PublicKeyDetails) session.get(
				PublicKeyDetails.class, aaid_keyid);

		if (publicKeyDetails != null) {
			rpAccountId = publicKeyDetails.getRpAccountId();
			deviceId = publicKeyDetails.getDeviceId();

			Transaction transaction = session.beginTransaction();

			// Delete transaction table record
			/*try {
				TransactionDetails transactionDetails = (TransactionDetails) session
						.get(TransactionDetails.class, rpAccountId);
				session.delete(transactionDetails);
			} catch (Exception e) {
				transaction.rollback();
				e.printStackTrace();
			}*/

			// Delete key_info table record
			try {
				PublicKeyDetails keyDetails = (PublicKeyDetails) session.get(
						PublicKeyDetails.class, aaid_keyid);
				session.delete(keyDetails);
			} catch (Exception e) {
				transaction.rollback();
				e.printStackTrace();
			}

			// Delete registration table record
			try {
				Registration registrationDetails = (Registration) session.get(
						Registration.class, rpAccountId);
				session.delete(registrationDetails);
			} catch (Exception e) {
				transaction.rollback();
				e.printStackTrace();
			}

			// Delete Device table record
			try {
				Device deviceDetails = (Device) session.get(Device.class,
						deviceId);
				session.delete(deviceDetails);
			} catch (Exception e) {
				transaction.rollback();
				e.printStackTrace();
			}

			// Delete Vendor table record
			try {
				VendorDetails vendorDetails = (VendorDetails) session.get(
						VendorDetails.class, rpAccountId);
				session.delete(vendorDetails);
			} catch (Exception e) {
				transaction.rollback();
				e.printStackTrace();
			}
		} else {
			System.out
					.println("No Key Information was found for the Given AAID#KEYID.");
		}
	}

	/**
	 * Method that gets AAIDs from DB for Given RP Account ID.
	 */
	@SuppressWarnings("unchecked")
	public String[] getAAIDsEnforcedForAccountId(int rpAccountId) {
		String[] aaids = null;
		List<String> aaidsFound = new ArrayList<String>();

		Session session = ConnectionUtil.OpenSession();

		String dbQuery = "SELECT authenticators_enforced FROM registrationdb WHERE rpaccountid= "
				+ rpAccountId;
		SQLQuery query = session.createSQLQuery(dbQuery);
		List<String> aaidsFromDB = query.list();

		String[] aaidList = aaidsFromDB.toArray(new String[aaidsFromDB.size()]);

		for (int i = 0; i < aaidList.length; i++) {
			aaidList[i] = aaidList[i].replace("[", "");
			aaidList[i] = aaidList[i].replace("]", "");
			aaidList[i] = aaidList[i].replace(" ", "");

			if (aaidList[i].contains(",")) {
				String[] parts = aaidList[i].split(",");
				for (int j = 0; j < parts.length; j++) {
					aaidsFound.add(parts[j]);
				}
			} else {
				aaidsFound.add(aaidList[i]);
			}
		}

		// Convert ArrayList to String[]
		aaids = aaidsFound.toArray(new String[aaidsFound.size()]);
		return aaids;
	}
}
