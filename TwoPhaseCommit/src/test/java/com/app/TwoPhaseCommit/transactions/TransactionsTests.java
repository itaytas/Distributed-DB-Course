package com.app.TwoPhaseCommit.transactions;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import javax.annotation.PostConstruct;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.app.TwoPhaseCommit.api.AccountTO;
import com.app.TwoPhaseCommit.dal.primary.TransactionPrimaryDao;
import com.app.TwoPhaseCommit.dal.secondary.TransactionSecondaryDao;
import com.app.TwoPhaseCommit.logic.accounts.AccountEntity;
import com.app.TwoPhaseCommit.logic.accounts.AccountsService;
import com.app.TwoPhaseCommit.logic.accounts.exceptions.AccountNotFoundException;
import com.app.TwoPhaseCommit.logic.transactions.TransactionEntity;
import com.app.TwoPhaseCommit.logic.transactions.TransactionService;
import com.app.TwoPhaseCommit.logic.transactions.TransactionState;
import com.app.TwoPhaseCommit.logic.transactions.exceptions.InvalidMoneyAmountException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TransactionsTests {
	
	@LocalServerPort
	private int port;
	private String url;
	private RestTemplate restTemplate;
	private ObjectMapper jsonMapper;
	
	@Autowired
	private AccountsService accountsService;
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private TransactionPrimaryDao transactionPrimaryDao;
	
	@Autowired
	private TransactionSecondaryDao transactionSecondaryDao;
	
	private AccountEntity account1;
	private AccountEntity account2;
	private AccountEntity account3;
	
	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.jsonMapper = new ObjectMapper();

		this.url = "http://localhost:" + port + "/api/transfer/{source}/{destination}/{value}";
		System.err.println(this.url);
	}

	@Before
	public void setup() throws Exception {
		// creating 3 Accounts
		AccountTO accountTO1 = new AccountTO("username1", 1000.0, null);
		AccountTO accountTO2 = new AccountTO("username2", 1100.0, null);
		AccountTO accountTO3 = new AccountTO("username3", 1200.0, null);
				
		this.account1 = this.accountsService.createNewAccount(accountTO1.toEntity());
		this.account2 = this.accountsService.createNewAccount(accountTO2.toEntity());
		this.account3 = this.accountsService.createNewAccount(accountTO3.toEntity());

	}

	@After
	public void teardown() {
		this.accountsService.cleanup();
		this.transactionService.cleanup();
	}

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testServerIsBootingCorrectly() throws Exception {
	}
	
	@Test
	public void testCreateTransactionSuccessfully() throws Exception {
		TransactionEntity expectedTransaction = 
				this.transactionService.createNewTransaction(
						this.account1.getUsername(),
						this.account2.getUsername(),
						100, TransactionState.INITIAL);
		
		
		Optional<TransactionEntity> opPrimary = this.transactionPrimaryDao.findById(expectedTransaction.getId());
		Optional<TransactionEntity> opSecondary = this.transactionSecondaryDao.findById(expectedTransaction.getId());
				
		assertThat(opPrimary.get())
		.isNotNull()
		.isEqualTo(opSecondary.get())
		.isEqualTo(expectedTransaction);
	}
	
	@Test
	public void testCreateTransactionWithNegativeMoneyValue() throws Exception {
		this.exception.expect(InvalidMoneyAmountException.class);
		
		TransactionEntity expectedTransaction = 
				this.transactionService.createNewTransaction(
						this.account1.getUsername(),
						this.account2.getUsername(),
						-100, TransactionState.INITIAL);
	}
	
	@Test
	public void testCreateTransactionFromUnknownSource() throws Exception {
		this.exception.expect(AccountNotFoundException.class);
		
		TransactionEntity expectedTransaction = 
				this.transactionService.createNewTransaction(
						"UnknownSourceUsername",
						this.account2.getUsername(),
						-100, TransactionState.INITIAL);
	}
	
	@Test
	public void testCreateTransactionToUnknownDestination() throws Exception {
		this.exception.expect(AccountNotFoundException.class);
		
		TransactionEntity expectedTransaction = 
				this.transactionService.createNewTransaction(
						"UnknownDestinationUsername",
						this.account2.getUsername(),
						-100, TransactionState.INITIAL);
	}
	
}
