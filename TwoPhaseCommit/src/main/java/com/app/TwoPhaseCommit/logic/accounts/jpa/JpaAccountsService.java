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

@Service
public class JpaAccountsService implements AccountsService {

//	@Autowired
	private AccountsPrimaryDao accountsPrimaryDao;
//	@Autowired
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
		Optional<AccountEntity> op = this.accountsPrimaryDao.findById(accountEntity.getUsername());
		if (op.isPresent()) {
			throw new AccountAlreadyExistsException(
					"There is already an account with username: " + accountEntity.getUsername());
		} else {
			while (true) {
				try {
					this.accountsSecondaryDao.save(accountEntity);
					break;
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Error in saving account in Secondary DB, aborting saving and retrying ...");
				}
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
	@Transactional
	public Object updateBalanceAndPushToPendingTransactions(String username, double amount, String transactionId) {
		AccountEntity accountEntity;
		try {
			accountEntity = getAccountById(username);
		} catch (AccountNotFoundException e) {
			e.printStackTrace();
			return null;
		}

		accountEntity.setBalance(accountEntity.getBalance() + amount);
		accountEntity.addToPendingTransactions(transactionId);

		while (true) {
			try {
				this.accountsSecondaryDao.save(accountEntity);
				break;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error in saving account in Secondary DB, aborting saving and retrying ...");
			}
		}

		return this.accountsPrimaryDao.save(accountEntity);
	}

	@Override
	@Transactional
	public Object updateBalanceAndPullFromPendingTransactions(String username, double amount, String transactionId) {
		AccountEntity accountEntity;
		try {
			accountEntity = getAccountById(username);
		} catch (AccountNotFoundException e) {
			e.printStackTrace();
			return null;
		}

		accountEntity.setBalance(accountEntity.getBalance() + amount);
		accountEntity.removePendingTransaction(transactionId);

		while (true) {
			try {
				this.accountsSecondaryDao.save(accountEntity);
				break;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error in saving account in Secondary DB, aborting saving and retrying ...");
			}
		}

		return this.accountsPrimaryDao.save(accountEntity);
	}

	@Override
	@Transactional
	public Object updatePullFromPendingTransactions(String username, String transactionId) {
		AccountEntity accountEntity;
		try {
			accountEntity = getAccountById(username);
		} catch (AccountNotFoundException e) {
			e.printStackTrace();
			return null;
		}

		accountEntity.removePendingTransaction(transactionId);

		while (true) {
			try {
				this.accountsSecondaryDao.save(accountEntity);
				break;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error in saving account in Secondary DB, aborting saving and retrying ...");
			}
		}

		return this.accountsPrimaryDao.save(accountEntity);
	}

}
