package com.app.TwoPhaseCommit.dal;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.app.TwoPhaseCommit.logic.accounts.AccountEntity;

public interface AccountsSecondaryDao extends MongoRepository<AccountEntity, String> {

}
