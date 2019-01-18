package com.app.TwoPhaseCommit.logic.accounts.exceptions;

public class AccountAlreadyExistsException extends Exception {

	private static final long serialVersionUID = -924407531800272681L;

	public AccountAlreadyExistsException() {
		// TODO Auto-generated constructor stub
	}

	public AccountAlreadyExistsException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public AccountAlreadyExistsException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public AccountAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public AccountAlreadyExistsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
