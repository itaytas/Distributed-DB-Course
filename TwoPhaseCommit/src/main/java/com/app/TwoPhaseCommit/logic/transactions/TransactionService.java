package com.app.TwoPhaseCommit.logic.transactions;

import com.app.TwoPhaseCommit.logic.transactions.exceptions.TransactionNotFoundException;

public interface TransactionService {
	
	public void cleanup();
	
	public TransactionEntity createNewTransaction(
			String source,
			String destination,
			double value,
			TransactionState state);

    public TransactionEntity updateStateOfTransaction(
    		String transactionId,
    		TransactionState fromState,
    		TransactionState toState);

    public TransactionEntity findTransactionByStateAndLastModified(TransactionState state);

	TransactionEntity getTransactionById(String transactionId) throws TransactionNotFoundException;
}
