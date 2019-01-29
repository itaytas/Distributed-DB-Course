package com.app.TwoPhaseCommit.api;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.app.TwoPhaseCommit.logic.transactions.TransactionService;
import com.app.TwoPhaseCommit.logic.transactions.TransactionState;

@RestController
public class TransactionRestController {
	
	private TransactionService transactionService;
	
	@Autowired
	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}
	
	@RequestMapping(
			method=RequestMethod.GET,
			path="/transfer/{source}/{destination}/{value}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public Object getAllAccounts(
			@PathVariable("source") String source,
			@PathVariable("destination") String destination,
			@PathVariable("value") String value) throws Exception {	    	
		return this.transactionService.createNewTransaction(
				source, destination, Double.parseDouble(value), TransactionState.INITIAL);
	}
}
