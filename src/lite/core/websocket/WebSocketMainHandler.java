package lite.core.websocket;

import java.util.Map;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lite.system.vo.UserProfile;

@Component
public class WebSocketMainHandler extends TextWebSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(WebSocketMainHandler.class);

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	WebSocketFactory webSocketFactory;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		webSocketFactory.getSessions().add(session);
	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		session.getRemoteAddress();

		UserProfile userProfile = (UserProfile) session.getAttributes().get("userProfile");
		String ip = session.getAttributes().get("ip").toString();

		ThreadContext.put("ip", ip);
		ThreadContext.put("user", userProfile.getName());

		String payload = message.getPayload();

		JsonFactory factory = objectMapper.getFactory();
		JsonParser parser = factory.createParser(payload);
		JsonNode actualObj = objectMapper.readTree(parser);

		String url = actualObj.get("url").asText();
		JsonNode data = actualObj.get("data");

		Map<String, Object> map = objectMapper.convertValue(data, new TypeReferenceImpl());

		WebSocketCommunicator<?> receiver = webSocketFactory.getCommunicator(url);
		if (receiver != null) {
			Object result = receiver.receiveFromClient(userProfile, map);
			if (result != null) {
				webSocketFactory.sendMessage(session, url, result);
			}
		}
	}

	private static class TypeReferenceImpl extends TypeReference<Map<String, Object>> {
		// Jackson convertValue 型別
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		logger.error("{0}", exception);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		webSocketFactory.getSessions().remove(session);
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

}
