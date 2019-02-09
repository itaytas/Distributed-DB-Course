package com.app.TwoPhaseCommit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

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
public class ApplicationTests {
	
	private final String CREATE_NEW_USER = "/api/account/new";
	private final String LOGIN = "/api/account/login/{username}";
	private final String GET_COMMUNITY = "/api/account/community/{username}";
	private final String WHO_AM_I = "/api/account/details/{username}";
	private final String TRANSFER_MONEY = "/api/transfer/{source}/{destination}/{value}";
	
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

	private AccountTO accountTO1;
	private AccountTO accountTO2;
	private AccountTO accountTO3;
	
	private AccountEntity account1;
	private AccountEntity account2;
	private AccountEntity account3;

	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.jsonMapper = new ObjectMapper();

		this.url = "http://localhost:" + port;
		System.err.println(this.url);
	}

	@Before
	public void setup() throws Exception {
		// creating 3 Accounts
		this.accountTO1 = new AccountTO("username1", 1000.0, null);
		this.accountTO2 = new AccountTO("username2", 1100.0, null);
		this.accountTO3 = new AccountTO("username3", 1200.0, null);

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
	public void testSuccessfullSenario() throws Exception {
		/**
		 * 1) Create an account 
		 * 2) Login with the account we've just created
		 * 3) Check to who we can transfer money
		 * 4) Choose one account to transfer money
		 * 5) Check our balance
		 */
		
		AccountTO itayTO = new AccountTO("Itay", 1000.0, null);
		AccountTO actualItayThatCreated = 
				this.restTemplate.postForObject(this.url + CREATE_NEW_USER, itayTO, AccountTO.class);
		AccountTO actualItayLogin = 
				this.restTemplate.getForObject(this.url + LOGIN, AccountTO.class, itayTO.getUsername());

		assertThat(actualItayThatCreated)
		.isEqualTo(itayTO)
		.isEqualTo(actualItayLogin);
		
		AccountTO[] accountsList = 
				this.restTemplate.getForObject(this.url + GET_COMMUNITY, AccountTO[].class, itayTO.getUsername());


		assertThat(accountsList).doesNotContain(itayTO);
		assertThat(accountsList).contains(this.accountTO1, this.accountTO2, this.accountTO3);

		TransactionTO expectedTransaction = 
				this.restTemplate.getForObject(
						this.url + TRANSFER_MONEY,
						TransactionTO.class,
						itayTO.getUsername(),
						this.account1.getUsername(),
						100);
		
		assertThat(this.accountsPrimaryDao.findById(itayTO.getUsername()).get().getBalance())
		.isEqualTo(900.0)
		.isEqualTo(this.accountsSecondaryDao.findById(itayTO.getUsername()).get().getBalance());
		
		assertThat(this.accountsPrimaryDao.findById(this.account1.getUsername()).get().getBalance())
		.isEqualTo(1100.0)
		.isEqualTo(this.accountsSecondaryDao.findById(this.account1.getUsername()).get().getBalance());
		
		assertThat(this.accountsPrimaryDao.findById(itayTO.getUsername()).get().getPendingTransactions())
		.isEqualTo(this.accountsSecondaryDao.findById(itayTO.getUsername()).get().getPendingTransactions())
		.isEmpty();
		
		assertThat(this.accountsPrimaryDao.findById(this.account1.getUsername()).get().getPendingTransactions())
		.isEqualTo(this.accountsSecondaryDao.findById(this.account1.getUsername()).get().getPendingTransactions())
		.isEmpty();
		
		AccountTO actualItayWhoAmI = 
				this.restTemplate.getForObject(this.url + WHO_AM_I, AccountTO.class, itayTO.getUsername());
		
		System.err.println(expectedTransaction.toString());
		
	}
	
}
