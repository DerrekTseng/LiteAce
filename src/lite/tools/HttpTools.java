package lite.tools;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpTools {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	/** Timeout in milliseconds */
	public static final int HTTP_CLIENT_TIMEOUT = 30000;

	public static <T> T doPost(String url, Object data, Class<T> returnType) {

		HttpPost httpPost = new HttpPost(url);

		RequestConfig requestConfig = RequestConfig.custom() //
				.setConnectionRequestTimeout(HTTP_CLIENT_TIMEOUT) //
				.setSocketTimeout(HTTP_CLIENT_TIMEOUT) //
				.setConnectTimeout(HTTP_CLIENT_TIMEOUT).build();

		httpPost.setConfig(requestConfig);

		httpPost.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE); // application/json
		httpPost.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE); // application/json
		String requestEntityValue = parseObjectString(data);
		httpPost.setEntity(new StringEntity(requestEntityValue, StandardCharsets.UTF_8));

		CloseableHttpClient httpClient = getHttpClient(url);

		try (CloseableHttpResponse httpResponse = httpClient.execute(httpPost)) {
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null) {
				String responseEntityValue = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
				return objectMapper.convertValue(responseEntityValue, returnType);
			} else {
				return null;
			}
		} catch (ParseException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static CloseableHttpClient getHttpClient(String url) {
		if (url.toLowerCase().startsWith("https:")) {
			return acceptsUntrustedCertsHttpClient();
		} else if (url.toLowerCase().startsWith("http:")) {
			return HttpClientBuilder.create().build();
		} else {
			throw new RuntimeException("url must start with 'https:' or 'http:'");
		}
	}

	private static String parseObjectString(Object data) {
		try {
			if (data instanceof String) {
				return data.toString();
			} else if (data instanceof Collection) {
				return objectMapper.writeValueAsString((Collection<?>) data);
			} else if (data instanceof Map) {
				return objectMapper.writeValueAsString((Map<?, ?>) data).toString();
			} else if (data != null) {
				return objectMapper.writeValueAsString(data).toString();
			} else {
				return "";
			}
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * CloseableHttpClient
	 *
	 * @return CloseableHttpClient
	 */
	private static CloseableHttpClient acceptsUntrustedCertsHttpClient() {
		try {
			HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, TrustStrategyImpl.getInstance()).setProtocol("TLSv1.2").build();
			httpClientBuilder.setSSLContext(sslContext);
			HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
			SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create() //
					.register("http", PlainConnectionSocketFactory.getSocketFactory()) //
					.register("https", sslSocketFactory).build();
			PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			connMgr.setMaxTotal(200);
			connMgr.setDefaultMaxPerRoute(100);
			httpClientBuilder.setConnectionManager(connMgr);
			return httpClientBuilder.build();
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			throw new RuntimeException(e);
		}
	}

	private static class TrustStrategyImpl implements TrustStrategy {

		public static TrustStrategy getInstance() {
			return new TrustStrategyImpl();
		}

		@Override
		public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			return true;
		}

	}

	private static final String[] HEADERS_TO_TRY = { "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR" };

	public static String getIP(ServerHttpRequest request) {

		for (String header : HEADERS_TO_TRY) {
			String ip = request.getHeaders().getFirst(header);
			if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
				if (ip.equals("0:0:0:0:0:0:0:1")) {
					ip = "127.0.0.1";
				}
				return ip;
			}
		}
		String ip = request.getRemoteAddress().getAddress().getHostAddress();
		if (ip.equals("0:0:0:0:0:0:0:1")) {
			ip = "127.0.0.1";
		}
		return ip;
	}

	public static String getIP(HttpServletRequest request) {
		for (String header : HEADERS_TO_TRY) {
			String ip = request.getHeader(header);
			if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
				if (ip.equals("0:0:0:0:0:0:0:1")) {
					ip = "127.0.0.1";
				}
				return ip;
			}
		}
		String ip = request.getRemoteAddr();
		if (ip.equals("0:0:0:0:0:0:0:1")) {
			ip = "127.0.0.1";
		}
		return ip;
	}

	public static String getIP(ServletRequest request) {
		String ip = request.getRemoteAddr();
		if (ip.equals("0:0:0:0:0:0:0:1")) {
			ip = "127.0.0.1";
		}
		return ip;
	}

	/**
	 * 取得 request 的 cookie 值
	 * 
	 * @param request
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String getCookieValue(HttpServletRequest request, String key, String defaultValue) {

		if (request == null) {
			return defaultValue;
		}

		Cookie[] cookies = request.getCookies();

		if (cookies == null) {
			return null;
		}

		return Arrays.stream(cookies).filter(c -> c.getName().equals(key)).findFirst().map(Cookie::getValue).orElse(defaultValue);
	}
	
	public static String getURLWithContextPath(HttpServletRequest request) {
		return String.format("%s://%s%s", request.getScheme(), (request.getScheme().toLowerCase().endsWith("s") && request.getServerPort() == 443) || request.getServerPort() == 80 ? request.getServerName() : request.getServerName() + ":" + request.getServerPort(), request.getContextPath());
	}

}
