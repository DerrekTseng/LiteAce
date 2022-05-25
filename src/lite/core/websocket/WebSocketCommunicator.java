package lite.core.websocket;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import lite.system.vo.UserProfile;

/**
 * WebSocket 通訊
 * 
 * @author DerrekTseng
 *
 * @param <T> 資料型別
 */
public abstract class WebSocketCommunicator<T> {

	@Autowired
	WebSocketFactory webSocketFactory;

	/**
	 * 傳送資料給全部有連線的客戶端，可以利用實作 sendToClientFilter 來過濾不傳送的對象
	 * 
	 * @param data
	 * @throws Exception
	 */
	public final void sendToClient(T data) {
		WebSocketEndpoint endpoint = this.getClass().getDeclaredAnnotation(WebSocketEndpoint.class);
		webSocketFactory.getSessions().entrySet().stream().filter(entry -> {
			return entry.getValue().contains(endpoint.value());
		}).map(entry -> entry.getKey()).forEach(session -> {
			UserProfile userProfile = (UserProfile) session.getAttributes().get("userProfile");
			if (sendToClientFilter(userProfile, data)) {
				try {
					webSocketFactory.sendMessage(session, endpoint.value(), data);
				} catch (Exception e) {

				}
			}
		});
	}

	/**
	 * 傳送資料給客戶端過濾器
	 * 
	 * @param jwtUser
	 * @param data
	 * @return false = 不傳送
	 */
	public abstract boolean sendToClientFilter(UserProfile userProfile, T data);

	/**
	 * 從客戶端接收資料
	 * 
	 * @param userProfile
	 * @param data
	 * @return 回傳 null 則不發送訊息
	 */
	public abstract T receiveFromClient(UserProfile userProfile, Map<String, Object> data);

}
