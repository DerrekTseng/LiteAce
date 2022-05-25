package lite.core.websocket;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import lite.core.listeners.SpringApplicationListener;

@Component
public class WebSocketFactory {

	private static final ConcurrentHashMap<String, WebSocketCommunicator<?>> communicators = new ConcurrentHashMap<String, WebSocketCommunicator<?>>();

	private static final ConcurrentHashMap<WebSocketSession, Set<String>> sessions = new ConcurrentHashMap<>();

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	SpringApplicationListener springApplicationListener;

	@PostConstruct
	private synchronized void init() {
		if (springApplicationListener.isApplicationInitialized()) {

			// 掃描 @WebSocketEndpoint() 及 extends WebSocketCommunicator 的 class

			Map<String, WebSocketCommunicator<?>> map = applicationContext.getBeansWithAnnotation(WebSocketEndpoint.class).values().stream().filter(bean -> {
				return WebSocketCommunicator.class.isAssignableFrom(bean.getClass());
			}).map(bean -> {
				WebSocketEndpoint endpoint = AopUtils.getTargetClass(bean).getAnnotation(WebSocketEndpoint.class);
				return new AbstractMap.SimpleEntry<String, WebSocketCommunicator<?>>(endpoint.value(), (WebSocketCommunicator<?>) bean);
			}).collect(Collectors.toMap(Map.Entry<String, WebSocketCommunicator<?>>::getKey, Map.Entry<String, WebSocketCommunicator<?>>::getValue));

			communicators.putAll(map);

		}
	}

	WebSocketCommunicator<?> getCommunicator(String url) {
		return communicators.getOrDefault(url, null);
	}

	public ConcurrentHashMap<WebSocketSession, Set<String>> getSessions() {
		return sessions;
	}

	public void sendMessage(WebSocketSession session, String url, Object data) {
		WebSocketResponseBean webSocketResponseBean = new WebSocketResponseBean();
		webSocketResponseBean.setUrl(url);
		webSocketResponseBean.setData(data);
		sendMessage(session, webSocketResponseBean);
	}

	public void sendMessage(WebSocketSession session, WebSocketResponseBean webSocketResponseBean) {
		TextMessage msg = parseTextMessage(webSocketResponseBean);
		if (msg != null) {
			sendMessage(session, msg);
		}
	}

	private void sendMessage(WebSocketSession session, TextMessage msg) {
		if (msg != null) {
			try {
				session.sendMessage(msg);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private TextMessage parseTextMessage(Object data) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			if (data instanceof TextMessage) {
				return (TextMessage) data;
			} else if (data instanceof String) {
				return new TextMessage(data.toString());
			} else if (data instanceof Collection) {
				return new TextMessage(objectMapper.writeValueAsString((Collection<?>) data));
			} else if (data instanceof Map) {
				return new TextMessage(objectMapper.writeValueAsString((Map<?, ?>) data));
			} else if (data != null) {
				return new TextMessage(objectMapper.writeValueAsString(data));
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}

	}
}
