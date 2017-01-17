package org.psl.fidouaf.entity;

import java.io.Serializable;

import javax.persistence.Entity;

@Entity
public class ContextDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4494714180835869486L;

	String aaid;
	String signature;
	String signedData;

	public ContextDetails() {
		super();
	}

	public ContextDetails(String aaid, String signature, String signedData) {
		super();
		this.aaid = aaid;
		this.signature = signature;
		this.signedData = signedData;
	}

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
