package com.app.TwoPhaseCommit.logic.accounts.jpa;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.TwoPhaseCommit.dal.AccountsPrimaryDao;
import com.app.TwoPhaseCommit.dal.AccountsSecondaryDao;
import com.app.TwoPhaseCommit.logic.accounts.AccountEntity;
import com.app.TwoPhaseCommit.logic.accounts.AccountsService;
import com.app.TwoPhaseCommit.logic.accounts.exceptions.AccountAlreadyExistsException;
import com.app.TwoPhaseCommit.logic.accounts.exceptions.AccountNotFoundException;

@Service
public class JpaAccountsService implements AccountsService {

	private AccountsPrimaryDao accountsPrimaryDao;
	private AccountsSecondaryDao accountsSecondaryDao;
//	private ApplicationContext spring;

	@Autowired
	public JpaAccountsService(AccountsPrimaryDao accountsPrimaryDao, AccountsSecondaryDao accountsSecondaryDao,
			ApplicationContext spring) {
		this.accountsPrimaryDao = accountsPrimaryDao;
		this.accountsSecondaryDao = accountsSecondaryDao;
//		this.spring = spring;
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
	public Object createNewAccount(AccountEntity accountEntity) throws Exception {
		if (this.accountsPrimaryDao.findAccountById(accountEntity.getUsername()).size() != 0) {
			throw new AccountAlreadyExistsException(
					"There is already an account with username: " + accountEntity.getUsername());
		}

		while (true) {
			try {
				this.accountsSecondaryDao.save(accountEntity);
				break;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error in save account in Secondary DB, aborting saving and retrying ...");
			}
		}

		return this.accountsPrimaryDao.save(accountEntity);
	}

	@Override
	@Transactional(readOnly = true)
	public AccountEntity getAccountById(String username) throws AccountNotFoundException {
		List<AccountEntity> accounts = this.accountsPrimaryDao.findAccountById(username);
		if (accounts.size() == 0) {
			throw new AccountNotFoundException("There is no account with username: " + username);
		}
		return (AccountEntity) accounts.toArray()[0];
	}

	@Override
	@Transactional
	public Object updateBalanceAndPushToPendingTransactions(String username, int amount, String transactionId) {
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
				System.out.println("Error in save account in Secondary DB, aborting saving and retrying ...");
			}
		}

		return this.accountsPrimaryDao.save(accountEntity);
	}

	@Override
	@Transactional
	public Object updateBalanceAndPullFromPendingTransactions(String username, int amount, String transactionId) {
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
				System.out.println("Error in save account in Secondary DB, aborting saving and retrying ...");
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
				System.out.println("Error in save account in Secondary DB, aborting saving and retrying ...");
			}
		}

		return this.accountsPrimaryDao.save(accountEntity);
	}

}
