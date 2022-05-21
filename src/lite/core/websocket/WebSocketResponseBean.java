package lite.core.websocket;

public class WebSocketResponseBean {
	String url;
	Object data;

	public WebSocketResponseBean() {

	}

	public WebSocketResponseBean(String url, Object data) {
		this.url = url;
		this.data = data;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
