package lite.core.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.www.NonceExpiredException;

import lite.tools.HttpTools;

public class AuthenticateProcessingFilter extends AbstractAuthenticationProcessingFilter {

	@Autowired
	AuthenticateUserFactory authenticateUserFactory;

	protected AuthenticateProcessingFilter(String defaultFilterProcessesUrl) {
		super(defaultFilterProcessesUrl);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

		String ip = HttpTools.getIP(request);

		ThreadContext.put("client", ip);

		AuthenticateUser authenticateUser = authenticateUserFactory.getAuthenticateUser();

		if (authenticateUser == null) {
			String token = authenticateUserFactory.getToken(request);

			if (token == null) {
				throw new ProviderNotFoundException("Token not found.");
			}

			authenticateUser = authenticateUserFactory.parseAuthenticateUser(token);

			if (authenticateUser == null) {
				throw new BadCredentialsException("Token parse error.");
			}

			if (!authenticateUser.getDetails().equals(HttpTools.getIP(request))) {
				throw new CookieTheftException("Token ip not matched.");
			}

		}

		ThreadContext.put("client", ip + " | " + authenticateUser.getName());

		if (authenticateUserFactory.isCached(authenticateUser.getUuid(), authenticateUser.getIp())) {

			// 登入成功

		} else {
			throw new NonceExpiredException("Token expired");
		}

		return authenticateUser;
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(authResult);
		chain.doFilter(request, response);
		ThreadContext.clearAll();
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
		super.unsuccessfulAuthentication(request, response, failed);
		ThreadContext.clearAll();
	}

}
