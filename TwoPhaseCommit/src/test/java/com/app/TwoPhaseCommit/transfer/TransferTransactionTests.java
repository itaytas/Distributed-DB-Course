package com.app.TwoPhaseCommit.transfer;

import static org.assertj.core.api.Assertions.assertThat;

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
import com.app.TwoPhaseCommit.api.TransactionTO;
import com.app.TwoPhaseCommit.dal.primary.AccountsPrimaryDao;
import com.app.TwoPhaseCommit.dal.primary.TransactionPrimaryDao;
import com.app.TwoPhaseCommit.dal.secondary.AccountsSecondaryDao;
import com.app.TwoPhaseCommit.dal.secondary.TransactionSecondaryDao;
import com.app.TwoPhaseCommit.logic.accounts.AccountEntity;
import com.app.TwoPhaseCommit.logic.accounts.AccountsService;
import com.app.TwoPhaseCommit.logic.transactions.TransactionService;
import com.app.TwoPhaseCommit.logic.transfer.TransferService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TransferTransactionTests {

	@LocalServerPort
	private int port;
	private String url;
	private RestTemplate restTemplate;
	private ObjectMapper jsonMapper;

	@Autowired
	private AccountsService accountsService;
	
	@Autowired
	private AccountsPrimaryDao accountsPrimaryDao;

	@Autowired
	private AccountsSecondaryDao accountsSecondaryDao;
	
	@Autowired
	private TransferService transferService; 
	
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

		this.url = "http://localhost:" + port + "/transfer/{source}/{destination}/{value}";
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
	public void testTransferMoneysuccessfully() throws Exception {
		// Before: account1.balance = 1000.0, account2.balance = 1100.0
		TransactionTO expectedTransaction = 
				this.restTemplate.getForObject(
						this.url,
						TransactionTO.class,
						this.account1.getUsername(),
						this.account2.getUsername(),
						100);
				
		// After: account1.balance = 900.0, account2.balance = 1200.0
		assertThat(this.accountsPrimaryDao.findById(this.account1.getUsername()).get().getBalance())
		.isEqualTo(900.0)
		.isEqualTo(this.accountsSecondaryDao.findById(this.account1.getUsername()).get().getBalance());
		
		assertThat(this.accountsPrimaryDao.findById(this.account2.getUsername()).get().getBalance())
		.isEqualTo(1200.0)
		.isEqualTo(this.accountsSecondaryDao.findById(this.account2.getUsername()).get().getBalance());
		
		assertThat(this.accountsPrimaryDao.findById(this.account1.getUsername()).get().getPendingTransactions())
		.isEqualTo(this.accountsSecondaryDao.findById(this.account1.getUsername()).get().getPendingTransactions())
		.isEmpty();
		
		assertThat(this.accountsPrimaryDao.findById(this.account2.getUsername()).get().getPendingTransactions())
		.isEqualTo(this.accountsSecondaryDao.findById(this.account2.getUsername()).get().getPendingTransactions())
		.isEmpty();
		
	}
	
}
