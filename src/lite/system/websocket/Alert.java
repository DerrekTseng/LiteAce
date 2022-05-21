package lite.system.websocket;

import java.util.Map;

import lite.core.websocket.WebSocketCommunicator;
import lite.core.websocket.WebSocketEndpoint;
import lite.system.dto.AlertDto;
import lite.system.vo.UserProfile;

@WebSocketEndpoint("index/alert")
public class Alert extends WebSocketCommunicator<AlertDto> {

	@Override
	public boolean sendToClientFilter(UserProfile userProfile, AlertDto data) {
		return true;
	}

	@Override
	public AlertDto receiveFromClient(UserProfile userProfile, Map<String, Object> data) {
		return null;
	}

}
