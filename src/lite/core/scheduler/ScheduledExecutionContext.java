package lite.core.scheduler;

import java.util.Date;
import java.util.Map;

import org.quartz.Calendar;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.core.jmx.JobDataMapSupport;

/**
 * 實作 JobExecutionContext 用於手動執行 job 時，能將 job id 放進 jobDataMap 讓 程式碼讀取到
 * 
 * @author DerrekTseng
 *
 */
public class ScheduledExecutionContext implements JobExecutionContext {

	JobDataMap jobDataMap;

	public ScheduledExecutionContext(Map<String, Object> dataMap) {
		jobDataMap = JobDataMapSupport.newJobDataMap(dataMap);
	}

	@Override
	public Scheduler getScheduler() {

		return null;
	}

	@Override
	public Trigger getTrigger() {
		return null;
	}

	@Override
	public Calendar getCalendar() {

		return null;
	}

	@Override
	public boolean isRecovering() {

		return false;
	}

	@Override
	public TriggerKey getRecoveringTriggerKey() throws IllegalStateException {

		return null;
	}

	@Override
	public int getRefireCount() {

		return 0;
	}

	@Override
	public JobDataMap getMergedJobDataMap() {

		return null;
	}

	@Override
	public JobDetail getJobDetail() {

		return null;
	}

	@Override
	public Job getJobInstance() {

		return null;
	}

	@Override
	public Date getFireTime() {

		return null;
	}

	@Override
	public Date getScheduledFireTime() {

		return null;
	}

	@Override
	public Date getPreviousFireTime() {

		return null;
	}

	@Override
	public Date getNextFireTime() {

		return null;
	}

	@Override
	public String getFireInstanceId() {

		return null;
	}

	@Override
	public Object getResult() {

		return null;
	}

	@Override
	public void setResult(Object result) {

	}

	@Override
	public long getJobRunTime() {

		return 0;
	}

	@Override
	public void put(Object key, Object value) {

	}

	@Override
	public Object get(Object key) {
		return null;
	}

	public JobDataMap getJobDataMap() {
		return jobDataMap;
	}

}
