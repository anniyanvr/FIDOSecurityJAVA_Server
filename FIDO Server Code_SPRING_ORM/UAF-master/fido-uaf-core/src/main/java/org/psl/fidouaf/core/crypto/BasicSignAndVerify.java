package org.psl.fidouaf.core.crypto;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;

public class BasicSignAndVerify {
	// private Key publicKey;
	// private Key privateKey;

	/**
	 * Method that generates the Key pair specifucally for context validation
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	/*
	 * public void generateKeyPair() throws NoSuchAlgorithmException {
	 * KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA"); KeyPair kp
	 * = keyGen.genKeyPair(); publicKey = kp.getPublic(); privateKey =
	 * kp.getPrivate(); System.out.println("Public Key: " +
	 * publicKey.toString()); System.out.println("Private Key: " +
	 * privateKey.toString()); }
	 */

	/**
	 * Method that returns the Public Key generated.
	 * 
	 * @return
	 */
	/*
	 * public String getPublicKey(){ byte[] publicKeyBytes =
	 * publicKey.getEncoded(); BASE64Encoder encoder = new BASE64Encoder();
	 * return encoder.encode(publicKeyBytes); }
	 */

	/**
	 * Method that get Private key from privateKey.key file
	 * 
	 * @return
	 * @throws InvalidKeySpecException
	 * @throws IOException
	 */
	public static PrivateKey getPrivateKey() throws InvalidKeySpecException,
			NoSuchAlgorithmException, IOException {
		/*
		 * byte[] keyBytes = null; try { keyBytes = Files.readAllBytes(new
		 * File("C:/Keys/privatekey.key") .toPath()); } catch (IOException e) {
		 * e.printStackTrace(); } PKCS8EncodedKeySpec spec = new
		 * PKCS8EncodedKeySpec(keyBytes); KeyFactory kf = null; kf =
		 * KeyFactory.getInstance("RSA"); return kf.generatePrivate(spec);
		 */

		File f = new File("C:/RSAKeys/privateKey.pk8");
		FileInputStream fis = new FileInputStream(f);
		DataInputStream dis = new DataInputStream(fis);
		byte[] keyBytes = new byte[(int) f.length()];
		dis.readFully(keyBytes);
		dis.close();

		String temp = new String(keyBytes);
		String privKeyPEM = temp.replace("-----BEGIN PRIVATE KEY-----\n", "");
		privKeyPEM = privKeyPEM.replace("-----END PRIVATE KEY-----", "");
		System.out.println("Private key: " + privKeyPEM);

		Base64 b64 = new Base64();
		byte[] decoded = b64.decode(privKeyPEM);

		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
	}

	/**
	 * Method that signs context with the Private key generated specifically for
	 * this process.
	 * 
	 * @param message
	 * @return
	 * @throws SignatureException
	 */
	public static String sign(String message, PrivateKey privateKey)
			throws SignatureException {
		try {
			Signature sign = Signature.getInstance("SHA1withRSA");
			sign.initSign(privateKey);
			sign.update(message.getBytes("UTF-8"));
			return new String(Base64.encodeBase64(sign.sign()), "UTF-8");
		} catch (Exception ex) {
			throw new SignatureException(ex);
		}
	}

}
