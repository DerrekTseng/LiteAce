package lite.tools;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

public class CryptoTools {

	public static byte[] JSByteToJavaByte(List<Object> list) {
		byte[] bytes = new byte[list.size()];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) ((int) list.get(i));
		}
		return bytes;
	}

	public static List<Integer> JavaByteToJSByte(byte[] bytes) {
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < bytes.length; i++) {
			list.add(Byte.toUnsignedInt(bytes[i]));
		}
		return list;
	}

	public static byte[] equalityEncrypt(byte[] data, byte[] key) {
		byte[] Result = new byte[data.length];
		int dataCount = 0;
		int keycount = 0;
		boolean flag = true;
		while (flag) {
			dataCount = 0;
			for (byte b : data) {
				int tempData = b;
				int lockmethod = dataCount + key[keycount];
				lockmethod = lockmethod % 256;
				if (key[keycount] % 2 == 0) {
					tempData += lockmethod;
				} else {
					tempData -= lockmethod;
				}
				if (tempData > 128) {
					tempData -= 256;
				}
				if (tempData < -128) {
					tempData += 256;
				}
				Result[dataCount] = (byte) tempData;
				dataCount += 1;
				keycount += 1;
				if (keycount >= key.length) {
					keycount = 0;
					flag = false;
				}
			}
		}
		return Result;
	}

	public static byte[] equalityDecrypt(byte[] data, byte[] key) {
		byte[] Result = new byte[data.length];
		int dataCount = 0;
		int keycount = 0;
		boolean flag = true;
		while (flag) {
			dataCount = 0;
			for (byte b : data) {
				int tempData = b;
				int lockmethod = dataCount + key[keycount];
				lockmethod = lockmethod % 256;
				if (key[keycount] % 2 == 0) {
					tempData -= lockmethod;
				} else {
					tempData += lockmethod;
				}
				if (tempData > 128) {
					tempData -= 256;
				}
				if (tempData < -128) {
					tempData += 256;
				}
				Result[dataCount] = (byte) tempData;
				dataCount += 1;
				keycount += 1;
				if (keycount >= key.length) {
					keycount = 0;
					flag = false;
				}
			}
		}
		return Result;
	}

	public static String bytesToString(byte[] bytes) {
		return DatatypeConverter.printHexBinary(bytes);
	}

	public static byte[] stringToBytes(String string) {
		return DatatypeConverter.parseHexBinary(string);
	}

	/**
	 * 單方向加密 無法反解密
	 * 
	 * @author DerrekTseng
	 */
	public static class oneWayHash {

		public static String MD5(String str) {
			return doHash(str, "MD5");
		}

		public static String SHA256(String str) {
			return doHash(str, "SHA-256");
		}

		public static String SHA512(String str) {
			return doHash(str, "SHA-512");
		}

		private static String doHash(String str, String algorithm) {
			try {
				if (str == null || "".equals(str)) {
					return "";
				}
				MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
				messageDigest.update(str.getBytes());
				return bytesToString(messageDigest.digest());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

	}
}
