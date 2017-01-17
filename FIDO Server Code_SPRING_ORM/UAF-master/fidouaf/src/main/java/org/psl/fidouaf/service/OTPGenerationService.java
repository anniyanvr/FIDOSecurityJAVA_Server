package org.psl.fidouaf.service;

import org.psl.fidouaf.entity.OTPDetails;
import org.springframework.stereotype.Service;

@Service
public interface OTPGenerationService {
	public OTPDetails generateOTPwithoutSMS();
}
