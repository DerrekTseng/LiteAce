package lite.system.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import lite.system.enums.MenuType;
import lite.system.vo.MenuVo;

public class MenuDto {

	Integer id;

	String name;

	String icon;

	String url;

	MenuType menuType;

	Integer seq;

	List<MenuDto> submenu = new ArrayList<>();

	public MenuDto() {

	}

	public MenuDto(MenuVo dao) {
		this.setId(dao.getId());
		this.setName(dao.getName());
		this.setIcon(dao.getIcon());
		this.setSeq(dao.getSeq());
		if (dao.getType() == 0) {
			this.setMenuType(MenuType.GROUP);
			this.setUrl(null);
			this.setSubmenu(new ArrayList<>());
		} else if (dao.getType() == 1) {
			this.setMenuType(MenuType.PAGE);
			this.setUrl(dao.getUrl());
			this.setSubmenu(null);
		} else if (dao.getType() == 2) {
			this.setMenuType(MenuType.LINK);
			this.setUrl(dao.getUrl());
			this.setSubmenu(null);
		}
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<MenuDto> getSubmenu() {
		return submenu;
	}

	public void setSubmenu(List<MenuDto> submenu) {
		this.submenu = submenu;
	}

	public MenuType getMenuType() {
		return menuType;
	}

	public void setMenuType(MenuType menuType) {
		this.menuType = menuType;
	}

	public static List<MenuDto> parseMenuDtos(List<MenuVo> menuVos) {
		
		List<MenuDto> menuDtos = menuVos.stream().filter(menuDao -> {
			return menuDao.getParent() == null;
		}).map(menuDao -> new MenuDto(menuDao)).collect(Collectors.toList());
		
		walkMenuDtos(menuDtos, menuVos);
		sortMenuDtoList(menuDtos);
		return menuDtos;
	}

	private static void walkMenuDtos(List<MenuDto> menuDtos, List<MenuVo> menuVos) {
		menuDtos.stream().filter(menuDto -> {
			return menuDto.getMenuType() == MenuType.GROUP;
		}).forEach(menuDto -> {
			List<MenuDto> submenu = menuDto.getSubmenu();
			AtomicBoolean hasSubmenu = new AtomicBoolean(false);
			menuVos.stream().filter(menuDao -> {
				return menuDao.getParent() == menuDto.getId();
			}).collect(Collectors.groupingBy(MenuVo::getType)).forEach((type, daos) -> {
				submenu.addAll(daos.stream().map(dao -> new MenuDto(dao)).collect(Collectors.toList()));
				hasSubmenu.set(type == 0 || hasSubmenu.get());
			});
			if (hasSubmenu.get()) {
				walkMenuDtos(submenu, menuVos);
			}
		});
	}

	private static void sortMenuDtoList(List<MenuDto> menuDtos) {
		Collections.sort(menuDtos, (o1, o2) -> o1.getSeq().compareTo(o2.getSeq()));
		menuDtos.stream().filter(menuDto -> menuDto.getSubmenu() != null).forEach(menuDto -> sortMenuDtoList(menuDto.getSubmenu()));
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

}
