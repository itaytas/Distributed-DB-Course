package com.app.TwoPhaseCommit.dal.secondary;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.app.TwoPhaseCommit.logic.accounts.AccountEntity;

@Repository
public interface AccountsSecondaryDao extends MongoRepository<AccountEntity, String> {

}
