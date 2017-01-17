package org.ebayopensource.fido.uaf.msg;

public class Login {
	
	String username;
	boolean regstats;
	String publickey; 
	String otp;
	boolean loginstats;
	String vendordetail; 
	String aaid_keyid;
	String deviceId;
	//String password;
	String deviceToken;
	
	public String getDeviceToken() {
		return deviceToken;
	}
	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
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
	public void setPublickey(String publicke) {
		this.publickey = publicke;
	}
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
	public boolean isLoginstats() {
		return loginstats;
	}
	public void setLoginstats(boolean loginstats) {
		this.loginstats = loginstats;
	}
	public String getVendordetail() {
		return vendordetail;
	}
	public void setVendordetail(String vendordetail) {
		this.vendordetail = vendordetail;
	}
	public String getAaid_keyid() {
		return aaid_keyid;
	}
	public void setAaid_keyid(String aaid_keyid) {
		this.aaid_keyid = aaid_keyid;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	/*public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}*/

}
