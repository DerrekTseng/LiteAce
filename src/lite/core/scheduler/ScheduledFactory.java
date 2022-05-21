package lite.core.scheduler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import lite.core.listeners.SpringApplicationListener;

@Component
public class ScheduledFactory {

	protected static final Logger logger = LoggerFactory.getLogger(ScheduledFactory.class);

	private static final String SCHEDULER_GROUP_NAME = "scheduled";

	@Autowired
	Scheduler scheduler;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	SpringApplicationListener springApplicationListener;

	private static final ConcurrentHashMap<String, ScheduleVo> scheduleVoMap = new ConcurrentHashMap<>();

	@PostConstruct
	private synchronized void init() throws SchedulerException {

		if (springApplicationListener.isApplicationInitialized()) {

			// 掃描 @Scheduled 及 extends ScheduledJob 的 class

			applicationContext.getBeansWithAnnotation(Scheduled.class).values().stream().filter(bean -> {
				return ScheduledJob.class.isAssignableFrom(bean.getClass());
			}).forEach(bean -> {
				try {
					@SuppressWarnings("unchecked")
					Class<ScheduledJob> clazz = (Class<ScheduledJob>) AopUtils.getTargetClass(bean);
					Scheduled scheduledAnnotation = clazz.getDeclaredAnnotation(Scheduled.class);
					String id = scheduledAnnotation.id();
					String scheduled = scheduledAnnotation.scheduled();
					boolean enabled = scheduledAnnotation.enabled();
					addJob(id, scheduled, clazz, enabled);
				} catch (SchedulerException e) {
					throw new RuntimeException(e);
				}
			});

		}
	}

	/**
	 * 取得已加入的 job id
	 * 
	 * @return
	 */
	public String[] getJobs() {
		return scheduleVoMap.keySet().stream().toArray(String[]::new);
	}

	/**
	 * job 是否在執行
	 * 
	 * @param id
	 * @return
	 */
	public boolean isRunning(String id) {
		ScheduleVo scheduleVo = getScheduleVo(id);
		return scheduleVo.isRunning;
	}

	/**
	 * 設定 job 是否執行
	 * 
	 * @param id
	 * @param isRunning
	 */
	void setRunning(String id, boolean isRunning) {
		ScheduleVo scheduleVo = getScheduleVo(id);
		scheduleVo.isRunning = isRunning;
	}

	/**
	 * 設定 job 是否啟用
	 * 
	 * @param id
	 * @param enabled
	 * @throws SchedulerException
	 */
	public void setEnabled(String id, boolean enabled) throws SchedulerException {
		ScheduleVo scheduleVo = getScheduleVo(id);
		if (scheduleVo.enabled != enabled) {
			scheduleVo.enabled = enabled;
			if (enabled) {
				scheduleJob(scheduleVo);
			} else {
				JobKey jobKey = new JobKey(id, SCHEDULER_GROUP_NAME);
				scheduler.deleteJob(jobKey);
			}
		}
	}

	/**
	 * job 是否啟用
	 * 
	 * @param id
	 * @return
	 */
	public boolean isEnabled(String id) {
		ScheduleVo scheduleVo = getScheduleVo(id);
		return scheduleVo.enabled;
	}

	/**
	 * 執行 job
	 * 
	 * @param id
	 * @throws SchedulerException
	 */
	public void fireJob(String id) throws SchedulerException {
		ScheduleVo scheduleVo = getScheduleVo(id);
		if (scheduleVo.isRunning) {
			throw new SchedulerException(String.format("job [%s] is running currently.", id));
		} else if (scheduleVo.enabled) {
			JobKey jobKey = new JobKey(id, SCHEDULER_GROUP_NAME);
			scheduler.triggerJob(jobKey);
		} else {
			new Thread(() -> {
				ScheduledJob scheduledJob = newScheduledJobInstance(scheduleVo.jobClass);
				Map<String, Object> dataMap = new HashMap<>();
				dataMap.put("id", id);
				ScheduledExecutionContext scheduledExecutionContext = new ScheduledExecutionContext(dataMap);
				scheduledJob.fire(scheduledExecutionContext);
			}).start();
		}
	}

	private ScheduledJob newScheduledJobInstance(Class<? extends ScheduledJob> clazz) {
		try {
			return clazz.getConstructor().newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 取得 job 下次執行時間，null 表示未啟用
	 * 
	 * @param id
	 * @return
	 * @throws SchedulerException
	 */
	public Date getNextFireTime(String id) throws SchedulerException {
		ScheduleVo scheduleVo = getScheduleVo(id);
		if (scheduleVo.enabled) {
			TriggerKey triggerKey = new TriggerKey(id, SCHEDULER_GROUP_NAME);
			return scheduler.getTrigger(triggerKey).getNextFireTime();
		} else {
			return null;
		}
	}

	/**
	 * 加入 job
	 * 
	 * @param id
	 * @param scheduled quartz cron expression
	 * @param jobClass  job 的 class
	 * @param enabled   是否啟用
	 * @throws SchedulerException
	 */
	public void addJob(String id, String scheduled, Class<? extends ScheduledJob> jobClass, boolean enabled) throws SchedulerException {
		if (scheduleVoMap.containsKey(id)) {
			throw new SchedulerException(String.format("job id [%s] is already existed.", id));
		} else {
			logger.info("job id={}, class={} added.", id, jobClass.getName());
			ScheduleVo scheduledVo = new ScheduleVo(id, scheduled, jobClass, enabled);
			scheduleVoMap.put(id, scheduledVo);
			if (enabled) {
				scheduleJob(scheduledVo);
			}
		}
	}

	/**
	 * 將 job 加入並啟用
	 * 
	 * @param scheduledVo
	 * @throws SchedulerException
	 */
	private void scheduleJob(ScheduleVo scheduledVo) throws SchedulerException {
		JobDetail jobDetail = JobBuilder.newJob(scheduledVo.jobClass).withIdentity(scheduledVo.id, SCHEDULER_GROUP_NAME).storeDurably(true).build();
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
		jobDataMap.put("id", scheduledVo.id);
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(scheduledVo.id, SCHEDULER_GROUP_NAME).withSchedule(CronScheduleBuilder.cronSchedule(scheduledVo.scheduled)).build();
		scheduler.scheduleJob(jobDetail, trigger);
	}

	/**
	 * 移除 job
	 * 
	 * @param id
	 */
	public void removeJob(String id) {
		try {
			ScheduleVo scheduleVo = getScheduleVo(id);
			scheduleVoMap.remove(id);
			if (scheduleVo.enabled) {
				JobKey jobKey = new JobKey(id, SCHEDULER_GROUP_NAME);
				scheduler.deleteJob(jobKey);
			}
		} catch (SchedulerException e) {

		}
	}

	private ScheduleVo getScheduleVo(String id) {
		if (scheduleVoMap.containsKey(id)) {
			return scheduleVoMap.get(id);
		} else {
			throw new RuntimeException(String.format("job id [%s] not found.", id));
		}
	}

	public void close() {
		try {
			scheduler.shutdown(false);
		} catch (Exception e) {
			logger.error("{}", e);
		}
	}

	public void start() {
		try {
			scheduler.start();
		} catch (Exception e) {
			logger.error("{}", e);
		}
	}

	private static class ScheduleVo {
		String id;
		String scheduled;
		Class<? extends ScheduledJob> jobClass;

		boolean enabled;
		boolean isRunning;

		public ScheduleVo(String id, String scheduled, Class<? extends ScheduledJob> jobClass, boolean enabled) {
			this.id = id;
			this.scheduled = scheduled;
			this.jobClass = jobClass;
			this.enabled = enabled;
			this.isRunning = false;
		}
	}
}
