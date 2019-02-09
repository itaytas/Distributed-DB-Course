package com.app.TwoPhaseCommit.logic.transactions.exceptions;

public class InvalidMoneyAmountException extends Exception {

	private static final long serialVersionUID = -3626304419649754048L;

	public InvalidMoneyAmountException() {
		// TODO Auto-generated constructor stub
	}

	public InvalidMoneyAmountException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public InvalidMoneyAmountException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidMoneyAmountException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidMoneyAmountException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
