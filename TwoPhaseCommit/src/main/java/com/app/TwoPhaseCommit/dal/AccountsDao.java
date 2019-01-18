package com.app.TwoPhaseCommit.dal;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.app.TwoPhaseCommit.logic.accounts.AccountEntity;

public interface AccountsDao extends CrudRepository<AccountEntity, String> {

	List<AccountEntity> findAccountByid(String username);

}
