package com.app.TwoPhaseCommit.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.app.TwoPhaseCommit.logic.transactions.TransactionService;
import com.app.TwoPhaseCommit.logic.transactions.TransactionState;
import com.app.TwoPhaseCommit.logic.transfer.TransferService;

@RestController
@RequestMapping("/api/transfer")
public class TransferTransactionRestController {
	
	private TransactionService transactionService;
	private TransferService transferService;
	
	@Autowired
	public TransferTransactionRestController(TransactionService transactionService, TransferService transferService) {
		this.transactionService = transactionService;
		this.transferService = transferService;
	}
	
	@RequestMapping(
			method=RequestMethod.GET,
			path="/{source}/{destination}/{value}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public TransactionTO transfer(
			@PathVariable("source") String source,
			@PathVariable("destination") String destination,
			@PathVariable("value") String value) throws Exception {	    	
		return new TransactionTO(
				this.transferService.transfer(
						this.transactionService.createNewTransaction(
											source,
											destination,	
											Double.parseDouble(value),
											TransactionState.INITIAL)));
	}

}
