package lite.core.translang;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class TranslangViewInterceptor implements AsyncHandlerInterceptor {

	@Autowired
	TranslangSupport translangSupport;

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

		if (modelAndView != null) {
			
			String language = translangSupport.getClientLanguage(request);
			
			modelAndView.addObject("translang", translangSupport.getTranslang(language));
			
		}

	}

}
