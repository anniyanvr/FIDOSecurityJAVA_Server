package org.ebayopensource.fido.uaf.msg;

public class ContextDetails {

	String aaid;
	String signature;
	String signedData;
	
	public String getAaid() {
		return aaid;
	}
	public void setAaid(String aaid) {
		this.aaid = aaid;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getSignedData() {
		return signedData;
	}
	public void setSignedData(String signedData) {
		this.signedData = signedData;
	}
	
}
