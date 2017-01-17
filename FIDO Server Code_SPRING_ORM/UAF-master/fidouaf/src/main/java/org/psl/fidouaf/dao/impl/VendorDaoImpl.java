package org.psl.fidouaf.dao.impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.psl.fidouaf.dao.VendorDao;
import org.psl.fidouaf.entity.VendorDetails;
import org.psl.fidouaf.res.util.ConnectionUtil;
import org.springframework.stereotype.Repository;

@Repository
public class VendorDaoImpl implements VendorDao {

	/**
	 * Method that inserts new Vendor Registration Data in DB.
	 */
	public boolean saveVendor(VendorDetails vendorDetails) {
		boolean isDuplicateVendor = false;

		Session session = ConnectionUtil.OpenSession();
		VendorDetails vendorDetailsFromDB = (VendorDetails) session.get(
				VendorDetails.class, vendorDetails.getAccountid());

		if (vendorDetailsFromDB != null) {
			System.out
					.println("Vendor with given RP Account Id already exists in DB.");
			isDuplicateVendor = true;
		} else {
			Transaction transaction = null;
			try {
				transaction = session.beginTransaction();
				session.save(vendorDetails);
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
		return isDuplicateVendor;
		/*
		 * Configuration configuration = new Configuration();
		 * configuration.configure(); ServiceRegistry serviceRegistry = new
		 * StandardServiceRegistryBuilder().applySettings(
		 * configuration.getProperties()).build(); SessionFactory sessionFactory
		 * = new
		 * Configuration().configure().buildSessionFactory(serviceRegistry);
		 * Session session = sessionFactory.openSession(); Transaction
		 * transaction = session.beginTransaction();
		 * session.persist(vendorDetails); transaction.commit();
		 * session.close();
		 */
	}

	/**
	 * Method that gets All Important Vendor Details for given RP Account Id.
	 */
	public VendorDetails getVendorDetails(int accountId) {
		Session session = ConnectionUtil.OpenSession();
		VendorDetails vendorDetailsFromDB = (VendorDetails) session.get(
				VendorDetails.class, accountId);
		return vendorDetailsFromDB;
	}

	/**
	 * 
	 * Method that marks the Vendor Registration Status to True in Vendor
	 * Details Table.
	 * 
	 * @param vendorDetails
	 */
	public void updateVendorRegistrationStatus(VendorDetails vendorDetails) {
		System.out
				.println("\nUpdating Vendor Registration status in Vendor table");
		Session session = ConnectionUtil.OpenSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			VendorDetails vendorDetailsFromDB = (VendorDetails) session.get(
					VendorDetails.class, vendorDetails.getAccountid());
			vendorDetailsFromDB.setVendor_regstats(true);
			session.update(vendorDetailsFromDB);
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
}
