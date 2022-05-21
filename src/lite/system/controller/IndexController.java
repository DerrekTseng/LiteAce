package lite.system.controller;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import lite.core.mybatis.DBProxy;
import lite.core.translang.TranslangSupport;
import lite.developer.DevelopingGlobalSettings;
import lite.system.dto.MenuDto;
import lite.system.service.UserService;
import lite.system.vo.UserProfile;

@Controller
@RequestMapping
public class IndexController {

	protected static final Logger logger = LoggerFactory.getLogger(IndexController.class);

	@Autowired
	UserService userService;

	@Autowired
	TranslangSupport translangSupport;

	@Autowired
	DBProxy dbProxy;

	@GetMapping
	public ModelAndView index() {

		if (DevelopingGlobalSettings.RedirectToDeveloper) {
			return new ModelAndView("redirect:/developer");
		}

		UserProfile userProfile = userService.getUserProfile();
		ModelAndView view;
		if (userProfile == null) {
			view = new ModelAndView("system/login");
		} else {
			view = new ModelAndView("system/index");
			view.addObject("user", userProfile.getName());
		}
		return view;
	}

	@ResponseBody
	@PostMapping("getMenu")
	public List<MenuDto> getMenu() {
		return userService.getUserProfile().getMenu();
	}

	@ResponseBody
	@PostMapping("doLogin")
	public boolean doLogin(String name, String pwd) throws IOException, ServletException {
		return userService.doLogin(name, pwd);
	}

	@GetMapping("openLanguage")
	public ModelAndView openLanguage(HttpServletRequest request) {
		ModelAndView view = new ModelAndView("system/language");
		String language = translangSupport.getClientLanguage(request);
		view.addObject("LANGUAGE_COOKIES_KEY", translangSupport.LANGUAGE_COOKIES_KEY);
		view.addObject("language", language);
		view.addObject("defaultLanguage", translangSupport.getDefalutKey());
		return view;
	}

	@ResponseBody
	@PostMapping("getLanguages")
	public Properties getLanguages() {
		return translangSupport.getLanguages();
	}

	@ResponseBody
	@PostMapping("doLogout")
	public void doLogout() {
		userService.doLogout();
	}

	@ResponseBody
	@PostMapping("readAllNotifications")
	public void readAllNotifications() {

	}

	@ResponseBody
	@PostMapping("readNotification")
	public void readNotification(Integer rowid) {

	}

}
