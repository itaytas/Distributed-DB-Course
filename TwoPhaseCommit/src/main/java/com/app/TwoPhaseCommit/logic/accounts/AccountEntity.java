package com.app.TwoPhaseCommit.logic.accounts;

import java.util.Arrays;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Accounts")
public class AccountEntity {

	private String username;
	private long balance;
	private Object[] pendingTransactions;

	public AccountEntity() {
	}

	public AccountEntity(String username, long balance, Object[] pendingTransactions) {
		super();
		this.username = username;
		this.balance = balance;
		this.pendingTransactions = pendingTransactions;
	}

	@Id
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getBalance() {
		return balance;
	}

	public void setBalance(long balance) {
		this.balance = balance;
	}

	public Object[] getPendingTransactions() {
		return pendingTransactions;
	}

	public void setPendingTransactions(Object[] pendingTransactions) {
		this.pendingTransactions = pendingTransactions;
	}

	@Override
	public String toString() {
		return "Account [balance=" + balance + ", pendingTransactions=" + Arrays.toString(pendingTransactions) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (balance ^ (balance >>> 32));
		result = prime * result + Arrays.deepHashCode(pendingTransactions);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AccountEntity other = (AccountEntity) obj;
		if (balance != other.balance)
			return false;
		if (!Arrays.deepEquals(pendingTransactions, other.pendingTransactions))
			return false;
		return true;
	}

}
