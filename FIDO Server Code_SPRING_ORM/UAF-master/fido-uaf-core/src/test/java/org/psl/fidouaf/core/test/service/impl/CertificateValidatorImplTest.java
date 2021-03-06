package org.psl.fidouaf.core.test.service.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;
import org.psl.fidouaf.core.service.impl.CertificateValidatorServiceImpl;
import org.psl.fidouaf.core.test.tlv.TestAssertions;
import org.psl.fidouaf.core.tlv.Tag;
import org.psl.fidouaf.core.tlv.Tags;
import org.psl.fidouaf.core.tlv.TagsEnum;
import org.psl.fidouaf.core.tlv.TlvAssertionParser;
import org.psl.fidouaf.core.tlv.UnsignedUtil;

public class CertificateValidatorImplTest {

	TlvAssertionParser p = new TlvAssertionParser();

	@Test
	public void basic() throws NoSuchAlgorithmException, Exception {
		Tags t = getTags(TestAssertions.getExampleRegAssertions());
		CertificateValidatorServiceImpl validator = new CertificateValidatorServiceImpl();
		boolean validate = validator.validate(
				t.getTags().get(TagsEnum.TAG_ATTESTATION_CERT.id).value,
				getSignedData(t.getTags().get(TagsEnum.TAG_UAFV1_KRD.id)), t
						.getTags().get(TagsEnum.TAG_SIGNATURE.id).value);
		assertTrue(validate);
	}

	@Test
	public void d() throws NoSuchAlgorithmException, Exception {
		Tags t = getTags(TestAssertions.getRegAssertionsFromD());
		CertificateValidatorServiceImpl validator = new CertificateValidatorServiceImpl();
		boolean validate = validator.validate(
				t.getTags().get(TagsEnum.TAG_ATTESTATION_CERT.id).value,
				getSignedData(t.getTags().get(TagsEnum.TAG_UAFV1_KRD.id)), t
						.getTags().get(TagsEnum.TAG_SIGNATURE.id).value);
		assertTrue(validate);
	}

	/***
	 * This validation false because EC curve secp256k1 is used instead of
	 * secp256r1 Validator is supporting only EC and only secp256r1 UPDATE:
	 * Added support for secp256r1 or secp256k1
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws Exception
	 */
	@Test
	public void sValidateTrue() throws NoSuchAlgorithmException, Exception {
		Tags t = getTags(TestAssertions.getRegAssertionsFromS());
		CertificateValidatorServiceImpl validator = new CertificateValidatorServiceImpl();
		boolean validate = validator.validate(
				t.getTags().get(TagsEnum.TAG_ATTESTATION_CERT.id).value,
				getSignedData(t.getTags().get(TagsEnum.TAG_UAFV1_KRD.id)), t
						.getTags().get(TagsEnum.TAG_SIGNATURE.id).value);
		assertTrue(validate);
	}

	@Test
	public void s2ValidateTrue() throws NoSuchAlgorithmException, Exception {
		Tags t = getTags(TestAssertions.getRegAssertionsFromS2());
		CertificateValidatorServiceImpl validator = new CertificateValidatorServiceImpl();
		boolean validate = validator.validate(
				t.getTags().get(TagsEnum.TAG_ATTESTATION_CERT.id).value,
				getSignedData(t.getTags().get(TagsEnum.TAG_UAFV1_KRD.id)), t
						.getTags().get(TagsEnum.TAG_SIGNATURE.id).value);
		assertTrue(validate);
	}

	@Test
	public void rsaValidateFalse() throws NoSuchAlgorithmException, Exception {
		Tags t = getTags(TestAssertions.regRequestAssertionsFromRaon());
		CertificateValidatorServiceImpl validator = new CertificateValidatorServiceImpl();
		boolean validate = validator.validate(
				t.getTags().get(TagsEnum.TAG_ATTESTATION_CERT.id).value,
				getSignedData(t.getTags().get(TagsEnum.TAG_UAFV1_KRD.id)), t
						.getTags().get(TagsEnum.TAG_SIGNATURE.id).value);
		assertTrue(!validate);
	}

	private Tags getTags(String regAssertions) throws IOException {
		return p.parse(regAssertions);
	}

	private byte[] getSignedData(Tag t) {
		byte[] signedBytes = new byte[t.value.length + 4];
		System.arraycopy(UnsignedUtil.encodeInt(t.id), 0, signedBytes, 0, 2);
		System.arraycopy(UnsignedUtil.encodeInt(t.length), 0, signedBytes, 2, 2);
		System.arraycopy(t.value, 0, signedBytes, 4, t.value.length);
		return signedBytes;
	}

}
