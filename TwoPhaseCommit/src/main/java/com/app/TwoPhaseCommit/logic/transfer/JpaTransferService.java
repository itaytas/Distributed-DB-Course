package com.app.TwoPhaseCommit.logic.transfer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.TwoPhaseCommit.aop.MyLog;
import com.app.TwoPhaseCommit.logic.accounts.AccountsService;
import com.app.TwoPhaseCommit.logic.transactions.TransactionEntity;
import com.app.TwoPhaseCommit.logic.transactions.TransactionService;
import com.app.TwoPhaseCommit.logic.transactions.TransactionState;

@Service
public class JpaTransferService implements TransferService {

	private TransactionService transactionService;
	private AccountsService accountsService;

	public JpaTransferService() {
	}

	@Autowired
	public JpaTransferService(TransactionService transactionService, AccountsService accountsService) {
		super();
		this.transactionService = transactionService;
		this.accountsService = accountsService;
	}

	@MyLog
	@Override
	public TransactionEntity transfer(TransactionEntity transaction) throws Exception {
		this.transactionService.updateStateOfTransaction(
				transaction.getId(),
				TransactionState.INITIAL,
				TransactionState.PENDING);

		this.accountsService.updateBalanceAndPushToPendingTransactions(
				transaction.getSource(),
				-transaction.getValue(),
				transaction.getId());
		
		this.accountsService.updateBalanceAndPushToPendingTransactions(
				transaction.getDestination(),
				transaction.getValue(),
				transaction.getId());

		this.transactionService.updateStateOfTransaction(
				transaction.getId(),
				TransactionState.PENDING,
				TransactionState.APPLIED);

		this.accountsService.updatePullFromPendingTransactions(transaction.getSource(), transaction.getId());
		this.accountsService.updatePullFromPendingTransactions(transaction.getDestination(), transaction.getId());

		this.transactionService.updateStateOfTransaction(
				transaction.getId(),
				TransactionState.APPLIED,
				TransactionState.DONE);
		
		return this.transactionService.getTransactionById(transaction.getId());

	}
	
/*	// Additional methods for strange situations
	@Override
	public void recoverPending(TransactionEntity transaction) {
		this.accountsService.updateBalanceAndPushToPendingTransactions(
				transaction.getSource(),
				-transaction.getValue(),
				transaction.getId());
		
		this.accountsService.updateBalanceAndPushToPendingTransactions(
				transaction.getDestination(),
				transaction.getValue(),
				transaction.getId());

		this.transactionService.updateStateOfTransaction(
				transaction.getId(),
				TransactionState.PENDING,
				TransactionState.APPLIED);

		this.accountsService.updatePullFromPendingTransactions(transaction.getSource(), transaction.getId());
		this.accountsService.updatePullFromPendingTransactions(transaction.getDestination(), transaction.getId());

		this.transactionService.updateStateOfTransaction(transaction.getId(), TransactionState.APPLIED,
				TransactionState.DONE);

	}

	@Override
	public void recoverApplied(TransactionEntity transaction) {
		this.accountsService.updatePullFromPendingTransactions(
				transaction.getSource(),
				transaction.getId());
		
		this.accountsService.updatePullFromPendingTransactions(
				transaction.getDestination(),
				transaction.getId());
		
		this.transactionService.updateStateOfTransaction(
				transaction.getId(),
				TransactionState.APPLIED,
				TransactionState.DONE);
	}

	@Override
	public void cancelPending(TransactionEntity transaction) {
		this.transactionService.updateStateOfTransaction(transaction.getId(), TransactionState.PENDING,
				TransactionState.CANCELING);

		this.accountsService.updateBalanceAndPullFromPendingTransactions(transaction.getSource(),
				transaction.getValue(), transaction.getId());
		this.accountsService.updateBalanceAndPullFromPendingTransactions(transaction.getDestination(),
				-transaction.getValue(), transaction.getId());

		this.transactionService.updateStateOfTransaction(transaction.getId(), TransactionState.CANCELING,
				TransactionState.CANCELED);
	}
*/
}
