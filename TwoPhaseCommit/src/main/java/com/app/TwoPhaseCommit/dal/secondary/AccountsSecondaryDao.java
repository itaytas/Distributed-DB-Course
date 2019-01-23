package com.app.TwoPhaseCommit.dal.secondary;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.app.TwoPhaseCommit.logic.accounts.AccountEntity;

public interface AccountsSecondaryDao extends MongoRepository<AccountEntity, String> {

}
