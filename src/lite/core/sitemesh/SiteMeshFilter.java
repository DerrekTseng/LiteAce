package lite.core.sitemesh;

import javax.servlet.annotation.WebFilter;

import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.sitemesh.config.ConfigurableSiteMeshFilter;
import org.sitemesh.content.tagrules.html.Sm2TagRuleBundle;

@WebFilter( urlPatterns = "/*", asyncSupported = true)
public class SiteMeshFilter extends ConfigurableSiteMeshFilter {
	@Override
	protected void applyCustomConfiguration(SiteMeshFilterBuilder builder) {
		/** sitemesh3 的 property 只有 title,head,body ，以下設定可讓 sitemesh3 自行定義 property **/
		builder.addTagRuleBundle(new Sm2TagRuleBundle());

		/** sitemesh3 不能每一頁jsp 自行定義樣版(docorator)，以下設定讓 sitemesh3 可以自行定義每一頁的樣版 **/
		builder.setCustomDecoratorSelector(new MetaTagDecoratorSelector(builder.getDecoratorSelector()));			
	}
}
