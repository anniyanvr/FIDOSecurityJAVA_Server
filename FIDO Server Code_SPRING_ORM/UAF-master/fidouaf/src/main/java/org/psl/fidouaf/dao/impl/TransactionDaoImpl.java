package org.psl.fidouaf.dao.impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.psl.fidouaf.dao.TransactionDao;
import org.psl.fidouaf.entity.TransactionDetails;
import org.psl.fidouaf.res.util.ConnectionUtil;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionDaoImpl implements TransactionDao {

	/**
	 * Method that saves New transaction details in DB.
	 * 
	 * @param transactionDetails
	 */
	public void saveTransaction(TransactionDetails transactionDetails) {

		Session session = ConnectionUtil.OpenSession();
		Transaction transaction = session.beginTransaction();
		try {
			//Transaction transaction = session.beginTransaction();
			session.save(transactionDetails);
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
	 * Method that gets Transaction Details from DB for given RP account Id.
	 */
	public TransactionDetails findTransaction(int rpAccountId) {
		Session session = ConnectionUtil.OpenSession();
		TransactionDetails transactionDetails = (TransactionDetails) session
				.get(TransactionDetails.class, rpAccountId);
		return transactionDetails;
	}
}
