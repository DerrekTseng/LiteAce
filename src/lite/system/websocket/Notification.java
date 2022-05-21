package lite.system.websocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lite.core.websocket.WebSocketCommunicator;
import lite.core.websocket.WebSocketEndpoint;
import lite.system.dto.NotificationDto;
import lite.system.vo.UserProfile;

@WebSocketEndpoint("index/notifications")
public class Notification extends WebSocketCommunicator<List<NotificationDto>> {

	@Override
	public boolean sendToClientFilter(UserProfile userProfile, List<NotificationDto> data) {
		return true;
	}

	@Override
	public List<NotificationDto> receiveFromClient(UserProfile userProfile, Map<String, Object> data) {
		List<NotificationDto> notifications = new ArrayList<>();
		notifications.add(new NotificationDto("Hello Notification"));
		return notifications;
	}

}
