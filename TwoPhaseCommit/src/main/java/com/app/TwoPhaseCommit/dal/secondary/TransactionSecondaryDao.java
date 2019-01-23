package com.app.TwoPhaseCommit.dal.secondary;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.app.TwoPhaseCommit.logic.transactions.TransactionEntity;

public interface TransactionSecondaryDao extends MongoRepository<TransactionEntity, String> {

}
