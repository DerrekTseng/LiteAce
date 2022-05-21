package lite.core.mybatis;

import org.springframework.util.LinkedCaseInsensitiveMap;

public class MyHashMap extends LinkedCaseInsensitiveMap<Object> {

	private static final long serialVersionUID = 1L;

	public <T> T getData(String key) {
		return getData(key, null);
	}

	public <T> T getData(String key, T defaultValue) {
		Object data = super.getOrDefault(key, defaultValue);
		@SuppressWarnings("unchecked")
		T t = (T) data;
		return t;
	}
}
