package lite.core.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;

import lite.core.scheduler.ScheduledFactory;

public class SpringApplicationListener implements ApplicationListener<ApplicationContextEvent> {

	Resource log4j2ConfigLocation;

	private LoggerContext loggerContext = null;

	private static boolean isApplicationInitialized = false;

	@Autowired
	ScheduledFactory scheduledFactory;

	@Override
	public void onApplicationEvent(ApplicationContextEvent event) {
		if (event instanceof ContextRefreshedEvent) {
			if (!isApplicationInitialized) {
				configLogging();
				scheduledFactory.start();
				isApplicationInitialized = true;
			}
		} else if (event instanceof ContextClosedEvent) {
			if (isApplicationInitialized) {
				loggerContext.close();
				scheduledFactory.close();
				isApplicationInitialized = false;
			}
		}
	}

	private void configLogging() {
		try {

			// 啟用 JavaScript
			System.setProperty("log4j2.Script.enableLanguages", "JavaScript");

			// 載入 log4j2 的設定檔
			loggerContext = Configurator.initialize(null, log4j2ConfigLocation.getFile().getAbsolutePath());

			// 設定 MyBatis 的 log 為 slf4j
			org.apache.ibatis.logging.LogFactory.useSlf4jLogging();

			// 設定 log4jdbc 的 log 為 slf4j
			System.setProperty("log4jdbc.spylogdelegator.name", "net.sf.log4jdbc.log.slf4j.Slf4jSpyLogDelegator");

			// 為 log4jdbc 的 DriverSpy 內加入額為的 Driver
			List<String> additionalDrivers = new ArrayList<>();
			additionalDrivers.add("org.sqlite.JDBC");
			additionalDrivers.add("com.mysql.cj.jdbc.Driver");
			System.setProperty("log4jdbc.drivers", additionalDrivers.stream().collect(Collectors.joining(",")));

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public Resource getLog4j2ConfigLocation() {
		return log4j2ConfigLocation;
	}

	public void setLog4j2ConfigLocation(Resource log4j2ConfigLocation) {
		this.log4j2ConfigLocation = log4j2ConfigLocation;
	}

	public boolean isApplicationInitialized() {
		return isApplicationInitialized;
	}

}
