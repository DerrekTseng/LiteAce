package lite.app.scheduler;

import lite.core.scheduler.Scheduled;
import lite.core.scheduler.ScheduledJob;

@Scheduled(id = "TestJob", scheduled = "0 * * ? * * *")
public class TestJob extends ScheduledJob {

	@Override
	public void execute(String id, String[] args) {
		logger.info("job [" + id + "] is running.");
	}

}
