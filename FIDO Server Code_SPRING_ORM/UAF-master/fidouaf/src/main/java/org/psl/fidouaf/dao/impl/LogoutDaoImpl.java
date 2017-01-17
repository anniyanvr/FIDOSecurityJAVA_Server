package org.psl.fidouaf.dao.impl;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.psl.fidouaf.dao.LogoutDao;
import org.psl.fidouaf.res.util.ConnectionUtil;
import org.springframework.stereotype.Repository;

@Repository
public class LogoutDaoImpl implements LogoutDao {

	/**
	 * Removes all records from all tables in Database.
	 * 
	 * @return true or false.
	 */
	public boolean removeAllRecords() {
		boolean isDeleted = true;
		int vendorDeleted = 0, deviceDeleted = 0, transactionDeleted = 0, registrationDeleted = 0, keysDeleted = 0;

		Session session = ConnectionUtil.OpenSession();
		Transaction transaction = session.beginTransaction();

		// Delete Transaction Data
		try {
			String stringQuery1 = "DELETE FROM TransactionDetails";
			Query query1 = session.createQuery(stringQuery1);
			transactionDeleted = query1.executeUpdate();
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			e.printStackTrace();
		}

		// Delete Public Key data
		try {
			transaction = session.beginTransaction();
			String stringQuery2 = "DELETE FROM PublicKeyDetails";
			Query query2 = session.createQuery(stringQuery2);
			keysDeleted = query2.executeUpdate();
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			e.printStackTrace();
		}

		// Delete Registration Data
		try {
			transaction = session.beginTransaction();
			String stringQuery3 = "DELETE FROM Registration";
			Query query3 = session.createQuery(stringQuery3);
			registrationDeleted = query3.executeUpdate();
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			e.printStackTrace();
		}

		// Delete Device Data
		try {
			transaction = session.beginTransaction();
			String stringQuery4 = "DELETE FROM Device";
			Query query4 = session.createQuery(stringQuery4);
			deviceDeleted = query4.executeUpdate();
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			e.printStackTrace();
		}

		// Delete Vendor Data
		try {
			transaction = session.beginTransaction();
			String stringQuery5 = "DELETE FROM VendorDetails";
			Query query5 = session.createQuery(stringQuery5);
			vendorDeleted = query5.executeUpdate();
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			e.printStackTrace();
		}

		// If all tables are deleted successful, then return true, else return
		// false.
		if (vendorDeleted == 0 && deviceDeleted == 0 && transactionDeleted == 0
				&& registrationDeleted == 0 && keysDeleted == 0) {
			isDeleted = false;
		}
		return isDeleted;
	}
}
