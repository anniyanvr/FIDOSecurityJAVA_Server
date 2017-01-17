package org.ebayopensource.fido.uaf.msg;

public class Device {
	
	String deviceid;
	String devicetoken;
	String rpaccountname;
	boolean regstats;
	String publickey;
	String aaid_keyid;
	String rpaccountid;
	//String phoneNumber;
	String context;
	Authenticators[] authenticators;
	ContextDetails[] contextDetails;
	
	public Authenticators[] getAuthenticators() {
		return authenticators;
	}
	public void setAuthenticators(Authenticators[] authenticators) {
		this.authenticators = authenticators;
	}
	public ContextDetails[] getContextDetails() {
		return contextDetails;
	}
	public void setContextDetails(ContextDetails[] contextDetails) {
		this.contextDetails = contextDetails;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getRpaccountname() {
		return rpaccountname;
	}
	public void setRpaccountname(String rpaccountname) {
		this.rpaccountname = rpaccountname;
	}
	public String getRpaccountid() {
		return rpaccountid;
	}
	public void setRpaccountid(String rpaccountid) {
		this.rpaccountid = rpaccountid;
	}
	
	
	/*public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}*/
	public String getDeviceid() {
		return deviceid;
	}
	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}
	public String getDevicetoken() {
		return devicetoken;
	}
	public void setDevicetoken(String devicetoken) {
		this.devicetoken = devicetoken;
	}
	public boolean isRegstats() {
		return regstats;
	}
	public void setRegstats(boolean regstats) {
		this.regstats = regstats;
	}
	public String getPublickey() {
		return publickey;
	}
	public void setPublickey(String publickey) {
		this.publickey = publickey;
	}
	public String getAaid_keyid() {
		return aaid_keyid;
	}
	public void setAaid_keyid(String aaid_keyid) {
		this.aaid_keyid = aaid_keyid;
	}
	
	

}
