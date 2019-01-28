package com.app.TwoPhaseCommit.dal.secondary;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.app.TwoPhaseCommit.logic.transactions.TransactionEntity;

@Repository
public interface TransactionSecondaryDao extends MongoRepository<TransactionEntity, String> {

}
