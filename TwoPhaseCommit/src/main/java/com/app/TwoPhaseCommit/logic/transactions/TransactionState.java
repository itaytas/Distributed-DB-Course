package com.app.TwoPhaseCommit.logic.transactions;

/**
 * Successful Flow
 * INITIAL -> PENDING -> APPLIED -> DONE
 * 
 * Failure Flow:
 * INITIAL -> PENDING -> APPLIED -> CANCELLING -> CANCELLED
*/
public enum TransactionState {
	INITIAL, PENDING, APPLIED, DONE, CANCELING, CANCELED
};