package com.app.TwoPhaseCommit.logic.accounts.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.TwoPhaseCommit.dal.primary.AccountsPrimaryDao;
import com.app.TwoPhaseCommit.dal.secondary.AccountsSecondaryDao;
import com.app.TwoPhaseCommit.logic.accounts.AccountEntity;
import com.app.TwoPhaseCommit.logic.accounts.AccountsService;
import com.app.TwoPhaseCommit.logic.accounts.exceptions.AccountAlreadyExistsException;
import com.app.TwoPhaseCommit.logic.accounts.exceptions.AccountNotFoundException;
import com.app.TwoPhaseCommit.logic.accounts.exceptions.SavingAccountToSecondaryFailedException;
import com.app.TwoPhaseCommit.logic.transactions.TransactionEntity;
import com.app.TwoPhaseCommit.logic.transactions.exceptions.SavingTransactionToSecondaryFailedException;

@Service
public class JpaAccountsService implements AccountsService {
	
	private int NUM_OF_TRYING_TO_SAVE_TO_DB = 5;
	
	private AccountsPrimaryDao accountsPrimaryDao;
	private AccountsSecondaryDao accountsSecondaryDao;

	@Autowired
	public JpaAccountsService(AccountsPrimaryDao accountsPrimaryDao, AccountsSecondaryDao accountsSecondaryDao) {
		this.accountsPrimaryDao = accountsPrimaryDao;
		this.accountsSecondaryDao = accountsSecondaryDao;
	}

	@Override
	@Transactional
	public void cleanup() {
		this.accountsPrimaryDao.deleteAll();
		this.accountsSecondaryDao.deleteAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountEntity> getAllAccounts() {
		List<AccountEntity> allList = new ArrayList<>();
		this.accountsPrimaryDao.findAll().forEach(o -> allList.add(o));
		return allList;
	}

	@Override
	@Transactional
	public AccountEntity createNewAccount(AccountEntity accountEntity) throws Exception {
		if (isAccountExists(accountEntity.getUsername())) {
			throw new AccountAlreadyExistsException(
					"There is already an account with username: " + accountEntity.getUsername());
		} else {
			boolean saveSuccessed = tryToSaveAccountToSecondary(accountEntity);
			
			if(!saveSuccessed) {
				throw new SavingAccountToSecondaryFailedException("Failed to save account to Scondary DB:" + accountEntity.toString());
			}

			return this.accountsPrimaryDao.save(accountEntity);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public AccountEntity getAccountById(String username) throws AccountNotFoundException {
		Optional<AccountEntity> op = this.accountsPrimaryDao.findById(username);
		if (!op.isPresent()) {
			throw new AccountNotFoundException(
					"Not found an account with username: " + username);
		}
		return op.get();
	}
	
	@Override
	public boolean isAccountExists(String username) {
		Optional<AccountEntity> op = this.accountsPrimaryDao.findById(username);
		return op.isPresent();
	}

	@Override
	@Transactional
	public Object updateBalanceAndPushToPendingTransactions(String username, double amount, String transactionId) throws Exception {
		AccountEntity accountEntity= getAccountById(username);
		

		accountEntity.setBalance(accountEntity.getBalance() + amount);
		accountEntity.addToPendingTransactions(transactionId);

		boolean saveSuccessed = tryToSaveAccountToSecondary(accountEntity);
		
		if(!saveSuccessed) {
			throw new SavingAccountToSecondaryFailedException("Failed to save account to Scondary DB:" + accountEntity.toString());
		}

		return this.accountsPrimaryDao.save(accountEntity);
	}

	@Override
	@Transactional
	public Object updateBalanceAndPullFromPendingTransactions(String username, double amount, String transactionId) throws Exception {
		AccountEntity accountEntity = getAccountById(username);
		

		accountEntity.setBalance(accountEntity.getBalance() + amount);
		accountEntity.removePendingTransaction(transactionId);

		boolean saveSuccessed = tryToSaveAccountToSecondary(accountEntity);
		
		if(!saveSuccessed) {
			throw new SavingAccountToSecondaryFailedException("Failed to save account to Scondary DB:" + accountEntity.toString());
		}

		return this.accountsPrimaryDao.save(accountEntity);
	}

	@Override
	@Transactional
	public Object updatePullFromPendingTransactions(String username, String transactionId) throws Exception{
		AccountEntity accountEntity = getAccountById(username);

		accountEntity.removePendingTransaction(transactionId);

		boolean saveSuccessed = tryToSaveAccountToSecondary(accountEntity);
		
		if(!saveSuccessed) {
			throw new SavingAccountToSecondaryFailedException("Failed to save account to Scondary DB:" + accountEntity.toString());
		}

		return this.accountsPrimaryDao.save(accountEntity);
	}
	
	private boolean tryToSaveAccountToSecondary(AccountEntity accountEntity) {
		int counter = 0;
		while (true) {
			try {
				counter++;
				this.accountsSecondaryDao.save(accountEntity);
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
