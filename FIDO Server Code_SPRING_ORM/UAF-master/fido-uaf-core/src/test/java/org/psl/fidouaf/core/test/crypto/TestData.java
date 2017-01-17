package org.psl.fidouaf.core.test.crypto;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import org.apache.commons.codec.binary.Base64;
import org.psl.fidouaf.core.constants.Constants;
import org.psl.fidouaf.core.crypto.KeyCodec;
import org.psl.fidouaf.core.crypto.NamedCurve;
import org.psl.fidouaf.core.crypto.SHA;
import org.psl.fidouaf.core.tlv.TagsEnum;

public class TestData {
	public PublicKey pub = null;
	public PrivateKey priv = null;
	public byte[] dataForSigning = new byte[4];
	public byte[] signature = null;
	public BigInteger[] rsSignature = null;

	public TestData() throws NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchProviderException, InvalidKeyException, SignatureException,
			UnsupportedEncodingException, InvalidAlgorithmParameterException {
		this(KeyCodec.getPubKey(Base64.decodeBase64(Constants.TEST_PUB_KEY)),
				KeyCodec.getPrivKey(Base64
						.decodeBase64(Constants.TEST_PRIV_KEY)));
	}

	public TestData(PublicKey pubArg, PrivateKey privArg)
			throws NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchProviderException, InvalidKeyException, SignatureException,
			UnsupportedEncodingException, InvalidAlgorithmParameterException {
		pub = pubArg;
		priv = privArg;
		int signedDataId = TagsEnum.TAG_UAFV1_SIGNED_DATA.id;
		int signedDataLength = 200;
		dataForSigning[0] = (byte) (signedDataId & 0x00ff);
		dataForSigning[1] = (byte) (signedDataId & 0xff00);
		dataForSigning[2] = (byte) (signedDataLength & 0x00ff);
		dataForSigning[3] = (byte) (signedDataLength & 0xff00);
		// signature = NamedCurve.sign(priv, dataForSigning);
		rsSignature = NamedCurve.signAndFromatToRS(priv,
				SHA.sha(dataForSigning, "SHA-1"));
	}
}
