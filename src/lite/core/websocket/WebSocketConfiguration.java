package lite.core.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

	private static final String LiteAceWebSocketMainEndpointUrl = "/ws";

	@Autowired
	WebSocketMainHandler webSocketMainHandler;

	@Autowired
	WebSocketHandshake webSocketHandshake;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(webSocketMainHandler, LiteAceWebSocketMainEndpointUrl).addInterceptors(webSocketHandshake);
	}

}
