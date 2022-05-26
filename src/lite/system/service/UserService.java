package lite.system.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lite.core.security.AuthenticateUser;
import lite.core.security.AuthenticateUserFactory;
import lite.system.dto.MenuDto;
import lite.system.vo.MenuVo;
import lite.system.vo.UserProfile;
import lite.tools.HttpTools;

@Service
public class UserService {

	public static final String HTTP_SESSION_USER_PROFILE_KEY = "HTTP_SESSION_USER_PROFILE_KEY";

	@Autowired
	AuthenticateUserFactory authenticateUserFactory;

	@Autowired
	HttpServletRequest request;

	@Autowired
	HttpServletResponse response;

	public UserProfile getUserProfile() {

		if (request == null) {
			return null;
		}

		HttpSession httpSession = request.getSession(true);

		AuthenticateUser authenticateUser = null;

		UserProfile userProfile = null;

		Object sessionUserProfile = httpSession.getAttribute(HTTP_SESSION_USER_PROFILE_KEY);

		if (sessionUserProfile != null && UserProfile.class.isAssignableFrom(sessionUserProfile.getClass())) {
			userProfile = (UserProfile) sessionUserProfile;
			authenticateUser = userProfile.getAuthenticateUser();
		}

		if (authenticateUser != null) {
			if (authenticateUserFactory.isCached(authenticateUser.getUuid(), authenticateUser.getIp())) {
				return userProfile;
			}
		}

		if (authenticateUser == null) {
			authenticateUser = authenticateUserFactory.getAuthenticateUser();
		}

		if (authenticateUser == null) {
			authenticateUser = authenticateUserFactory.getAuthenticateUser(request);
		}

		if (authenticateUser == null) {
			httpSession.removeAttribute(HTTP_SESSION_USER_PROFILE_KEY);
			return null;
		}

		if (!authenticateUserFactory.isCached(authenticateUser.getUuid(), authenticateUser.getIp())) {
			httpSession.removeAttribute(HTTP_SESSION_USER_PROFILE_KEY);
			return null;
		}

		userProfile = parseUserProfile(authenticateUser);

		httpSession.setAttribute(HTTP_SESSION_USER_PROFILE_KEY, userProfile);

		return userProfile;
	}

	/**
	 * 將 JwtUser 轉換成 UserProfile
	 * 
	 * @param user
	 * @return
	 */
	public UserProfile parseUserProfile(AuthenticateUser authenticateUser) {

		// 檢查資料庫 User RowId 是否存在

		UserProfile userProfile = new UserProfile();
		userProfile.setName(authenticateUser.getName());
		userProfile.setRowid(authenticateUser.getRowid());

		List<MenuVo> menuVos = new ArrayList<>(); // 從資料庫撈出 menu

		List<MenuDto> menuDto = MenuDto.parseMenuDtos(menuVos);
		userProfile.setMenu(menuDto);
		userProfile.setAuthenticateUser(authenticateUser);

		return userProfile;
	}

	public boolean doLogin(String name, String pwd) throws IOException, ServletException {

		if (request == null || response == null) {
			return false;
		}

		name = name.toLowerCase();

		// String pwd_hash = CryptoTools.oneWayHash.MD5(pwd);
		// 或 String pwd_hash = CryptoTools.oneWayHash.SHA256(pwd);
		// 或 String pwd_hash = CryptoTools.oneWayHash.SHA512(pwd);

		// 檢核資料庫 name 和 pwd_hash 是否正確

		Integer userRowid = 0;

		String ip = HttpTools.getIP(request);

		AuthenticateUser authenticateUser = new AuthenticateUser(userRowid, name, ip, UUID.randomUUID().toString());

		authenticateUserFactory.addCache(authenticateUser);

		String token = authenticateUserFactory.parseToken(authenticateUser);

		response.addCookie(authenticateUserFactory.makeCookie(token));

		response.setHeader(AuthenticateUserFactory.TOKEN_KEY_NAME, token);

		HttpSession httpSession = request.getSession(true);

		UserProfile userProfile = parseUserProfile(authenticateUser);

		httpSession.setAttribute(HTTP_SESSION_USER_PROFILE_KEY, userProfile);

		return true;
	}

	public void doLogout() {

		if (response != null && request != null) {

			authenticateUserFactory.removeCache(getUserProfile().getAuthenticateUser().getCredentials().toString());

			request.getSession(true).removeAttribute(HTTP_SESSION_USER_PROFILE_KEY);

			response.addCookie(authenticateUserFactory.makeCookie(""));
			
		}

		SecurityContextHolder.clearContext();

	}

}
