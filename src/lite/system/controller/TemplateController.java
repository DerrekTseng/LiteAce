package lite.system.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.nio.file.Paths;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lite.core.translang.TranslangSupport;
import lite.tools.HttpTools;

/**
 * 讀取 LiteTemplate.html 並將其轉換成 JS 和 CSS 檔案 傳送給前端使用
 * 
 * @author DerrekTseng
 *
 */
@Controller
@RequestMapping("/resource")
public class TemplateController {

	private static final String TemplatePath = "/WEB-INF/template.html";

	private static volatile File templateHTML = null;

	private static volatile byte[] templateJavascript = null;
	private static volatile byte[] templateStyle = null;

	private static volatile long templateHTML_LastModifiedTime = 0L;

	@Autowired
	ServletContext servletContext;

	@Autowired
	TranslangSupport translangSupport;

	@ResponseBody
	@GetMapping("/script/template.js")
	public void javascript(HttpServletRequest request, HttpServletResponse response) throws IOException {
		load(request);
		response.setHeader("Accept-Ranges", "bytes");
		response.setHeader("Keep-Alive", "timeout=20");
		response.setHeader("cache-control", null);
		response.setHeader("Vary", "Origin, Access-Control-Request-Method, Access-Control-Request-Headers");
		response.setStatus(HttpServletResponse.SC_OK);

		response.setContentType("application/javascript;charset=UTF-8");
		response.setContentLength(templateJavascript.length);
		response.setDateHeader("Last-Modified", templateHTML_LastModifiedTime);
		response.getOutputStream().write(templateJavascript);
	}

	@ResponseBody
	@GetMapping("/style/template.css")
	public void style(HttpServletRequest request, HttpServletResponse response) throws IOException {
		load(request);
		response.setHeader("Accept-Ranges", "bytes");
		response.setHeader("Keep-Alive", "timeout=20");
		response.setHeader("cache-control", null);
		response.setHeader("Vary", "Origin, Access-Control-Request-Method, Access-Control-Request-Headers");
		response.setStatus(HttpServletResponse.SC_OK);

		response.setContentType("text/css;charset=UTF-8");
		response.setContentLength(templateStyle.length);
		response.setDateHeader("Last-Modified", templateHTML_LastModifiedTime);
		response.getOutputStream().write(templateStyle);
	}

	private synchronized void load(HttpServletRequest request) {

		try {
			if (templateHTML == null) {
				templateHTML = Paths.get(servletContext.getResource(TemplatePath).toURI()).toFile();
			}
			if (templateHTML.lastModified() != templateHTML_LastModifiedTime) {
				String language = HttpTools.getCookieValue(request, translangSupport.LANGUAGE_COOKIES_KEY, null);
				Properties translangProperties = translangSupport.getTranslang(language);
				templateHTML_LastModifiedTime = templateHTML.lastModified();
				InputStream inputStream = servletContext.getResourceAsStream(TemplatePath);
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				CharBuffer charBuffer = CharBuffer.allocate(inputStream.available());
				bufferedReader.read(charBuffer);
				charBuffer.flip();
				bufferedReader.close();
				inputStreamReader.close();
				inputStream.close();
				String templateContent = charBuffer.toString();
				Document doc = Jsoup.parse(templateContent);
				templateStyle = minify(doc.getElementsByTag("style").get(0).html()).getBytes("UTF-8");
				String funFormat = "_liteTemplateMap_.set('[%s]', $(\"%s\"));";
				StringBuilder sb = new StringBuilder();
				sb.append("const _liteTemplateMap_ = new Map();").append(" ");
				for (Element element : doc.getElementsByTag("body").get(0).children()) {
					String id = element.attr("id");
					String content = minify(element.html());
					content = translang(content, translangProperties);
					String funResult = String.format(funFormat, id, content);
					sb.append(funResult).append(" ");
				}
				templateJavascript = sb.toString().getBytes("UTF-8");
			}
		} catch (Exception e) {
			templateHTML_LastModifiedTime = 0L;
			throw new RuntimeException(e);
		}
	}

	private String translang(String content, Properties translangProperties) {
		String prefix = "${translang[";
		String suffix = "]}";
		while (content.contains(prefix)) {
			int startIndex = content.indexOf(prefix);
			int endIndex = content.indexOf(suffix, startIndex) + suffix.length();
			String tag = content.substring(startIndex, endIndex);
			String key = tag.replace(prefix, "").replace(suffix, "").replace("'", "").replace("\"", "");
			String replacement = translangProperties.getOrDefault(key, "").toString();
			content = content.replace(tag, replacement);
		}
		return content;
	}

	private String minify(String content) throws UnsupportedEncodingException {
		content = content.replace("\"", "'");
		content = content.replace("\r\n", " ").replace("\n", " ");
		content = content.replace("	", " ");
		while (content.contains("  ")) {
			content = content.replace("  ", " ");
		}
		content = content.replace("> ", ">");
		return content;
	}

}
