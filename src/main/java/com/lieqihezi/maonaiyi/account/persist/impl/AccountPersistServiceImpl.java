package com.lieqihezi.maonaiyi.account.persist.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.lieqihezi.maonaiyi.account.persist.AccountPersistException;
import com.lieqihezi.maonaiyi.account.persist.AccountPersistService;
import com.lieqihezi.maonaiyi.account.persist.domain.Account;

public class AccountPersistServiceImpl implements AccountPersistService {
	
	private static final String ELEMENT_ROOT = "account-persist";
	
	private static final String ELEMENT_ACCOUNTS = "accounts";
	
	private static final String ELEMENT_ACCOUNT = "account";
	
	private static final String ELEMENT_ACCOUNT_ID = "id";
	
	private static final String ELEMENT_ACCOUNT_NAME = "name";
	
	private static final String ELEMENT_ACCOUNT_EMAIL = "email";
	
	private static final String ELEMENT_ACCOUNT_PASSWORD = "password";
	
	private static final String ELEMENT_ACCOUNT_ACTIVATED = "activated";
	
	private String file;
	
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	private SAXReader reader = new SAXReader();
	
	private Document readDocument() throws AccountPersistException {
		File dataFile = new File(file);
		
		if(!dataFile.exists()){
			dataFile.getParentFile().mkdirs();
			
			Document doc = DocumentFactory.getInstance().createDocument();
			
			Element rootElement = doc.addElement(ELEMENT_ROOT);
			
			rootElement.addElement(ELEMENT_ACCOUNTS);
			
			writeDocument(doc);
		}
		try {
			return reader.read(new File(file));
		} catch (DocumentException e) {
			throw new AccountPersistException("Unable to read persist data xml", e);
		}
	}
	
	private void writeDocument(Document doc) throws AccountPersistException
	{
		Writer out = null;
		try {
			out = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
			XMLWriter writer = new XMLWriter(out, OutputFormat.createPrettyPrint());
			writer.write(doc);
		} catch (IOException e) {
			throw new AccountPersistException("Unable to write persist data xml", e);
		} finally {
			try {
				if(out != null){
					out.close();
				}
			} catch (IOException e) {
				throw new AccountPersistException("Unable to close persist data xml writer", e);
			}
		}
	}
	
	private Account buildAccount(Element element){
		Account account = new Account();
		
		account.setId(element.elementText(ELEMENT_ACCOUNT_ID));
		account.setName(element.elementText(ELEMENT_ACCOUNT_NAME));
		account.setEmail(element.elementText(ELEMENT_ACCOUNT_EMAIL));
		account.setPassword(element.elementText(ELEMENT_ACCOUNT_PASSWORD));
		account.setActivated("true".equals(element.elementText(ELEMENT_ACCOUNT_ACTIVATED))?true:false);
		
		return account;
	}
	
	private void buildAccountElement(Element accountsElement, Account account){
		Element accountElement = accountsElement.addElement(ELEMENT_ACCOUNT);
		accountElement.addElement(ELEMENT_ACCOUNT_ID).setText(account.getId());
		accountElement.addElement(ELEMENT_ACCOUNT_NAME).setText(account.getName());
		accountElement.addElement(ELEMENT_ACCOUNT_EMAIL).setText(account.getEmail());
		accountElement.addElement(ELEMENT_ACCOUNT_PASSWORD).setText(account.getPassword());
		accountElement.addElement(ELEMENT_ACCOUNT_ACTIVATED).setText(String.valueOf(account.isActivated()));
	}
	public Account createAccount(Account account) throws AccountPersistException {
		File dataFile = new File(file);
		
		if(!dataFile.exists()){
			dataFile.getParentFile().mkdirs();
			
			Document doc = DocumentFactory.getInstance().createDocument();
			
			Element rootElement = doc.addElement(ELEMENT_ROOT);
			
			Element accountsElement = rootElement.addElement(ELEMENT_ACCOUNTS);
			
			buildAccountElement(accountsElement, account);
			
			writeDocument(doc);
		}else{
			Document doc = readDocument();
			
			Element accountsElement = doc.getRootElement().element(ELEMENT_ACCOUNTS);
			
			buildAccountElement(accountsElement, account);

			writeDocument(doc);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Account readAccount(String id) throws AccountPersistException {
		Document doc = readDocument();
		
		Element accountsElement = doc.getRootElement().element(ELEMENT_ACCOUNTS);
		
		for(Element accountElement : (List<Element>) accountsElement.elements()){
			if(accountElement.elementText(ELEMENT_ACCOUNT_ID).equals(id)){
				return buildAccount(accountElement);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Account updateAccount(Account account) throws AccountPersistException {
		try {
			Document doc = reader.read(file);
			
			Element accountsElement = doc.getRootElement().element(ELEMENT_ACCOUNTS);
			
			for(Element accountElement : (List<Element>) accountsElement.elements()){
				if(accountElement.elementText(ELEMENT_ACCOUNT_ID).equals(account.getId())){
					accountElement.element(ELEMENT_ACCOUNT_NAME).setText(account.getName());
					accountElement.element(ELEMENT_ACCOUNT_EMAIL).setText(account.getEmail());
					accountElement.element(ELEMENT_ACCOUNT_PASSWORD).setText(account.getPassword());
					accountElement.element(ELEMENT_ACCOUNT_ACTIVATED).setText(String.valueOf(account.isActivated()));
				}
			}
			
			writeDocument(doc);
		} catch (DocumentException e) {
			throw new AccountPersistException("Unable to update Account", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public void deleteAccount(String id) throws AccountPersistException {
		Document doc;
		try {
			doc = reader.read(file);
			// 删除注释
			doc.getRootElement().remove(doc.selectSingleNode("//comment()"));
			
			Element accountsElement = doc.getRootElement().element(ELEMENT_ACCOUNT);
			
			for(Element accountElement : (List<Element>) accountsElement.elements()){
				if(accountElement.elementText(ELEMENT_ACCOUNT_ID).equals(id)){
					accountElement.remove(accountElement);
				}
			}
		} catch (DocumentException e) {
			throw new AccountPersistException("Unable to delete Account", e);
		}
	}

}
