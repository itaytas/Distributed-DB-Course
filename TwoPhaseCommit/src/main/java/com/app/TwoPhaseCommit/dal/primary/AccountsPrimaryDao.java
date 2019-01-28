package com.app.TwoPhaseCommit.dal.primary;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.app.TwoPhaseCommit.logic.accounts.AccountEntity;

@Repository
public interface AccountsPrimaryDao extends MongoRepository<AccountEntity, String> {

	Optional<AccountEntity> findById(String username);

}
