package personal.spring.batch.part2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SharedConfiguration {

	private Logger logger = LoggerFactory.getLogger(SharedConfiguration.class);

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	public SharedConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
	}

	@Bean
	public Job shareJob() {
		return jobBuilderFactory.get("shareJob")
				.incrementer(new RunIdIncrementer())
				.start(this.firstStep())
				.next(this.secondStep())
				.build();
	}

	@Bean
	public Step firstStep() {
		return stepBuilderFactory.get("firstStep").tasklet((contribution, chunkContext) -> {
			StepExecution stepExecution = contribution.getStepExecution();
			ExecutionContext stepExecutionContext = stepExecution.getExecutionContext();
			stepExecutionContext.putString("stepKey", "step execution context");

			JobExecution jobExecution = stepExecution.getJobExecution();
			JobInstance jobInstance = jobExecution.getJobInstance();

			ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
			jobExecutionContext.put("jobKey", "job execution context");
			JobParameters jobParameters = jobExecution.getJobParameters();

			logger.debug("jobName : {}, stepName : {}, run.id : {}", jobInstance.getJobName(),
					stepExecution.getStepName(), jobParameters.getLong("run.id"));
			return RepeatStatus.FINISHED;
		}).build();
	}

	@Bean
	public Step secondStep() {
		return stepBuilderFactory.get("secondStep").tasklet((contribution, chunkContext) -> {

			StepExecution stepExecution = contribution.getStepExecution();
			ExecutionContext stepExecutionContext = stepExecution.getExecutionContext();

			JobExecution jobExecution = stepExecution.getJobExecution();
			ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();

			// log
			logger.debug("jobValue : {}, stepValue : {}", jobExecutionContext.getString("jobKey", "emptyJob"),
					stepExecutionContext.getString("stepKey", "emptyStep"));

			return RepeatStatus.FINISHED;

		}).build();
	}
}
