package lite.core.translang;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;

import lite.tools.HttpTools;

public class TranslangSupport {

	/**
	 * 前端語言設定 KEY
	 */
	public final String LANGUAGE_COOKIES_KEY = "language";

	private static final Properties EMPTY_PROPERTIES = new Properties();

	private final Resource languageMapping;

	private volatile Properties languageProperties;

	private volatile long languageFileLastModifiedTime = 0L;

	private final Map<String, Properties> translangProperties = new HashMap<>();

	private final Map<String, Resource> translangMapping;

	private String defalutKey;

	private boolean reloadable;

	private final Map<String, Long> translangFileLastModifiedTime = new HashMap<>();

	public TranslangSupport(Resource languageMapping, Map<String, Resource> translangMapping) throws IOException {

		this.languageMapping = languageMapping;

		this.translangMapping = translangMapping;

		if (this.translangMapping == null || this.languageMapping == null) {
			return;
		}

		languageFileLastModifiedTime = languageMapping.getFile().lastModified();

		try (InputStreamReader inputStreamReader = new InputStreamReader(languageMapping.getInputStream(), StandardCharsets.UTF_8)) {
			Properties properties = new Properties();
			properties.load(inputStreamReader);
			languageProperties = properties;
		}

		for (Entry<String, Resource> entry : this.translangMapping.entrySet()) {
			try (InputStreamReader inputStreamReader = new InputStreamReader(entry.getValue().getInputStream(), StandardCharsets.UTF_8)) {
				translangFileLastModifiedTime.put(entry.getKey(), entry.getValue().getFile().lastModified());
				Properties properties = new Properties();
				properties.load(inputStreamReader);
				translangProperties.put(entry.getKey(), properties);
			}
		}
	}

	public Properties getLanguages() {
		try {
			if (languageFileLastModifiedTime != languageMapping.getFile().lastModified()) {
				try (InputStreamReader inputStreamReader = new InputStreamReader(languageMapping.getInputStream(), StandardCharsets.UTF_8)) {
					Properties properties = new Properties();
					properties.load(inputStreamReader);
					languageProperties = properties;
				}
			}
		} catch (Exception e) {

		}
		return languageProperties;
	}

	public Properties getTranslang() {
		return getTranslang(null);
	}

	public Properties getTranslang(String key) {

		if (StringUtils.isBlank(key) && StringUtils.isNotBlank(defalutKey)) {
			key = defalutKey;
		} else if (StringUtils.isAllBlank(key, defalutKey)) {
			return EMPTY_PROPERTIES;
		}

		Properties properties = translangProperties.get(key);

		if (properties == null && StringUtils.isNotBlank(defalutKey)) {
			key = defalutKey;
			properties = translangProperties.get(key);
		}

		if (properties == null) {
			return EMPTY_PROPERTIES;
		} else {
			if (reloadable) {
				if (isTranslangModified(key)) {
					return reloadTranslang(key);
				} else {
					return properties;
				}
			} else {
				return properties;
			}
		}
	}

	/**
	 * 翻譯
	 * 
	 * @param key
	 * @return
	 */
	public String translating(String key) {
		return translating(null, key);
	}

	/**
	 * 翻譯
	 * 
	 * @param lang
	 * @param key
	 * @return
	 */
	public String translating(String lang, String key) {
		return getTranslang(lang).getProperty(key, "");
	}

	public String getClientLanguage(HttpServletRequest request) {
		return HttpTools.getCookieValue(request, LANGUAGE_COOKIES_KEY, "");
	}

	private boolean isTranslangModified(String key) {
		try {
			return translangFileLastModifiedTime.get(key).compareTo(translangMapping.get(key).getFile().lastModified()) != 0;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Properties reloadTranslang(String key) {
		Resource resource = translangMapping.get(key);
		try (InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
			translangFileLastModifiedTime.put(key, resource.getFile().lastModified());
			Properties properties = new Properties();
			properties.load(inputStreamReader);
			translangProperties.put(key, properties);
			return properties;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String getDefalutKey() {
		return defalutKey;
	}

	public void setDefalutKey(String defalutKey) {
		this.defalutKey = defalutKey;
	}

	public boolean isReloadable() {
		return reloadable;
	}

	public void setReloadable(boolean reloadable) {
		this.reloadable = reloadable;
	}

}
