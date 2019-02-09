package com.app.TwoPhaseCommit.api;

import java.util.ArrayList;

import com.app.TwoPhaseCommit.logic.accounts.AccountEntity;

public class AccountTO {

	private String username;
	private double balance;
	private ArrayList<Object> pendingTransactions;

	public AccountTO() {
	}

	public AccountTO(String username, double balance, ArrayList<Object> pendingTransactions) {
		this.username = username;
		this.balance = balance;
		if (pendingTransactions != null) {
			this.pendingTransactions = pendingTransactions;
		}
		else {
			this.pendingTransactions = new ArrayList<Object>();
		}
		
	}

	public AccountTO(AccountEntity entity) {
		this(entity.getUsername(), entity.getBalance(), entity.getPendingTransactions());
	}

	public AccountEntity toEntity() {
		return new AccountEntity(this.username, this.balance, this.pendingTransactions);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public ArrayList<Object> getPendingTransactions() {
		return pendingTransactions;
	}

	public void setPendingTransactions(ArrayList<Object> pendingTransactions) {
		this.pendingTransactions = pendingTransactions;
	}

	@Override
	public String toString() {
		return "AccountTO [username=" + username + ", balance=" + balance + ", pendingTransactions="
				+ pendingTransactions + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(balance);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((pendingTransactions == null) ? 0 : pendingTransactions.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		AccountTO other = (AccountTO) obj;
		if (Double.doubleToLongBits(balance) != Double.doubleToLongBits(other.balance))
			return false;
		if (pendingTransactions == null) {
			if (other.pendingTransactions != null)
				return false;
		} else if (!pendingTransactions.equals(other.pendingTransactions))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

}
