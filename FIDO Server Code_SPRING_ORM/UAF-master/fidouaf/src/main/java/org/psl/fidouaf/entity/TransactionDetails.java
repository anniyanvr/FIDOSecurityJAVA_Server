package org.psl.fidouaf.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

@Table(name = "transactiondb")
@Entity
public class TransactionDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3131744313537291976L;

	@Id
	@JsonProperty("accountId")
	@Column(name = "accountid")
	public int accountId;

	@JsonProperty("amount_transferred")
	@Column(name = "amount_transferred")
	public String amount_transferred;

	@JsonProperty("username")
	@Column(name = "username")
	public String username;

	@JsonProperty("to")
	@Column(name = "toAccount")
	public String to;

	@JsonProperty("appId")
	@Column(name = "appid")
	public String appId;

	public TransactionDetails() {
		super();
	}

	public TransactionDetails(int accountId, String amount_transferred,
			String username, String to, String appId) {
		super();
		this.accountId = accountId;
		this.amount_transferred = amount_transferred;
		this.username = username;
		this.to = to;
		this.appId = appId;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public String getAmount_transferred() {
		return amount_transferred;
	}

	public void setAmount_transferred(String amount_transferred) {
		this.amount_transferred = amount_transferred;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	@Override
	public String toString() {
		return "\nRP Account ID: " + this.accountId + "\n APP ID: "
				+ this.appId + "\nContent Details **************** \nAmount: "
				+ this.amount_transferred + "\n To: " + this.to + "\nFrom: "
				+ this.username;
	}
}
