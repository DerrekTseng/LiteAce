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
	public static String getMD5(String str) {
		if ("".equals(str) || str == null) {
			return "";
		}
		String ret = null;
		try {
			// 生成一個MD5加密計算摘要
			MessageDigest md = MessageDigest.getInstance("MD5");
			// 計算md5函數
			md.update(str.getBytes());
			// digest()最後確定返回md5 hash值，返回值為8為字符串。因為md5 hash值是16位的hex值，實際上就是8位的字符
			// BigInteger函數則將8位的字符串轉換成16位hex值，用字符串來表示；得到字符串形式的hash值
			ret = new BigInteger(1, md.digest()).toString(16);
		} catch (Exception e) {
			// throw new SpeedException("MD5加密出現錯誤");
			ExceptionTools.printAllStackTrace(e);
		}
		return ret;
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
