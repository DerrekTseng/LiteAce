package lite.developer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lite.system.dto.MenuDto;
import lite.system.vo.MenuVo;

@Service
public class DeveloperService {
	static final Logger logger = LoggerFactory.getLogger(DeveloperService.class);

	private static final AtomicInteger menuIndexCount = new AtomicInteger(0);
	private static final AtomicInteger menuSeqCount = new AtomicInteger(0);
	private static final List<MenuVo> testMenu = generateMenu();
	private static final int MENU_GROUP = 0;
	private static final int MENU_PAGE = 1;
	private static final int MENU_LINK = 2;

	public List<MenuDto> getMenuBean() {
		return MenuDto.parseMenuDtos(testMenu);
	}

	private static int getIndex() {
		return menuIndexCount.get();
	}

	private static int addIndex() {
		return menuIndexCount.addAndGet(1);
	}

	/**
	 * <pre>
	 * LiteAce 框架
	 * 	基本介紹
	 * 
	 * 	JavaScript 範例
	 * 		LiteService
	 * 		LiteAce 
	 * 		LiteDialog
	 * 		LitePopup
	 * 		LiteTable
	 * 		LiteWebSockek
	 * 	
	 * 	Java 範例
	 * 		Controller
	 * 		Service
	 * 		WebSocket
	 * 	
	 * 	HTML 範例
	 * 		查詢畫面
	 * 		編輯畫面
	 * 		左右畫面
	 * 		SiteMesh
	 * 	
	 * Spring 架構
	 * 	Spring MVC
	 * 	Spring AOP
	 * 	Spring Scheduler
	 * 	Spring Data
	 * 	Spring Security
	 * 	
	 * Ace Admin 內建
	 * 
	 * 全部圖示
	 * </pre>
	 */
	private static List<MenuVo> generateMenu() {
		List<MenuVo> result = new ArrayList<>();

		/*******************************************************************************************/

		result.add(genMenuDao("LiteAce 框架", null, "menu-icon fa fa-leaf", null, MENU_GROUP));
		{
			int index_1 = getIndex();
			result.add(genMenuDao("基本介紹", "developer/lite-ace/intro", "menu-icon fa fa-caret-right", index_1, MENU_PAGE));
			result.add(genMenuDao("系統開發", "developer/lite-ace/develop", "menu-icon fa fa-caret-right", index_1, MENU_PAGE));

			result.add(genMenuDao("JavaScript 範例", null, "menu-icon fa fa-caret-right", index_1, MENU_GROUP));
			{
				int index_2 = getIndex();
				result.add(genMenuDao("LiteService", "developer/lite-ace/javascript/service", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("LiteAce", "developer/lite-ace/javascript/ace", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("LiteDialog", "developer/lite-ace/javascript/dialog", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("LitePopup", "developer/lite-ace/javascript/popup", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("LiteTable", "developer/lite-ace/javascript/table", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("LiteWebSockek", "developer/lite-ace/javascript/ws", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
			}

			result.add(genMenuDao("Java 範例", null, "menu-icon fa fa-caret-right", index_1, MENU_GROUP));
			{
				int index_2 = getIndex();
				result.add(genMenuDao("Controller", "developer/lite-ace/java/controller", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("Service", "developer/lite-ace/java/service", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("WebSocket", "developer/lite-ace/java/ws", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("SiteMesh", "developer/lite-ace/java/sitemesh", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
			}

		}

		/*******************************************************************************************/

		result.add(genMenuDao("Spring 架構", null, "menu-icon fab fa-envira", null, MENU_GROUP));
		{
			int index_1 = getIndex();
			result.add(genMenuDao("Spring MVC", "developer/spring/mvc", "menu-icon fa fa-caret-right", index_1, MENU_PAGE));
			result.add(genMenuDao("Spring AOP", "developer/spring/aop", "menu-icon fa fa-caret-right", index_1, MENU_PAGE));
			result.add(genMenuDao("Spring Scheduler", "developer/spring/scheduler", "menu-icon fa fa-caret-right", index_1, MENU_PAGE));
			result.add(genMenuDao("Spring Data", "developer/spring/data", "menu-icon fa fa-caret-right", index_1, MENU_PAGE));
			result.add(genMenuDao("Spring Security", "developer/spring/security", "menu-icon fa fa-caret-right", index_1, MENU_PAGE));
		}

		/*******************************************************************************************/

		result.add(genMenuDao("Ace Admin 內建", null, "menu-icon fa fa-desktop", null, MENU_GROUP));
		{
			int index_1 = getIndex();
			result.add(genMenuDao("Dashborad", "developer/ace-admin/dashborad", "menu-icon fa fa-tachometer", index_1, MENU_PAGE));
			result.add(genMenuDao("UI &amp; Elements", null, "menu-icon fa fa-desktop", index_1, MENU_GROUP));
			{
				int index_2 = getIndex();
				result.add(genMenuDao("Typography", "developer/ace-admin/typography", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("Elements", "developer/ace-admin/elements", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("Buttons &amp; Icons", "developer/ace-admin/buttons", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("Content Sliders", "developer/ace-admin/content-slider", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("Treeview", "developer/ace-admin/treeview", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("jQuery UI", "developer/ace-admin/jquery-ui", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("Nestable Lists", "developer/ace-admin/nestable-list", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
			}

			result.add(genMenuDao("Tables", null, "menu-icon fa fa-list", index_1, MENU_GROUP));
			{
				int index_2 = getIndex();
				result.add(genMenuDao("Simple &amp; Dynamic", "developer/ace-admin/tables", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("jqGrid plugin", "developer/ace-admin/jqgrid", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));

			}

			result.add(genMenuDao("Forms", null, "menu-icon fa fa-pencil-square-o", index_1, MENU_GROUP));
			{
				int index_2 = getIndex();
				result.add(genMenuDao("Form Elements", "developer/ace-admin/form-elements", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("Form Elements 2", "developer/ace-admin/form-elements-2", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("Wizard &amp; Validation", "developer/ace-admin/form-wizard", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("Wysiwyg &amp; Markdown", "developer/ace-admin/wysiwyg", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("Dropzone File Upload", "developer/ace-admin/dropzone", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));

			}
			result.add(genMenuDao("Widgets", "developer/ace-admin/widgets", "menu-icon fa fa-list-alt", index_1, MENU_PAGE));
			result.add(genMenuDao("Calendar", "developer/ace-admin/calendar", "menu-icon fa fa-calendar", index_1, MENU_PAGE));
			result.add(genMenuDao("Gallery", "developer/ace-admin/gallery", "menu-icon fa fa-picture-o", index_1, MENU_PAGE));
			result.add(genMenuDao("More Pages", null, "menu-icon fa fa-tag", index_1, MENU_GROUP));
			{
				int index_2 = getIndex();
				result.add(genMenuDao("JwtUser Profile", "developer/ace-admin/profile", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("Inbox", "developer/ace-admin/inbox", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("Pricing Tables", "developer/ace-admin/pricing", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("Invoice", "developer/ace-admin/invoice", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("Timeline", "developer/ace-admin/timeline", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("Search Results", "developer/ace-admin/search", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("Email Templates", "developer/ace-admin/email", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("Login &amp; Register", "developer/ace-admin/login", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));

			}

			result.add(genMenuDao("Other Pages", null, "menu-icon fa fa-file-o", index_1, MENU_GROUP));
			{
				int index_2 = getIndex();
				result.add(genMenuDao("FAQ", "developer/ace-admin/faq", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("Error 404", "developer/ace-admin/error-404", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("Error 500", "developer/ace-admin/error-500", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("Grid", "developer/ace-admin/grid", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));
				result.add(genMenuDao("Blank Page", "developer/ace-admin/blank", "menu-icon fa fa-caret-right", index_2, MENU_PAGE));

			}
		}

		/*******************************************************************************************/

		result.add(genMenuDao("全部圖示", "developer/all-icons", "menu-icon far fa-smile-beam", null, MENU_PAGE));

		result.add(genMenuDao("測試頁面", "developer/test", "menu-icon fa fa-bug", null, MENU_PAGE));

		result.add(genMenuDao("測試連結", "developer/test", "menu-icon fa fa-bug", null, MENU_LINK));

		return result;
	}

	private static MenuVo genMenuDao(String name, String url, String icon, Integer parent, Integer type) {
		MenuVo dao = new MenuVo();
		dao.setId(addIndex());
		dao.setName(name);
		dao.setUrl(url);
		dao.setIcon(icon);
		dao.setParent(parent);
		dao.setSeq(menuSeqCount.addAndGet(1));
		dao.setType(type);
		return dao;
	}
}
