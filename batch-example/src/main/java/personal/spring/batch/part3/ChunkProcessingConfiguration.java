package personal.spring.batch.part3;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// @Configuration
public class ChunkProcessingConfiguration {

	private Logger logger = LoggerFactory.getLogger(ChunkProcessingConfiguration.class);
	
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	public ChunkProcessingConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
	}
	
	@Bean
	public Job chunkProcessingJob() {
		return jobBuilderFactory.get("chunkProcessingJob")
				.incrementer(new RunIdIncrementer())
				.start(this.taskBaseStep())
				.build();
	}
	
	@Bean
	public Step taskBaseStep() {
		return stepBuilderFactory.get("chunkProcessingStep")
				.tasklet(this.tasklet())
				.build();
	}

	private Tasklet tasklet() {
		return (contribution, chunkContext) -> {
			List<String> item = getItems();
			logger.info("task item size : {}", item.size());
			return RepeatStatus.FINISHED;
			
		};
	}

	private List<String> getItems() {
		List<String> items = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			items.add(i + " Hello");
		}
		
		return items;
	}
}
