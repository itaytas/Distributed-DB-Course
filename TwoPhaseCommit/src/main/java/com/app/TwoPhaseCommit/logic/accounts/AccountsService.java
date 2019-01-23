package com.app.TwoPhaseCommit.logic.accounts;

import java.util.List;

import com.app.TwoPhaseCommit.logic.accounts.exceptions.AccountNotFoundException;


public interface AccountsService {
	
	public void cleanup();
	
	public List<AccountEntity> getAllAccounts();
		
	public Object createNewAccount(AccountEntity accountEntity) throws Exception;
	
	public AccountEntity getAccountById(String username) throws AccountNotFoundException;
	
	public Object updateBalanceAndPushToPendingTransactions(String username, double amount, String transactionId);

    public Object updateBalanceAndPullFromPendingTransactions(String username, double amount, String transactionId);
    
    public Object updatePullFromPendingTransactions(String username, String transactionId);

}
