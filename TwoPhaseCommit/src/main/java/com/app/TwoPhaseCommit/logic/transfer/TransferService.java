package com.app.TwoPhaseCommit.logic.transfer;

import com.app.TwoPhaseCommit.logic.transactions.TransactionEntity;

public interface TransferService {
	
	public TransactionEntity transfer(TransactionEntity transaction) throws Exception;
	
	//public void recoverPending(TransactionEntity transaction);
	
	//public void recoverApplied(TransactionEntity transaction);
	
	//public void cancelPending(TransactionEntity transaction);
}
