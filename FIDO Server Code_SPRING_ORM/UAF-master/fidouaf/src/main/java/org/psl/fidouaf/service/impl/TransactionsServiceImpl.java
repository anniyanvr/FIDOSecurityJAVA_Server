package org.psl.fidouaf.service.impl;

import org.psl.fidouaf.dao.TransactionDao;
import org.psl.fidouaf.entity.TransactionDetails;
import org.psl.fidouaf.exceptions.DataRecordNotFoundException;
import org.psl.fidouaf.service.TransactionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionsServiceImpl implements TransactionsService {

	@Autowired
	private TransactionDao transactionDao;

	/**
	 * Method that saves New transaction details in DB.
	 * 
	 * @param transactionDetails
	 */
	public void saveTransaction(TransactionDetails transactionDetails) {
		transactionDao.saveTransaction(transactionDetails);
	}

	/**
	 * Method that gets Transaction Details from DB for given RP account Id.
	 * 
	 * @throws DataRecordNotFoundException
	 */
	public TransactionDetails findTransaction(int rpAccountId)
			throws DataRecordNotFoundException {
		TransactionDetails transactionDetails = transactionDao
				.findTransaction(rpAccountId);
		if (transactionDetails == null) {
			throw new DataRecordNotFoundException(
					"EMPTY RECORD: Transaction for given RP Account ID not found in Database");
		} else {
			return transactionDetails;
		}
	}
}
