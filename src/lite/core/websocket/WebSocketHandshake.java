package lite.core.websocket;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import lite.system.service.UserService;
import lite.system.vo.UserProfile;
import lite.tools.HttpTools;

@Component
public class WebSocketHandshake implements HandshakeInterceptor {

	@Autowired
	UserService userService;

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

		UserProfile userProfile = userService.getUserProfile();

		attributes.put("userProfile", userProfile);
		attributes.put("ip", HttpTools.getIP(request));

		return userProfile != null;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

	}

}
