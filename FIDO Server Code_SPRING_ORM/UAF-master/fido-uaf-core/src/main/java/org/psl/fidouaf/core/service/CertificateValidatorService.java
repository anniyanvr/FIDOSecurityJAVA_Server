package org.psl.fidouaf.core.service;

public interface CertificateValidatorService {
	public boolean validate(String cert, String signedData, String signature)
			throws Exception;

	public boolean validate(byte[] certBytes, byte[] signedDataBytes,
			byte[] signatureBytes) throws Exception;
}
