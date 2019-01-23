package com.app.TwoPhaseCommit.logic.transactions.exceptions;

public class TransactionNotFoundException extends Exception {

	private static final long serialVersionUID = -6084564471602587476L;

	public TransactionNotFoundException() {
		// TODO Auto-generated constructor stub
	}

	public TransactionNotFoundException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public TransactionNotFoundException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public TransactionNotFoundException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public TransactionNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
