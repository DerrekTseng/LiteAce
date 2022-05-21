package lite.core.security;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lite.core.listeners.SpringApplicationListener;
import lite.tools.HttpTools;

@Component
public class AuthenticateUserFactory {

	public static final String TOKEN_KEY_NAME = "authorization";

	private static final String secret = "MTIz";

	private static final ConcurrentHashMap<String, String> tokenCache = new ConcurrentHashMap<String, String>();

	@Autowired
	SpringApplicationListener springApplicationListener;

	@PostConstruct
	private synchronized void postConstruct() {
		if (springApplicationListener.isApplicationInitialized()) {

			// 這裡可以抓資料庫 重啟時 把 cached token 抓出來

		}
	}

	public String getToken(HttpServletRequest request) {
		String token = request.getHeader(TOKEN_KEY_NAME);
		if (token == null) {
			token = HttpTools.getCookieValue(request, TOKEN_KEY_NAME, null);
		}
		return token;
	}

	public Cookie makeCookie(String token) {
		Cookie cookie = new Cookie(TOKEN_KEY_NAME, token);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setComment(TOKEN_KEY_NAME);
		if (StringUtils.isBlank(token)) {
			cookie.setMaxAge(0);
		} else {
			cookie.setMaxAge(Integer.MAX_VALUE);
		}
		return cookie;
	}

	public AuthenticateUser parseAuthenticateUser(String token) {
		try {
			Claims body = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
			Integer rowid = (Integer) body.get("rowid");
			String name = (String) body.get("name");
			String ip = (String) body.get("ip");
			String uuid = (String) body.get("uuid");
			AuthenticateUser user = new AuthenticateUser(rowid, name, ip, uuid);
			return user;
		} catch (JwtException | ClassCastException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public String parseToken(AuthenticateUser user) {
		Claims claims = Jwts.claims().setSubject("");
		claims.put("rowid", user.getRowid());
		claims.put("name", user.getName());
		claims.put("ip", user.getIp());
		claims.put("uuid", user.getUuid());
		return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secret).compact();
	}

	public AuthenticateUser getAuthenticateUser(HttpServletRequest request) {
		String token = getToken(request);
		return parseAuthenticateUser(token);
	}

	public AuthenticateUser getAuthenticateUser() {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
		if (authentication == null) {
			return null;
		} else {
			return (AuthenticateUser) authentication;
		}
	}

	public void addCache(AuthenticateUser authenticateUser) {
		tokenCache.put(authenticateUser.getUuid(), authenticateUser.getIp());

		// 在資料庫 insert token

	}

	public boolean isCached(String uuid, String ip) {
		if (tokenCache.containsKey(uuid)) {
			return tokenCache.get(uuid).equals(ip);
		} else {
			return false;
		}
	}

	public void removeCache(String uuid) {
		tokenCache.remove(uuid);

		// 從資料庫 delete token
	}

}
