package org.rpsingh.spring.batch;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.StepListener;
import org.springframework.batch.item.Chunk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class BatchWriteStepListener implements ItemWriteListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(StepListener.class);

    private final MongoTemplate mongoTemplate;

    @Autowired
    public BatchWriteStepListener(MongoTemplate jdbcTemplate) {
        this.mongoTemplate = jdbcTemplate;
    }

    @Override
    public void beforeWrite(Chunk items) {
        log.info("before write");
    }


    @Override
    public void afterWrite(Chunk items) {
        log.info("after write");
    }

    @Override
    public void onWriteError(Exception exception, Chunk items) {
        log.info("write error");
    }

}
