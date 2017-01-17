package org.ebayopensource.fido.uaf.msg;

public class Account {
	
	String rpDisplayName; //vendor name
	String displayName; // user name
	String email; //email address
	int accountId; //accountId
	String phoneNumber; //phone number
	
	public String getRpDisplayName() {
		return rpDisplayName;
	}
	public void setRpDisplayName(String rpDisplayName) {
		this.rpDisplayName = rpDisplayName;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getAccountId() {
		return accountId;
	}
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	

}
