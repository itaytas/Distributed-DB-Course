package com.app.TwoPhaseCommit.logic.transactions.exceptions;

public class SavingTransactionToSecondaryFailedException extends Exception {

	private static final long serialVersionUID = -2216251723164540927L;

	public SavingTransactionToSecondaryFailedException() {
		// TODO Auto-generated constructor stub
	}

	public SavingTransactionToSecondaryFailedException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public SavingTransactionToSecondaryFailedException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public SavingTransactionToSecondaryFailedException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public SavingTransactionToSecondaryFailedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
