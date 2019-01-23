package com.app.TwoPhaseCommit.dal.primary;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.app.TwoPhaseCommit.logic.accounts.AccountEntity;

public interface AccountsPrimaryDao extends MongoRepository<AccountEntity, String> {

	List<AccountEntity> findAccountById(String username);

}
