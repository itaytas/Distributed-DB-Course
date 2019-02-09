package com.app.TwoPhaseCommit.accounts;

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
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.app.TwoPhaseCommit.api.AccountTO;
import com.app.TwoPhaseCommit.dal.primary.AccountsPrimaryDao;
import com.app.TwoPhaseCommit.dal.secondary.AccountsSecondaryDao;
import com.app.TwoPhaseCommit.logic.accounts.AccountEntity;
import com.app.TwoPhaseCommit.logic.accounts.AccountsService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AccountsTests {
	
	private final String CREATE_NEW_USER = "/new";
	private final String LOGIN = "/login/{username}";
	private final String GET_COMMUNITY = "/community/{username}";
	
	
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

	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.jsonMapper = new ObjectMapper();

		this.url = "http://localhost:" + port + "/api/account";
		System.err.println(this.url);
	}

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
		this.accountsService.cleanup();
	}

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testServerIsBootingCorrectly() throws Exception {
	}

	@Test
	public void testCreateAccountSuccessfully() throws Exception {
		AccountTO expectedTO = new AccountTO("username1", 100.0, null);
		AccountTO actualReturnedValue = 
				this.restTemplate.postForObject(this.url + CREATE_NEW_USER, expectedTO, AccountTO.class);

		Optional<AccountEntity> opPrimary = this.accountsPrimaryDao.findById(expectedTO.getUsername());
		Optional<AccountEntity> opSecondary = this.accountsSecondaryDao.findById(expectedTO.getUsername());

		assertThat(opPrimary.get())
		.isNotNull()
		.isEqualToIgnoringNullFields(opSecondary.get())
		.isEqualToIgnoringNullFields(actualReturnedValue.toEntity());

	}
	
	@Test
	public void testCreateTwoUsersWithSameUsername() throws Exception {
		AccountTO expectedTO1 = new AccountTO("username1", 100.0, null);
		AccountTO actualReturnedValue1 = 
				this.restTemplate.postForObject(this.url + CREATE_NEW_USER, expectedTO1, AccountTO.class);

		this.exception.expect(HttpServerErrorException.class);
		
		AccountTO expectedTO2 = new AccountTO("username1", 200.0, null);
		AccountTO actualReturnedValue2 = 
				this.restTemplate.postForObject(this.url + CREATE_NEW_USER, expectedTO2, AccountTO.class);
	}
	
	@Test
	public void testLoginSuccessfully() throws Exception {
		AccountTO expectedTO = new AccountTO("username1", 100.0, null);
		this.accountsService.createNewAccount(expectedTO.toEntity());
		AccountTO actualReturnedValue = 
				this.restTemplate.getForObject(this.url + LOGIN, AccountTO.class, expectedTO.getUsername());

		Optional<AccountEntity> opPrimary = this.accountsPrimaryDao.findById(expectedTO.getUsername());
		Optional<AccountEntity> opSecondary = this.accountsSecondaryDao.findById(expectedTO.getUsername());

		assertThat(opPrimary.get())
		.isNotNull()
		.isEqualToIgnoringNullFields(opSecondary.get())
		.isEqualToIgnoringNullFields(actualReturnedValue.toEntity());

	}
	
	@Test
	public void testLoginWithNoExistsAccount() throws Exception {
		AccountTO expectedTO = new AccountTO("username1", 100.0, null);
		
		this.exception.expect(HttpServerErrorException.class);
		
		AccountTO actualReturnedValue = 
				this.restTemplate.getForObject(this.url + LOGIN, AccountTO.class, expectedTO.getUsername());
	}
	
	@Test
	public void testGetCommunity() throws Exception {
		AccountTO expectedTO1 = new AccountTO("username1", 100.0, null);
		this.accountsService.createNewAccount(expectedTO1.toEntity());
		AccountTO expectedTO2 = new AccountTO("username2", 200.0, null);
		this.accountsService.createNewAccount(expectedTO2.toEntity());

		AccountTO[] accountsList = 
				this.restTemplate.getForObject(this.url + GET_COMMUNITY, AccountTO[].class, "username1");

		// Arrays.stream(accountsList).forEach(o -> System.err.println(o.toString()));
		assertThat(accountsList).doesNotContain(expectedTO1);
		assertThat(accountsList).contains(expectedTO2);
	}
	
	
	
}
