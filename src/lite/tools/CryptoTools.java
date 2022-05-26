package lite.tools;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

	/**
	 * 對字符串md5加密 空字串會回空字串
	 *
	 * @param str
	 * @return
	 */
	public static String MD5(String str) {
		try {
			return hashString(str, MessageDigest.getInstance("MD5"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String SHA256(String str) {
		try {
			return hashString(str, MessageDigest.getInstance("SHA-256"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String hashString(String str, MessageDigest messageDigest) {
		if (str == null || "".equals(str)) {
			return "";
		}
		messageDigest.update(str.getBytes());
		return new BigInteger(1, messageDigest.digest()).toString(16);
	}

	public static String bytesToString(byte[] bytes) {
		return DatatypeConverter.printHexBinary(bytes);
	}

	public static byte[] stringToBytes(String string) {
		return DatatypeConverter.parseHexBinary(string);
	}

	public static String genUUID() {
		return UUID.randomUUID().toString();
	}
}
