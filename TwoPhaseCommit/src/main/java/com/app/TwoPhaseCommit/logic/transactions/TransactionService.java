package com.app.TwoPhaseCommit.logic.transactions;

import com.app.TwoPhaseCommit.logic.accounts.exceptions.AccountNotFoundException;
import com.app.TwoPhaseCommit.logic.transaction.exceptionss.jpa.InvalidMoneyAmountException;
import com.app.TwoPhaseCommit.logic.transactions.exceptions.TransactionNotFoundException;

public interface TransactionService {
	
	public void cleanup();
	
	public TransactionEntity createNewTransaction(
			String source,
			String destination,
			double value,
			TransactionState state) throws AccountNotFoundException, InvalidMoneyAmountException;

    public TransactionEntity updateStateOfTransaction(
    		String transactionId,
    		TransactionState fromState,
    		TransactionState toState);

    public TransactionEntity findTransactionByStateAndLastModified(TransactionState state);

	TransactionEntity getTransactionById(String transactionId) throws TransactionNotFoundException;
}
