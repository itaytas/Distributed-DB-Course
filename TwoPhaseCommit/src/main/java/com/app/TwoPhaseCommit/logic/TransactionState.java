package com.app.TwoPhaseCommit.logic;

public enum TransactionState {
	INITIAL, PENDING, APPLIED, DONE, CANCELING, CANCELED
};