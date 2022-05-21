package lite.system.dto;

public class NotificationDto {

	Integer rowid = Double.valueOf(Math.random()).intValue();

	String icon;

	String msg;

	String url;

	Object data;

	public NotificationDto() {

	}

	public NotificationDto(String msg) {
		this.icon = "ace-icon fa fa-comment";
		this.msg = msg;
	}

	public NotificationDto(String icon, String msg) {
		this.icon = icon;
		this.msg = msg;
	}

	public NotificationDto(String icon, String msg, String url, Object data) {
		this.icon = icon;
		this.msg = msg;
		this.url = url;
		this.data = data;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
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

	public Integer getRowid() {
		return rowid;
	}

	public void setRowid(Integer rowid) {
		this.rowid = rowid;
	}

}
