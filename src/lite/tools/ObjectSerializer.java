package lite.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

/**
 * java.io.Serializable Object to byte[] to String
 * 
 * @author DerrekTseng
 *
 */
public class ObjectSerializer {

	/**
	 * Parse object to byte[].<br/>
	 * The object must implement java.io.Serializable<br/>
	 * <br/>
	 * Example:<br/>
	 * byte[] bytes = SerializeObject.ParseString(myObject);
	 * 
	 * @param Object object
	 * @return byte[]
	 * @throws IOException
	 */
	public static byte[] parseByte(Object object) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(object);
		objectOutputStream.close();
		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * Parse byte[] to object.<br/>
	 * The object must implement java.io.Serializable<br/>
	 * <br/>
	 * Example:<br/>
	 * MyObject myObject = SerializeObject.ParseObject(bytes);
	 * 
	 * @param byte[] bytes
	 * @return Object
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static <E> E parseObject(byte[] bytes) throws IOException, ClassNotFoundException {
		ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
		Object object = objectInputStream.readObject();
		objectInputStream.close();
		return (E) object;
	}

	/**
	 * Parse object to String.<br/>
	 * The object must implement java.io.Serializable<br/>
	 * <br/>
	 * Example:<br/>
	 * String string = SerializeObject.ParseString(myObject);
	 * 
	 * @param Object object
	 * @return String
	 * @throws IOException
	 */
	public static String parseString(Object object) throws IOException {
		return Base64.getEncoder().encodeToString(parseByte(object));
	}

	/**
	 * Parse String to object.<br/>
	 * The object must implement java.io.Serializable<br/>
	 * <br/>
	 * Example:<br/>
	 * MyObject myObject = SerializeObject.ParseObject(string);
	 * 
	 * @param String string
	 * @return Object
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static <E> E parseObject(String string) throws IOException, ClassNotFoundException {
		byte[] bytes = Base64.getDecoder().decode(string);
		return (E) parseObject(bytes);
	}

	/**
	 * Test the object if is implemented java.io.Serializable
	 * 
	 * @param object
	 * @return
	 */
	public static boolean isSerializabled(Object object) {
		if (object instanceof java.io.Serializable) {
			return true;
		}
		return false;
	}
}