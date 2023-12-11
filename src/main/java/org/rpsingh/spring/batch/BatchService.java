package org.rpsingh.spring.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Ram Prakash Singh
 */


@Slf4j
@Service
public class BatchService {

    @Autowired
    JobLauncher jobLauncher;

    //@Autowired
    //Job job;

    @Autowired
    JobExplorer jobExplorer;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    JobRegistry jobRegistry;

    @Autowired
    BatchProperties properties;

    @Autowired
    JobCompletionNotificationListener listener;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    ItemWriter<Coffee> writer;

    @Autowired
    CoffeeItemProcessor processor;


    @Value("classpath:coffee-list.json")
    Resource resource;

    @Autowired
    BatchReadStepListener batchReadStepListener;

    @Autowired
    BatchWriteStepListener batchWriteStepListener;

    @Autowired
    BatchProcessStepListener batchProcessStepListener;

    public Job createJob(String jobName) {
        if (jobRegistry != null) {
            try {
                Job current = jobRegistry.getJob(jobName);
                return current;
            } catch (NoSuchJobException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }


    public void startJobLaunch() throws Exception {
        JobLauncherApplicationRunner runner = jobLauncherApplicationRunner(jobLauncher, jobExplorer, jobRepository, properties);
        runner.run(new DefaultApplicationArguments());
    }

    public JobLauncherApplicationRunner jobLauncherApplicationRunner(JobLauncher jobLauncher, JobExplorer jobExplorer, JobRepository jobRepository, BatchProperties properties) {
        JobLauncherApplicationRunner runner = new JobLauncherApplicationRunner(jobLauncher, jobExplorer, jobRepository);
        String jobNames = "importUserJob";//properties.getJob().getName();
        if (StringUtils.hasText(jobNames)) {
            runner.setJobName(jobNames);
        }

       // runner.setJobs(List.of(job));
        return runner;
    }

    public void launchJob() throws JobExecutionException, IOException {
        Job job = createJob("cofeeImportJob", "step1", resource.getInputStream());
        jobLauncher.run(job, new JobParameters());
    }


    public Job createJob(String jobName, String stepName, InputStream inputStream) {
        Step step = step(stepName, inputStream, Coffee.class);
        Job job = buildJob(jobName, step);
        return job;
    }

    public <T> JsonItemReader<T> readerJson(String jsonReaderName, InputStream inputStream, Class<T> itemType) {
        return new JsonItemReaderBuilder<T>().jsonObjectReader(new JacksonJsonObjectReader<>(itemType)).resource(new InputStreamResource(inputStream)).name(jsonReaderName).build();
    }

    public <T> Step step(String stepName, InputStream inputStream, Class<T> itemType) {
        ItemReader reader = readerJson(stepName + "ItemReader", inputStream, itemType);
        return new StepBuilder(stepName, jobRepository).<T,T>chunk(1, transactionManager).listener(batchReadStepListener).listener(batchProcessStepListener).listener(
                batchWriteStepListener).reader(reader).processor(processor).writer(writer).build();
    }

    public Job buildJob(String jobName, Step step) {
        return new JobBuilder(jobName, jobRepository).incrementer(new RunIdIncrementer()).listener(listener).flow(step).end().build();
    }
}
