package lite.core.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import lite.tools.DateTools;

public class AuthenticateUser implements Authentication {

	private static final long serialVersionUID = -6217689279010556503L;

	private Integer rowid;

	private String name;

	private String ip;

	private String uuid;

	private boolean authenticated;

	private Long lastAccessTime;

	private List<GrantedAuthority> authorities = new ArrayList<>();

	public AuthenticateUser(Integer rowid, String name, String ip, String uuid) {
		this.rowid = rowid;
		this.name = name;
		this.ip = ip;
		this.uuid = uuid;
		this.lastAccessTime = DateTools.getCurrentDateTimeFormatNumberLong();
	}

	/**
	 * 使用者名稱
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * 使用者權限
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	/**
	 * 客戶端 UUID
	 */
	@Override
	public Object getCredentials() {
		return uuid;
	}

	/**
	 * 客戶端 IP
	 */
	@Override
	public Object getDetails() {
		return ip;
	}

	/**
	 * User RowId
	 */
	@Override
	public Object getPrincipal() {
		return rowid;
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		authenticated = isAuthenticated;
	}

	/**
	 * 最後訪問時間
	 * 
	 * @return
	 */
	public Long getLastAccessTime() {
		return lastAccessTime;
	}

	/**
	 * 最後訪問時間
	 * 
	 * @param lastAccessTime
	 */
	public void setLastAccessTime(Long lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public Integer getRowid() {
		return rowid;
	}

	public void setRowid(Integer rowid) {
		this.rowid = rowid;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setName(String name) {
		this.name = name;
	}

}
