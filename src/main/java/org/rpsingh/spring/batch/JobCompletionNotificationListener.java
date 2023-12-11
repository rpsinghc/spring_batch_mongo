package org.rpsingh.spring.batch;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final MongoTemplate mongoTemplate;

    @Autowired
    public JobCompletionNotificationListener(MongoTemplate jdbcTemplate) {
        this.mongoTemplate = jdbcTemplate;
    }


    @Override
    public void beforeJob(JobExecution jobExecution) {
            log.info("mongo  : {}",mongoTemplate);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            LOGGER.info("!!! JOB FINISHED! Time to verify the results");

            String query = "SELECT brand, origin, characteristics FROM coffee";
            mongoTemplate.query(Coffee.class)
                .all().forEach(coffee -> LOGGER.info("Found < {} > in the database.", coffee));
        }
    }
}
