package lite.core.scheduler;

import org.apache.logging.log4j.ThreadContext;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import lite.core.mybatis.DBProxy;

@Component
@Scope("prototype")
public abstract class ScheduledJob implements Job {

	protected Logger logger;

	@Autowired
	DBProxy dbProxy;

	@Autowired
	ScheduledFactory scheduledFactory;

	/**
	 * 將字串轉換成 ScheduledJob class
	 * 
	 * @param className
	 * @return
	 */
	public static Class<ScheduledJob> parseClass(String className) {
		try {
			Class<?> clazz = Class.forName(className);
			if (ScheduledJob.class.isAssignableFrom(clazz)) {
				@SuppressWarnings("unchecked")
				Class<ScheduledJob> scheduledJobClass = (Class<ScheduledJob>) clazz;
				return scheduledJobClass;
			} else {
				throw new RuntimeException(String.format("class %s is not assignable from lite.core.scheduler.ScheduledJob", clazz.getName()));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		fire(context);
	}

	/**
	 * 啟動 僅為同層 class 可呼叫
	 * 
	 * @param context
	 */
	void fire(JobExecutionContext context) {
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		String id = dataMap.getString("id");

		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);

		ThreadContext.put("jobid", id);
		logger = LoggerFactory.getLogger(this.getClass());

		if (scheduledFactory.isRunning(id)) {
			logger.warn(String.format("job [%s] is running currently.", id));
			return;
		}

		logger.info(String.format("job [%s] started.", id));

		scheduledFactory.setRunning(id, true);

		try {

			String[] args = null;

			// args 看是要從資料庫撈資料還是怎樣
			// 要 insert 資料庫 當作[開始執行]的紀錄

			execute(id, args);

		} catch (Exception e) {

		} finally {

			// 要 update 資料庫 當作[結束執行]的紀錄

			scheduledFactory.setRunning(id, false);
			
			ThreadContext.clearAll();
		}
	}

	/**
	 * 執行
	 * 
	 * @param args
	 */
	public abstract void execute(String id, String[] args);

}
