package com.app.TwoPhaseCommit.dal.primary;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.app.TwoPhaseCommit.logic.transactions.TransactionEntity;

public interface TransactionPrimaryDao extends MongoRepository<TransactionEntity, String> {

	List<TransactionEntity> findTransactionById(String transactionId);

}
