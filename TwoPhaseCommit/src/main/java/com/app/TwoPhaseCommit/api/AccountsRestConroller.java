package com.app.TwoPhaseCommit.api;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.app.TwoPhaseCommit.logic.accounts.AccountsService;

@RestController
@RequestMapping("/api/account")
public class AccountsRestConroller {
	
	private AccountsService accountsService;
	
	@Autowired
	public void setAccounts(AccountsService accountsService) {
		this.accountsService = accountsService;
	}
	
	@RequestMapping(
			method=RequestMethod.POST,
			path="/new",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public AccountTO createNewAccount(@RequestBody AccountTO newAccountTO) throws Exception {	    	
		return new AccountTO(this.accountsService.createNewAccount(newAccountTO.toEntity()));
	}
	
	@RequestMapping(
			method=RequestMethod.GET,
			path="/login/{username}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public AccountTO login(@PathVariable String username) throws Exception {	    	
		return new AccountTO(this.accountsService.getAccountById(username));
	}
	
	@RequestMapping(
			method=RequestMethod.GET,
			path="/community/{username}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public AccountTO[] getCommunity(@PathVariable String username) throws Exception {	    	
		return this.accountsService.getCommunity(username)
													.stream()
													.map(AccountTO::new)
													.collect(Collectors.toList())												
													.toArray(new AccountTO[0]);
	}
	
	@RequestMapping(
			method=RequestMethod.GET,
			path="/details/{username}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public AccountTO whoAmI(@PathVariable String username) throws Exception {	    	
		return new AccountTO(this.accountsService.getAccountById(username));
	}
	
}
