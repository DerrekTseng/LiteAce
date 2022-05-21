package lite.system.vo;

import java.util.List;

import lite.core.security.AuthenticateUser;
import lite.system.dto.MenuDto;

public class UserProfile {

	Integer rowid;

	String name;

	List<MenuDto> menu;

	AuthenticateUser authenticateUser;

	public Integer getRowid() {
		return rowid;
	}

	public void setRowid(Integer rowid) {
		this.rowid = rowid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<MenuDto> getMenu() {
		return menu;
	}

	public void setMenu(List<MenuDto> menu) {
		this.menu = menu;
	}

	public AuthenticateUser getAuthenticateUser() {
		return authenticateUser;
	}

	public void setAuthenticateUser(AuthenticateUser authenticateUser) {
		this.authenticateUser = authenticateUser;
	}

}
