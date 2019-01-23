package com.app.TwoPhaseCommit.logic.transactions.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.app.TwoPhaseCommit.dal.primary.TransactionPrimaryDao;
import com.app.TwoPhaseCommit.dal.secondary.TransactionSecondaryDao;
import com.app.TwoPhaseCommit.logic.transactions.TransactionEntity;
import com.app.TwoPhaseCommit.logic.transactions.TransactionService;
import com.app.TwoPhaseCommit.logic.transactions.TransactionState;
import com.app.TwoPhaseCommit.logic.transactions.exceptions.TransactionNotFoundException;

public class JpaTransactionService implements TransactionService {

	private TransactionPrimaryDao transactionPrimaryDao;
	private TransactionSecondaryDao transactionSecondaryDao;

	@Autowired
	public JpaTransactionService(TransactionPrimaryDao transactionPrimaryDao,
			TransactionSecondaryDao transactionSecondaryDao) {
		this.transactionPrimaryDao = transactionPrimaryDao;
		this.transactionSecondaryDao = transactionSecondaryDao;
	}

	@Override
	@Transactional
	public void cleanup() {
		this.transactionPrimaryDao.deleteAll();
		this.transactionSecondaryDao.deleteAll();
	}

	@Override
	@Transactional
	public TransactionEntity createNewTransaction(String source, String destination, double value,
			TransactionState state) {
		TransactionEntity transactionEntity = new TransactionEntity(source, destination, value, state, System.currentTimeMillis());

		tryToSaveEntityToSecondary(transactionEntity);

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
	public TransactionEntity updateStateOfTransaction(String transactionId, TransactionState fromState, TransactionState toState) {
		TransactionEntity transactionEntity;
		try {
			transactionEntity = getTransactionById(transactionId);
		} catch (TransactionNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		transactionEntity.setState(toState);
		transactionEntity.setLastModified(System.currentTimeMillis());
		
		tryToSaveEntityToSecondary(transactionEntity);

		return this.transactionPrimaryDao.save(transactionEntity);
	}

	

	@Override
	@Transactional(readOnly = true)
	public TransactionEntity findTransactionByStateAndLastModified(TransactionState state) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	private void tryToSaveEntityToSecondary(TransactionEntity transactionEntity) {
		while (true) {
			try {
				this.transactionSecondaryDao.save(transactionEntity);
				break;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error in saving transaction in Secondary DB, aborting saving and retrying ...");
			}
		}
	}
}
