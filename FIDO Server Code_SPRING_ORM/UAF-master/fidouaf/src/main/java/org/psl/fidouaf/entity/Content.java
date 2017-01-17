package org.psl.fidouaf.entity;

import java.io.Serializable;

import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Content implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4020733094097885470L;

	@JsonProperty("amount_transferred")
	public String amount_transferred;

	@JsonProperty("username")
	public String username;

	@JsonProperty("to")
	public String to;

	public Content() {
		super();
	}

	public Content(String amount_transferred, String username, String to) {
		super();
		this.amount_transferred = amount_transferred;
		this.username = username;
		this.to = to;
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

}
