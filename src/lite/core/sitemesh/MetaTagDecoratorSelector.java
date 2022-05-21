package lite.core.sitemesh;

import java.io.IOException;

import org.sitemesh.DecoratorSelector;
import org.sitemesh.content.Content;
import org.sitemesh.webapp.WebAppContext;

/**
 * 讓 sitemesh3 可以使用 sitemesh2 的 <meta name="decorator" content="xxxxx"> 的用法 ps:
 * sitemesh3 無法指定某張頁面套用那一個 decorator(樣版)，sitemesh 2 才行
 *
 */
public class MetaTagDecoratorSelector implements DecoratorSelector<WebAppContext> {

	private final DecoratorSelector<WebAppContext> fallbackSelector;

	public MetaTagDecoratorSelector(DecoratorSelector<WebAppContext> fallbackSelector) {
		this.fallbackSelector = fallbackSelector;
	}

	@Override
	public String[] selectDecoratorPaths(Content content, WebAppContext context) throws IOException {
		String decorator = content.getExtractedProperties().getChild("meta").getChild("decorator").getValue();
		if (decorator != null) {
			return decorator.split(",");
		} else {
			return fallbackSelector.selectDecoratorPaths(content, context);
		}
	}

}