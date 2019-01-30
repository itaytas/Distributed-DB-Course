package com.app.TwoPhaseCommit.dal.primary;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.TwoPhaseCommit.logic.transactions.TransactionEntity;

@Repository
public interface TransactionPrimaryDao extends MongoRepository<TransactionEntity, String> {

	List<TransactionEntity> findTransactionById(@Param("transactionId") String transactionId);

}
