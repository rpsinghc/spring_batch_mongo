package org.rpsingh.spring.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.experimental.core.explore.support.MongoJobExplorerFactoryBean;
import org.springframework.batch.experimental.core.repository.support.MongoJobRepositoryFactoryBean;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;

@Configuration
@ConditionalOnProperty(prefix = "spring.data.mongodb", name = "uri", matchIfMissing = false)
@EnableAutoConfiguration(exclude = {org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class})
public class BatchConfigurationMongo {


    @Value("${spring.batch.mongo.collection.name}")
    private String collectionName;

    @Autowired
    void setMapKeyDotReplacement(MappingMongoConverter mappingMongoConverter) {
        mappingMongoConverter.setMapKeyDotReplacement("_");
    }

    @Bean
    public MongoItemWriter<Coffee> writer(MongoTemplate mongoTemplate) {
        MongoItemWriter<Coffee> mongoItemWriter = new MongoItemWriter<Coffee>();
        mongoItemWriter.setTemplate(mongoTemplate);
        mongoItemWriter.setCollection(collectionName);
        return mongoItemWriter;
    }


    @Bean
    public JobRepository jobRepository(MongoTemplate mongoTemplate, MongoTransactionManager transactionManager) throws Exception {
        MongoJobRepositoryFactoryBean jobRepositoryFactoryBean = new MongoJobRepositoryFactoryBean();
        jobRepositoryFactoryBean.setMongoOperations(mongoTemplate);
        jobRepositoryFactoryBean.setTransactionManager(transactionManager);
        jobRepositoryFactoryBean.afterPropertiesSet();
        return jobRepositoryFactoryBean.getObject();
    }

    @Bean
    public JobExplorer jobExplorer(MongoTemplate mongoTemplate, MongoTransactionManager transactionManager) throws Exception {
        MongoJobExplorerFactoryBean mongoJobExplorer = new MongoJobExplorerFactoryBean();
        mongoJobExplorer.setMongoOperations(mongoTemplate);
        mongoJobExplorer.setTransactionManager(transactionManager);
        mongoJobExplorer.afterPropertiesSet();
        return mongoJobExplorer.getObject();
    }


    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository){
        TaskExecutorJobLauncher executorJobLauncher = new TaskExecutorJobLauncher();
        executorJobLauncher.setJobRepository(jobRepository);
        return executorJobLauncher;
    }

    @Bean
    public JobLauncherApplicationRunner jobLauncherApplicationRunner(JobLauncher jobLauncher, JobExplorer jobExplorer,
                                                                     JobRepository jobRepository, BatchProperties properties) {
        JobLauncherApplicationRunner runner = new JobLauncherApplicationRunner(jobLauncher, jobExplorer, jobRepository);
        String jobNames = "importUserJob";//properties.getJob().getName();
        if (StringUtils.hasText(jobNames)) {
            runner.setJobName(jobNames);
        }
        return runner;
    }


    @Bean
    public MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    @Bean
    public Job importUserJob(JobRepository jobRepository, JobCompletionNotificationListener listener, Step step1) {
        return new JobBuilder("importUserJob", jobRepository).incrementer(new RunIdIncrementer()).listener(listener).flow(step1).end().build();
    }


    @Bean
    public MapJobRegistry jobRegistry(){
        return new MapJobRegistry();
    }

   @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager, ItemWriter<Coffee> writer, CoffeeItemProcessor processor, FlatFileItemReader reader) {
        return new StepBuilder("step1", jobRepository).<Coffee,Coffee>chunk(1, transactionManager).reader(reader).processor(processor).writer(writer).build();
    }


}
