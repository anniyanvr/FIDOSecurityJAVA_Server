package org.psl.fidouaf.service;

import org.psl.fidouaf.entity.TransactionDetails;
import org.psl.fidouaf.exceptions.DataRecordNotFoundException;
import org.springframework.stereotype.Service;

@Service
public interface TransactionsService {
	public void saveTransaction(TransactionDetails transactionDetails);

	public TransactionDetails findTransaction(int rpAccountId)
			throws DataRecordNotFoundException;
}
