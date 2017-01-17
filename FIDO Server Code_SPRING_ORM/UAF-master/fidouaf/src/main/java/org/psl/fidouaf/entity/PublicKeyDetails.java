package org.psl.fidouaf.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "key_info")
@Entity
public class PublicKeyDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3674819661301690157L;

	@Column(name = "deviceid")
	String deviceId;

	@Column(name = "rpaccountid")
	int rpAccountId;

	@Column(name = "publickey")
	String publicKey;

	@Id
	@Column(name = "aaid_keyid")
	String aaid_keyid;

	public PublicKeyDetails() {
		super();
	}

	public PublicKeyDetails(String deviceId, int rpAccountId, String publicKey,
			String aaid_keyid) {
		super();
		this.deviceId = deviceId;
		this.rpAccountId = rpAccountId;
		this.publicKey = publicKey;
		this.aaid_keyid = aaid_keyid;
	}

	public int getRpAccountId() {
		return rpAccountId;
	}

	public void setRpAccountId(int rpAccountId) {
		this.rpAccountId = rpAccountId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getAaid_keyid() {
		return aaid_keyid;
	}

	public void setAaid_keyid(String aaid_keyid) {
		this.aaid_keyid = aaid_keyid;
	}
}
