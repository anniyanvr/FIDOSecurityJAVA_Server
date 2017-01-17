package org.psl.fidouaf.entity;

import java.util.Date;

public class OTPDetails {
	private String generatedOTP;
	private Date OTPGenerationDate;

	public String getGeneratedOTP() {
		return generatedOTP;
	}

	public void setGeneratedOTP(String generatedOTP) {
		this.generatedOTP = generatedOTP;
	}

	public Date getOTPGenerationDate() {
		return OTPGenerationDate;
	}

	public void setOTPGenerationDate(Date oTPGenerationDate) {
		OTPGenerationDate = oTPGenerationDate;
	}
}
