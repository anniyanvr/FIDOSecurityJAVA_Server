package org.psl.fidouaf.entity;

public class SendPushNotification {

	private String token;
	private String content;
	public PushOperation operation;

	public PushOperation getOperation() {
		return operation;
	}

	public void setOperation(PushOperation operation) {
		this.operation = operation;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
