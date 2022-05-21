package lite.tools;

import javax.crypto.Cipher;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.util.Base64;

public class RSATools {

	public static final String CHARSET = "UTF-8";
	public static final String RSA_ALGORITHM = "RSA";
	public static final String RSA_SIGNATURE_ALGORITHM = "SHA256WithRSA";

	public static class RSAKeyPair implements Serializable {

		private static final long serialVersionUID = -2024175666031498187L;

		private final String privateKey;
		private final String publicKey;

		private final String privateKeyFileFormat;
		private final String publicKeyFileFormat;

		public RSAKeyPair(String privateKey, String publicKey) {
			this.privateKey = privateKey;
			this.publicKey = publicKey;

			StringBuilder fileFormat = new StringBuilder();
			int count = 0;

			fileFormat.setLength(0);
			fileFormat.append("-----BEGIN PRIVATE KEY-----\n");
			count = 0;
			for (char c : privateKey.toCharArray()) {
				count++;
				fileFormat.append(c);
				if (count % 64 == 0) {
					fileFormat.append("\n");
				}
			}
			if (count % 64 != 0) {
				fileFormat.append("\n");
			}
			fileFormat.append("-----END PRIVATE KEY-----");
			privateKeyFileFormat = fileFormat.toString();

			fileFormat.setLength(0);
			fileFormat.append("-----BEGIN PUBLIC KEY-----\n");
			count = 0;
			for (char c : publicKey.toCharArray()) {
				count++;
				fileFormat.append(c);
				if (count % 64 == 0) {
					fileFormat.append("\n");
				}
			}
			if (count % 64 != 0) {
				fileFormat.append("\n");
			}
			fileFormat.append("-----END PUBLIC KEY-----");
			publicKeyFileFormat = fileFormat.toString();
		}

		public String getPrivateKey() {
			return privateKey;
		}

		public String getPublicKey() {
			return publicKey;
		}

		public String getPrivateKeyFileFormat() {
			return privateKeyFileFormat;
		}

		public String getPublicKeyFileFormat() {
			return publicKeyFileFormat;
		}

	}

	/**
	 * 建立一個 RSA KeyPair
	 * 
	 * @param keySize
	 * @return
	 */
	public static RSAKeyPair createKeyPair(int keySize) {
		// 為RSA演算法建立一個KeyPairGenerator物件
		KeyPairGenerator kpg;
		try {
			kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
		}

		// 初始化KeyPairGenerator物件,金鑰長度
		kpg.initialize(keySize);
		// 生成密匙對
		KeyPair keyPair = kpg.generateKeyPair();
		// 得到公鑰
		Key publicKey = keyPair.getPublic();
		String publicKeyStr = Base64.getEncoder().encodeToString((publicKey.getEncoded()));
		// 得到私鑰
		Key privateKey = keyPair.getPrivate();
		String privateKeyStr = Base64.getEncoder().encodeToString((privateKey.getEncoded()));

		RSAKeyPair rsaKeyPair = new RSAKeyPair(privateKeyStr, publicKeyStr);

		return rsaKeyPair;
	}

	/**
	 * 得到公鑰
	 * 
	 * @param publicKey 金鑰字串（經過base64編碼）
	 * @throws Exception
	 */
	public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		// 通過X509編碼的Key指令獲得公鑰物件
		KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));
		RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
		return key;
	}

	/**
	 * 得到私鑰
	 * 
	 * @param privateKey 金鑰字串（經過base64編碼）
	 * @throws Exception
	 */
	public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		// 通過PKCS#8編碼的Key指令獲得私鑰物件
		KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
		RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
		return key;
	}

	/**
	 * 公鑰加密
	 * 
	 * @param data
	 * @param publicKey
	 * @return
	 */
	public static String publicEncrypt(String data, RSAPublicKey publicKey) {
		try {
			Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			return Base64.getEncoder().encodeToString((rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), publicKey.getModulus().bitLength())));
		} catch (Exception e) {
			throw new RuntimeException("加密字串[" + data + "]時遇到異常", e);
		}
	}

	/**
	 * 公鑰加密
	 * 
	 * @param data
	 * @param publicKey
	 * @return
	 */
	public static String publicEncrypt(String data, String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		return publicEncrypt(data, getPublicKey(publicKey));
	}

	/**
	 * 私鑰解密
	 * 
	 * @param data
	 * @param privateKey
	 * @return
	 */
	public static String privateDecrypt(String data, RSAPrivateKey privateKey) {
		try {
			Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.getDecoder().decode(data), privateKey.getModulus().bitLength()), CHARSET);
		} catch (Exception e) {
			throw new RuntimeException("解密字串[" + data + "]時遇到異常", e);
		}
	}

	/**
	 * 私鑰解密
	 * 
	 * @param data
	 * @param privateKey
	 * @return
	 */
	public static String privateDecrypt(String data, String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		return privateDecrypt(data, getPrivateKey(privateKey));
	}

	/**
	 * 私鑰加密
	 * 
	 * @param data
	 * @param privateKey
	 * @return
	 */
	public static String privateEncrypt(String data, RSAPrivateKey privateKey) {
		try {
			Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			return Base64.getEncoder().encodeToString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), privateKey.getModulus().bitLength()));
		} catch (Exception e) {
			throw new RuntimeException("加密字串[" + data + "]時遇到異常", e);
		}
	}

	/**
	 * 私鑰加密
	 * 
	 * @param data
	 * @param privateKey
	 * @return
	 */
	public static String privateEncrypt(String data, String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		return privateEncrypt(data, getPrivateKey(privateKey));
	}

	/**
	 * 公鑰解密
	 * 
	 * @param data
	 * @param publicKey
	 * @return
	 */
	public static String publicDecrypt(String data, RSAPublicKey publicKey) {
		try {
			Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.getDecoder().decode(data), publicKey.getModulus().bitLength()), CHARSET);
		} catch (Exception e) {
			throw new RuntimeException("解密字串[" + data + "]時遇到異常", e);
		}
	}

	/**
	 * 公鑰解密
	 * 
	 * @param data
	 * @param publicKey
	 * @return
	 */
	public static String publicDecrypt(String data, String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		return publicDecrypt(data, getPublicKey(publicKey));
	}

	private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
		int maxBlock = 0;
		if (opmode == Cipher.DECRYPT_MODE) {
			maxBlock = keySize / 8;
		} else {
			maxBlock = keySize / 8 - 11;
		}
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			int offSet = 0;
			byte[] buff;
			int i = 0;
			try {
				while (datas.length > offSet) {
					if (datas.length - offSet > maxBlock) {
						buff = cipher.doFinal(datas, offSet, maxBlock);
					} else {
						buff = cipher.doFinal(datas, offSet, datas.length - offSet);
					}
					out.write(buff, 0, buff.length);
					i++;
					offSet = i * maxBlock;
				}
			} catch (Exception e) {
				throw new RuntimeException("加解密閥值為[" + maxBlock + "]的資料時發生異常", e);
			}
			byte[] resultDatas = out.toByteArray();

			return resultDatas;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 為資料進行簽名
	 * 
	 * @param data       資料
	 * @param privateKey 私鑰
	 * @return byte[] 數位簽章
	 */
	public static byte[] getSignature(byte[] data, String privateKey) {
		try {
			Signature sig = Signature.getInstance(RSA_SIGNATURE_ALGORITHM);
			sig.initSign(getPrivateKey(privateKey));
			sig.update(data);
			return sig.sign();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 驗證簽章
	 * 
	 * @param data           資料
	 * @param signatureBytes 數位簽章
	 * @param publicKey      公鑰
	 * @return
	 */
	public static boolean verifySignature(byte[] data, byte[] signatureBytes, String publicKey) {
		try {
			Signature sig = Signature.getInstance(RSA_SIGNATURE_ALGORITHM);
			sig.initVerify(getPublicKey(publicKey));
			sig.update(data);
			return sig.verify(signatureBytes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
