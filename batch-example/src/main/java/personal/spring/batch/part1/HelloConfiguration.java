package personal.spring.batch.part1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// @Configuration
public class HelloConfiguration {

	private Logger logger = LoggerFactory.getLogger(HelloConfiguration.class);

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	public HelloConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
	}

	@Bean
	public Job helloJob() {
		return jobBuilderFactory.get("hellJob").incrementer(new RunIdIncrementer()).start(this.helloStep()).build();
	}

	@Bean
	public Step helloStep() {
		return stepBuilderFactory.get("helloStep").tasklet((contribution, chunkContext) -> {
			logger.info("hello spring batch");
			return RepeatStatus.FINISHED;
		}).build();
	}

}
