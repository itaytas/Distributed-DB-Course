package com.app.TwoPhaseCommit.logic.transactions.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.TwoPhaseCommit.dal.primary.TransactionPrimaryDao;
import com.app.TwoPhaseCommit.dal.secondary.TransactionSecondaryDao;
import com.app.TwoPhaseCommit.logic.accounts.AccountsService;
import com.app.TwoPhaseCommit.logic.accounts.exceptions.AccountNotFoundException;
import com.app.TwoPhaseCommit.logic.transaction.exceptionss.jpa.InvalidMoneyAmountException;
import com.app.TwoPhaseCommit.logic.transactions.TransactionEntity;
import com.app.TwoPhaseCommit.logic.transactions.TransactionService;
import com.app.TwoPhaseCommit.logic.transactions.TransactionState;
import com.app.TwoPhaseCommit.logic.transactions.exceptions.SavingTransactionToSecondaryFailedException;
import com.app.TwoPhaseCommit.logic.transactions.exceptions.TransactionNotFoundException;

@Service
public class JpaTransactionService implements TransactionService {
	
	private int NUM_OF_TRYING_TO_SAVE_TO_DB = 5;
	
	private TransactionPrimaryDao transactionPrimaryDao;
	private TransactionSecondaryDao transactionSecondaryDao;
	private AccountsService accountsService;

	@Autowired
	public JpaTransactionService(TransactionPrimaryDao transactionPrimaryDao,
			TransactionSecondaryDao transactionSecondaryDao, 
			AccountsService accountsService) {
		this.transactionPrimaryDao = transactionPrimaryDao;
		this.transactionSecondaryDao = transactionSecondaryDao;
		this.accountsService = accountsService;
	}

	@Override
	@Transactional
	public void cleanup() {
		this.transactionPrimaryDao.deleteAll();
		this.transactionSecondaryDao.deleteAll();
	}

	@Override
	@Transactional
	public TransactionEntity createNewTransaction(
			String source,
			String destination,
			double value,
			TransactionState state) throws Exception {
		
		if (!this.accountsService.isAccountExists(source)) {
			throw new AccountNotFoundException("There is no Account with username: " + source);
		}
		
		if (!this.accountsService.isAccountExists(destination)) {
			throw new AccountNotFoundException("There is no Account with username: " + destination);
		}
		
		if (value < 0) {
			throw new InvalidMoneyAmountException("You can't transfer negative amount of money: " + value);
		}
		
		TransactionEntity transactionEntity = 
				new TransactionEntity(source, destination, value, state, System.currentTimeMillis());

		boolean saveSuccessed = tryToSaveTransactionToSecondary(transactionEntity);
		
		if(!saveSuccessed) {
			throw new SavingTransactionToSecondaryFailedException("Failed to save transaction to Scondary DB:" + transactionEntity.toString());
		}
		
		return this.transactionPrimaryDao.save(transactionEntity);
	}
	
	@Override
	@Transactional(readOnly = true)
	public TransactionEntity getTransactionById(String transactionId) throws TransactionNotFoundException {
		List<TransactionEntity> transactions = this.transactionPrimaryDao.findTransactionById(transactionId);
		if (transactions.size() == 0) {
			throw new TransactionNotFoundException("There is no transaction with Id: " + transactionId);
		}
		return (TransactionEntity) transactions.toArray()[0];
	}
		
	@Override
	@Transactional
	public TransactionEntity updateStateOfTransaction(String transactionId, TransactionState fromState, TransactionState toState) throws Exception {
		TransactionEntity transactionEntity= getTransactionById(transactionId);
		
		transactionEntity.setState(toState);
		transactionEntity.setLastModified(System.currentTimeMillis());
		
		boolean saveSuccessed = tryToSaveTransactionToSecondary(transactionEntity);
		
		if(!saveSuccessed) {
			throw new SavingTransactionToSecondaryFailedException("Failed to save transaction to Scondary DB:" + transactionEntity.toString());
		}
		return this.transactionPrimaryDao.save(transactionEntity);
	}
	
	private boolean tryToSaveTransactionToSecondary(TransactionEntity transactionEntity) {
		int counter = 0;
		while (true) {
			try {
				counter++;
				this.transactionSecondaryDao.save(transactionEntity);
				return true;
			} catch (Exception e) {
				if (counter == NUM_OF_TRYING_TO_SAVE_TO_DB) {
					System.out.println("Error in saving transaction in Secondary DB: timeout");
					return false;
				}
				e.printStackTrace();
				System.out.println("Error in saving transaction in Secondary DB, aborting saving and retrying ...");
			}
		}
	}
}
