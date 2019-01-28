package com.app.TwoPhaseCommit.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.app.TwoPhaseCommit.logic.accounts.AccountsService;

@RestController
public class AccountsRestConroller {
	
	@Autowired
	private AccountsService accountsService;
	
	
//	public void setAccounts(AccountsService accountsService) {
//		this.accountsService = accountsService;
//	}
	
	@RequestMapping(
			method=RequestMethod.GET,
			path="/accounts",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public Object addNewActivity() throws Exception {	    	
		return this.accountsService.getAllAccounts();
	}
	
}
