package org.psl.fidouaf.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author bhakti_lawande
 *
 */
@Table(name = "vendordb")
@Entity
public class VendorDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3165036304489597818L;

	@Id
	@JsonProperty("accountId")
	@Column(name = "accountid")
	private int accountid;

	@Column(name = "displayname")
	@JsonProperty("displayName")
	private String userName;

	@Column(name = "rpdisplayname")
	@JsonProperty("rpDisplayName")
	private String vendorName;

	@Column(name = "email")
	@JsonProperty("email")
	private String email;

	@JsonIgnore
	@JsonProperty("otp")
	@Column(name = "otp")
	private String otp;

	@JsonIgnore
	@Column(name = "vendor_regstats", columnDefinition = "boolean default false", nullable = false)
	private boolean vendor_regstats;

	@JsonIgnore
	@Column(name = "otp_creationdate")
	private Date otpCreationDate;

	@JsonIgnore
	@Transient
	@JsonProperty("deviceId")
	transient private String deviceId;

	public VendorDetails() {
		super();
	}

	public VendorDetails(int accountid, String userName, String vendorName,
			String email, String otp, boolean vendor_regstats,
			Date otpCreationDate, String deviceId) {
		super();
		this.accountid = accountid;
		this.userName = userName;
		this.vendorName = vendorName;
		this.email = email;
		this.otp = otp;
		this.vendor_regstats = vendor_regstats;
		this.otpCreationDate = otpCreationDate;
		this.deviceId = deviceId;
	}

	public VendorDetails(int accountid, String userName, String vendorName,
			String email, String otp, boolean vendor_regstats,
			Date otpCreationDate) {
		super();
		this.accountid = accountid;
		this.userName = userName;
		this.vendorName = vendorName;
		this.email = email;
		this.otp = otp;
		this.vendor_regstats = vendor_regstats;
		this.otpCreationDate = otpCreationDate;
	}

	public boolean isVendor_regstats() {
		return vendor_regstats;
	}

	public void setVendor_regstats(boolean vendor_regstats) {
		this.vendor_regstats = vendor_regstats;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getAccountid() {
		return accountid;
	}

	public void setAccountid(int accountid) {
		this.accountid = accountid;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public Date getOtpCreationDate() {
		return otpCreationDate;
	}

	public void setOtpCreationDate(Date otpCreationDate) {
		this.otpCreationDate = otpCreationDate;
	}

	@Override
	public String toString() {
		return "\nRP Display Name: " + this.vendorName + "\n Display Name: "
				+ this.userName + "\n RP Account ID: " + this.accountid
				+ "\n Email: " + this.email + "\n device Id: " + this.deviceId
				+ "\n Vendor Reg. Status: " + this.vendor_regstats + "\n OTP: "
				+ this.otp;
	}
}
