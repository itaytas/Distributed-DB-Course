package com.app.TwoPhaseCommit.api;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.app.TwoPhaseCommit.logic.accounts.AccountsService;

@RestController
public class AccountsRestConroller {
	
	private AccountsService accountsService;
	
	@Autowired
	public void setAccounts(AccountsService accountsService) {
		this.accountsService = accountsService;
	}
	
	@RequestMapping(
			method=RequestMethod.POST,
			path="/accounts",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public AccountTO createNewAccount(@RequestBody AccountTO newAccountTO) throws Exception {	    	
		return new AccountTO(this.accountsService.createNewAccount(newAccountTO.toEntity()));
	}
	
	@RequestMapping(
			method=RequestMethod.GET,
			path="/accounts",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public AccountTO[] getAllAccounts() throws Exception {	    	
		return this.accountsService.getAllAccounts().stream().map(AccountTO::new).collect(Collectors.toList()).toArray(new AccountTO[0]);
	}
	
}
