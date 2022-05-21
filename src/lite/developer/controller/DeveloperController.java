package lite.developer.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import lite.developer.service.DeveloperService;
import lite.system.dto.MenuDto;

@Controller
@RequestMapping("developer")
public class DeveloperController {

	public static final Logger logger = LoggerFactory.getLogger(DeveloperController.class);
	
	@Autowired
	DeveloperService developService;

	@GetMapping
	public ModelAndView index() {
		
		logger.info("Hello Developer");
		ModelAndView view = new ModelAndView("developer/developer");
		return view;
	}

	@ResponseBody
	@PostMapping("getMenu")
	public List<MenuDto> getMenu() {
		return developService.getMenuBean();
	}

	@GetMapping("lite-ace/intro")
	public ModelAndView intro() {
		return new ModelAndView("developer/lite-ace/intro");
	}

	@GetMapping("lite-ace/develop")
	public ModelAndView develop() {
		return new ModelAndView("developer/lite-ace/develop");
	}
	
	@GetMapping("lite-ace/java/sitemesh")
	public ModelAndView jsp_sitemesh() {
		return new ModelAndView("developer/lite-ace/java/sitemesh");
	}

	@GetMapping("lite-ace/java/controller")
	public ModelAndView java_controller() {
		return new ModelAndView("developer/lite-ace/java/controller");
	}

	@GetMapping("lite-ace/java/service")
	public ModelAndView java_service() {
		return new ModelAndView("developer/lite-ace/java/service");
	}

	@GetMapping("lite-ace/java/ws")
	public ModelAndView java_ws() {
		return new ModelAndView("developer/lite-ace/java/ws");
	}

	@GetMapping("lite-ace/javascript/ace")
	public ModelAndView javascript_ace() {
		return new ModelAndView("developer/lite-ace/javascript/lite-ace-js");
	}

	@GetMapping("lite-ace/javascript/dialog")
	public ModelAndView javascript_dialog() {
		return new ModelAndView("developer/lite-ace/javascript/lite-dialog-js");
	}

	@GetMapping("lite-ace/javascript/popup")
	public ModelAndView javascript_popup() {
		return new ModelAndView("developer/lite-ace/javascript/lite-popup-js");
	}

	@GetMapping("lite-ace/javascript/service")
	public ModelAndView javascript_service() {
		return new ModelAndView("developer/lite-ace/javascript/lite-service-js");
	}

	@GetMapping("lite-ace/javascript/table")
	public ModelAndView javascript_table() {
		return new ModelAndView("developer/lite-ace/javascript/lite-table-js");
	}

	@GetMapping("lite-ace/javascript/ws")
	public ModelAndView javascript_ws() {
		return new ModelAndView("developer/lite-ace/javascript/lite-ws-js");
	}

	@GetMapping("spring/aop")
	public ModelAndView spring_aop() {
		return new ModelAndView("developer/spring/aop");
	}

	@GetMapping("spring/data")
	public ModelAndView spring_data() {
		return new ModelAndView("developer/spring/data");
	}

	@GetMapping("spring/mvc")
	public ModelAndView spring_mvc() {
		return new ModelAndView("developer/spring/mvc");
	}

	@GetMapping("spring/scheduler")
	public ModelAndView spring_scheduler() {
		return new ModelAndView("developer/spring/scheduler");
	}

	@GetMapping("spring/security")
	public ModelAndView spring_security() {
		return new ModelAndView("developer/spring/security");
	}

	@GetMapping("ace-admin/dashborad")
	public ModelAndView dashborad() {
		return new ModelAndView("developer/ace-admin/dashborad");
	}

	@GetMapping("ace-admin/typography")
	public ModelAndView typography() {
		return new ModelAndView("developer/ace-admin/typography");
	}

	@GetMapping("ace-admin/elements")
	public ModelAndView elements() {
		return new ModelAndView("developer/ace-admin/elements");
	}

	@GetMapping("ace-admin/buttons")
	public ModelAndView buttons() {
		return new ModelAndView("developer/ace-admin/buttons");
	}

	@GetMapping("ace-admin/content-slider")
	public ModelAndView contentSlider() {
		return new ModelAndView("developer/ace-admin/content-slider");
	}

	@GetMapping("ace-admin/treeview")
	public ModelAndView treeview() {
		return new ModelAndView("developer/ace-admin/treeview");
	}

	@GetMapping("ace-admin/jquery-ui")
	public ModelAndView jqueryui() {
		return new ModelAndView("developer/ace-admin/jquery-ui");
	}

	@GetMapping("ace-admin/nestable-list")
	public ModelAndView nestableList() {
		return new ModelAndView("developer/ace-admin/nestable-list");
	}

	@GetMapping("ace-admin/tables")
	public ModelAndView tables() {
		return new ModelAndView("developer/ace-admin/tables");
	}

	@GetMapping("ace-admin/jqgrid")
	public ModelAndView jqgrid() {
		return new ModelAndView("developer/ace-admin/jqgrid");
	}

	@GetMapping("ace-admin/form-elements")
	public ModelAndView formElements() {
		return new ModelAndView("developer/ace-admin/form-elements");
	}

	@GetMapping("ace-admin/form-elements-2")
	public ModelAndView formElements2() {
		return new ModelAndView("developer/ace-admin/form-elements-2");
	}

	@GetMapping("ace-admin/form-wizard")
	public ModelAndView formWizard() {
		return new ModelAndView("developer/ace-admin/form-wizard");
	}

	@GetMapping("ace-admin/wysiwyg")
	public ModelAndView wysiwyg() {
		return new ModelAndView("developer/ace-admin/wysiwyg");
	}

	@GetMapping("ace-admin/dropzone")
	public ModelAndView dropzone() {
		return new ModelAndView("developer/ace-admin/dropzone");
	}

	@GetMapping("ace-admin/widgets")
	public ModelAndView widgets() {
		return new ModelAndView("developer/ace-admin/widgets");
	}

	@GetMapping("ace-admin/calendar")
	public ModelAndView calendar() {
		return new ModelAndView("developer/ace-admin/calendar");
	}

	@GetMapping("ace-admin/gallery")
	public ModelAndView gallery() {
		return new ModelAndView("developer/ace-admin/gallery");
	}

	@GetMapping("ace-admin/profile")
	public ModelAndView profile() {
		return new ModelAndView("developer/ace-admin/profile");
	}

	@GetMapping("ace-admin/inbox")
	public ModelAndView inbox() {
		return new ModelAndView("developer/ace-admin/inbox");
	}

	@GetMapping("ace-admin/pricing")
	public ModelAndView pricing() {
		return new ModelAndView("developer/ace-admin/pricing");
	}

	@GetMapping("ace-admin/invoice")
	public ModelAndView invoice() {
		return new ModelAndView("developer/ace-admin/invoice");
	}

	@GetMapping("ace-admin/timeline")
	public ModelAndView timeline() {
		return new ModelAndView("developer/ace-admin/timeline");
	}

	@GetMapping("ace-admin/search")
	public ModelAndView search() {
		return new ModelAndView("developer/ace-admin/search");
	}

	@GetMapping("ace-admin/email")
	public ModelAndView email() {
		return new ModelAndView("developer/ace-admin/email");
	}

	@GetMapping("ace-admin/email-confirmation")
	public ModelAndView email_confirmation() {
		return new ModelAndView("developer/ace-admin/email-confirmation");
	}

	@GetMapping("ace-admin/email-contrast")
	public ModelAndView email_contrast() {
		return new ModelAndView("developer/ace-admin/email-contrast");
	}

	@GetMapping("ace-admin/email-navbar")
	public ModelAndView email_navbar() {
		return new ModelAndView("developer/ace-admin/email-navbar");
	}

	@GetMapping("ace-admin/email-newsletter")
	public ModelAndView email_newsletter() {
		return new ModelAndView("developer/ace-admin/email-newsletter");
	}

	@GetMapping("ace-admin/login")
	public ModelAndView login() {
		return new ModelAndView("developer/ace-admin/login");
	}

	@GetMapping("ace-admin/faq")
	public ModelAndView faq() {
		return new ModelAndView("developer/ace-admin/faq");
	}

	@GetMapping("ace-admin/error-404")
	public ModelAndView error404() {
		return new ModelAndView("developer/ace-admin/error-404");
	}

	@GetMapping("ace-admin/error-500")
	public ModelAndView error500() {
		return new ModelAndView("developer/ace-admin/error-500");
	}

	@GetMapping("ace-admin/grid")
	public ModelAndView grid() {
		return new ModelAndView("developer/ace-admin/grid");
	}

	@GetMapping("ace-admin/blank")
	public ModelAndView blank() {
		return new ModelAndView("developer/ace-admin/blank");
	}

	@GetMapping("all-icons")
	public ModelAndView all_icons() {
		return new ModelAndView("developer/all-icons");
	}
	
	@GetMapping("test")
	public ModelAndView test() {
		return new ModelAndView("developer/test");
	}
}
