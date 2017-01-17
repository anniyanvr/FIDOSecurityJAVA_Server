package org.ebayopensource.fido.uaf.msg;

public class TransactionDetails {
	
	public String accountId;
	public Content content;
	public String appId;
	//public String transactionContext;
	
	/*public String getTransactionContext() {
		return transactionContext;
	}
	public void setTransactionContext(String transactionContext) {
		this.transactionContext = transactionContext;
	}*/
	public Content getContent() {
		return content;
	}
	public void setContent(Content content) {
		this.content = content;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
}
