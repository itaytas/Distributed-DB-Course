package com.app.TwoPhaseCommit.logic.accounts.jpa;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.TwoPhaseCommit.dal.AccountsDao;
import com.app.TwoPhaseCommit.logic.accounts.AccountEntity;
import com.app.TwoPhaseCommit.logic.accounts.AccountsService;
import com.app.TwoPhaseCommit.logic.accounts.exceptions.AccountAlreadyExistsException;
import com.app.TwoPhaseCommit.logic.accounts.exceptions.AccountNotFoundException;

@Service
public class JpaAccountsService implements AccountsService {

	private AccountsDao accountsDao;
	private ApplicationContext spring;

	@Autowired
	public JpaAccountsService(AccountsDao accountsDao, ApplicationContext spring) {
		this.accountsDao = accountsDao;
		this.spring = spring;
	}

	@Override
	@Transactional
	public void cleanup() {
		this.accountsDao.deleteAll();
	}

	/**
	 * This method wasn't implemented with pagination therefore we bring all
	 * accounts from DB.
	 */
	@Override
	@Transactional(readOnly=true)
	public List<AccountEntity> getAllAccounts() {
		List<AccountEntity> allList = new ArrayList<>();
		this.accountsDao.findAll().forEach(o -> allList.add(o));
		return allList;
	}

	@Override
	@Transactional
	public Object createNewAccount(AccountEntity accountEntity) throws Exception {
		if(this.accountsDao
				.findAccountByid(accountEntity.getUsername())
				.size() != 0) {
			throw new AccountAlreadyExistsException("There is already an account with username: " 
				+ accountEntity.getUsername());
		}
		
		return this.accountsDao.save(accountEntity);
	}

	@Override
	@Transactional(readOnly=true)
	public AccountEntity getAccountById(String username) throws AccountNotFoundException {
		// TODO Auto-generated method stub
		List<AccountEntity> accounts = this.accountsDao.findAccountByid(username);
		if (accounts.size() == 0) {
			throw new AccountNotFoundException("There is no account with username: " + username);
		}
		return (AccountEntity) accounts.toArray()[0];
	}

	@Override
	@Transactional
	public Object updateBalanceAndPushToPendingTransactions(String username, int amount, String transactionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public Object updateBalanceAndPullFromPendingTransactions(String username, int amount, String transactionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public Object updatePullFromPendingTransactions(String accountId, String transactionId) {
		// TODO Auto-generated method stub
		return null;
	}

}
