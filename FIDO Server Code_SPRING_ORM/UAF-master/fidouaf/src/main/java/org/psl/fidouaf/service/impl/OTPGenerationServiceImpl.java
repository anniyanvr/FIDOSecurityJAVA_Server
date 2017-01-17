package org.psl.fidouaf.service.impl;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.psl.fidouaf.entity.OTPDetails;
import org.psl.fidouaf.service.OTPGenerationService;
import org.springframework.stereotype.Service;

@Service
public class OTPGenerationServiceImpl implements OTPGenerationService {

	private DateFormat dateFormat;

	public OTPDetails generateOTPwithoutSMS() {
		int size = 6;

		StringBuilder generatedToken = new StringBuilder();
		try {
			SecureRandom number = SecureRandom.getInstance("SHA1PRNG");
			// Generate 20 integers 0..20
			for (int i = 0; i < size; i++) {
				generatedToken.append(number.nextInt(9));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		Date date = new Date();
		//String creationDate = dateFormat.format(date);
		OTPDetails otpDetails = new OTPDetails();
		otpDetails.setGeneratedOTP(generatedToken.toString());
		otpDetails.setOTPGenerationDate(date/*creationDate*/);
		return otpDetails;
	}

	public DateFormat getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}
}
