package com.lieqihezi.maonaiyi.account.persist;

import static org.junit.Assert.*;
import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lieqihezi.maonaiyi.account.persist.domain.Account;

public class AccountPersistServiceTest {
	
	private AccountPersistService service;
	
	@Before
	public void prepare() throws Exception {
		File persistDataFile = new File("target/test-classes/persist-data.xml");
		if(persistDataFile.exists()){
			persistDataFile.delete();
		}
		ApplicationContext ctx = new ClassPathXmlApplicationContext("account-persist.xml");
		
		service = (AccountPersistService) ctx.getBean("accountPersistService");
		
		Account account = new Account();
		account.setId("bitao");
		account.setName("bitao.chen");
		account.setEmail("cbtpro@163.com");
		account.setPassword("this_should_be_encrypted");
		account.setActivated(true);
		
		service.createAccount(account);
		
		Account account1 = new Account();
		account1.setId("peter");
		account1.setName("peter.chen");
		account1.setEmail("peter@163.com");
		account1.setPassword("this_should_be_encrypted");
		account1.setActivated(false);
		service.createAccount(account1);
	}
	
	@Test
	public void testReadAccount() throws Exception
	{
		Account account = service.readAccount("bitao");
		assertNotNull(account);
		assertEquals("bitao",account.getId());
		assertEquals("bitao.chen",account.getName());
		assertEquals("cbtpro@163.com",account.getEmail());
		assertEquals("this_should_be_encrypted",account.getPassword());
		assertTrue(account.isActivated());
	}
	
	@After
	public void updateAccount() throws Exception
	{
		Account account = new Account();
		account.setId("bitao");
		account.setName("bitao.chen");
		account.setEmail("lieqihezi@163.com");
		account.setPassword("this_should_be_encrypted");
		account.setActivated(false);
		service.updateAccount(account);
		
		Account account1 = service.readAccount(account.getId());
		assertFalse(account1.isActivated());
	}
}
