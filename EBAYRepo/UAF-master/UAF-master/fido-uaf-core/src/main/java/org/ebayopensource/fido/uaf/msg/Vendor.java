package org.ebayopensource.fido.uaf.msg;


public class Vendor {
	//Account account;
	String otp;
	boolean vendorRegStats;
	String rpDisplayName; //vendor name
	String displayName; // user name
	String email; //email address
	String accountId; //accountId
	//String phoneNumber; //phone number
	String deviceId;
	Authenticators authenticators;
	
	public Authenticators getAuthenticators() {
		return authenticators;
	}
	public void setAuthenticators(Authenticators authenticators) {
		this.authenticators = authenticators;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
	public boolean isVendorRegStats() {
		return vendorRegStats;
	}
	public void setVendorRegStats(boolean vendorRegStats) {
		this.vendorRegStats = vendorRegStats;
	}
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
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	/*public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}*/
}
