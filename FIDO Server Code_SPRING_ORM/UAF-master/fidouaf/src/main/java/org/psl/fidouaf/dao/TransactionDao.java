package org.psl.fidouaf.dao;

import org.psl.fidouaf.entity.TransactionDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionDao {
	public void saveTransaction(TransactionDetails transactionDetails);

	public TransactionDetails findTransaction(int rpAccountId);
}
