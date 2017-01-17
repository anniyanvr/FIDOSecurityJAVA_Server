package org.psl.fidouaf.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.ws.rs.DefaultValue;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Table(name = "registrationdb")
@Entity
public class Registration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7755750674468396837L;

	@Column(name = "deviceid")
	String deviceId;

	@Column(name = "rpaccountname")
	String rpDisplayName;

	@JsonIgnore
	@DefaultValue("false")
	@Column(name = "regstats")
	boolean regStats;

	@Id
	@JsonProperty("rpaccountId")
	@Column(name = "rpaccountid")
	int rpaccountId;

	@JsonIgnore
	@DefaultValue("false")
	@Column(name = "auth_in_progress")
	boolean authInProgress;

	@JsonIgnore
	@DefaultValue("false")
	@Column(name = "authstats")
	boolean authStatus;

	@Column(name = "context")
	String context;

	@JsonIgnore
	@JsonProperty("aaids")
	@Column(name = "authenticators_enforced")
	String aaids;

	@JsonIgnore
	@Transient
	transient ContextDetails[] contextDetails;

	public Registration() {
		super();
	}

	public Registration(String deviceId, String rpDisplayName,
			boolean regStats, int rpaccountId, boolean authInProgress,
			boolean authStatus, String context, String aaids,
			ContextDetails[] contextDetails) {
		super();
		this.deviceId = deviceId;
		this.rpDisplayName = rpDisplayName;
		this.regStats = regStats;
		this.rpaccountId = rpaccountId;
		this.authInProgress = authInProgress;
		this.authStatus = authStatus;
		this.context = context;
		this.aaids = aaids;
		this.contextDetails = contextDetails;
	}

	public Registration(String deviceId, String rpDisplayName,
			boolean regStats, int rpaccountId, boolean authInProgress,
			boolean authStatus, String context, String aaids) {
		super();
		this.deviceId = deviceId;
		this.rpDisplayName = rpDisplayName;
		this.regStats = regStats;
		this.rpaccountId = rpaccountId;
		this.authInProgress = authInProgress;
		this.authStatus = authStatus;
		this.context = context;
		this.aaids = aaids;
	}

	public String getAaids() {
		return aaids;
	}

	public void setAaids(String aaids) {
		this.aaids = aaids;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "registrationdb")
	@Cascade({ CascadeType.SAVE_UPDATE, CascadeType.DELETE })
	public ContextDetails[] getContextDetails() {
		return contextDetails;
	}

	public void setContextDetails(ContextDetails[] contextDetails) {
		this.contextDetails = contextDetails;
	}

	public boolean getAuthInProgress() {
		return authInProgress;
	}

	public void setAuthInProgress(boolean authInProgress) {
		this.authInProgress = authInProgress;
	}

	public boolean getAuthStatus() {
		return authStatus;
	}

	public void setAuthStatus(boolean authStatus) {
		this.authStatus = authStatus;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public boolean getRegStats() {
		return regStats;
	}

	public void setRegStats(boolean vendorRegStats) {
		this.regStats = vendorRegStats;
	}

	public String getRpDisplayName() {
		return rpDisplayName;
	}

	public void setRpDisplayName(String rpDisplayName) {
		this.rpDisplayName = rpDisplayName;
	}

	public int getRpAccountId() {
		return rpaccountId;
	}

	public void setRpAccountId(int rpAccountId) {
		this.rpaccountId = rpAccountId;
	}

}
